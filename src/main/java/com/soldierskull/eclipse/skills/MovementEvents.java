package com.soldierskull.eclipse.skills;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Efeitos de movimento que precisam de eventos próprios (não cabem em
 * BuffManager nem CombatEvents):
 *   - Salto Predatório (Lobisomem): dano em área ao aterrissar.
 *   - Forma de Névoa (Vampiro): noclip temporário + dano ao atravessar mobs.
 */
@Mod.EventBusSubscriber(modid = "eclipse")
public class MovementEvents {

    private static final Set<UUID> PENDING_PREDATORY_JUMP = new HashSet<>();
    /** jogadorId -> epoch millis em que a Forma de Névoa acaba (também usado pelo tick de dano-ao-atravessar). */
    private static final Map<UUID, Long> FOG_FORM_END = new HashMap<>();

    private MovementEvents() {
    }

    // ---- Salto Predatório ----

    public static void markPredatoryJump(UUID playerId) {
        PENDING_PREDATORY_JUMP.add(playerId);
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!(entity instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (!PENDING_PREDATORY_JUMP.remove(player.getUUID())) {
            return;
        }

        event.setCanceled(true); // o salto nao deveria machucar quem pulou

        List<LivingEntity> nearby = player.level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(2.5D), e -> e != player && e.isAlive());
        for (LivingEntity target : nearby) {
            target.hurt(DamageSource.playerAttack(player), 3.0F);
        }
    }

    // ---- Forma de Névoa ----

    /** Ativa o noclip por `durationMillis` (chamado por VampireSkillEffects.FORMA_DE_NEVOA). */
    public static void startFogForm(ServerPlayerEntity player, long durationMillis) {
        player.noPhysics = true;
        FOG_FORM_END.put(player.getUUID(), System.currentTimeMillis() + durationMillis);
    }

    /** Chamado a cada tick do jogador (ver ServerEvents.onPlayerTick) - precisa ser todo tick, não 1x/segundo, pra pegar a colisão com mobs a tempo. */
    public static void tickFogForm(ServerPlayerEntity player) {
        Long end = FOG_FORM_END.get(player.getUUID());
        if (end == null) {
            return;
        }
        if (System.currentTimeMillis() >= end) {
            player.noPhysics = false;
            FOG_FORM_END.remove(player.getUUID());
            return;
        }

        List<LivingEntity> overlapping = player.level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox(), e -> e != player && e.isAlive());
        for (LivingEntity target : overlapping) {
            // sem cooldown por-alvo aqui: em ticks de 20/s, atravessar rapido ja limita
            // naturalmente quantos golpes acontecem: aceitavel como aproximacao.
            target.hurt(DamageSource.playerAttack(player), 2.5F);
        }
    }

    public static boolean isInFogForm(UUID playerId) {
        return FOG_FORM_END.containsKey(playerId);
    }
}
