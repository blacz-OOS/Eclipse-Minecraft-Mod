package com.soldierskull.eclipse.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.soldierskull.eclipse.item.SilverWeapon;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Passivas de combate calculadas AO VIVO em cada golpe (sem precisar de
 * AttributeModifier) - mais simples e sem risco de ficar "preso" um
 * modificador por bug de remoção. Cobre só as skills fraseadas como
 * "reduz/aumenta O DANO" diretamente; skills fraseadas como "aumenta
 * RESISTÊNCIA em Nx" viram multiplicador na attribute ARMOR (ver
 * SkillAttributeHandler/BuffManager) e reaproveitam a fórmula de
 * armadura do próprio Minecraft, então não duplicam lógica aqui.
 *
 * Valores sem número exato no documento (ex.: bônus de "Golpe Preciso")
 * usam um placeholder claramente marcado - ajuste quando definir o
 * número real.
 */
@Mod.EventBusSubscriber(modid = "eclipse")
public class CombatEvents {

    /** Golpe Preciso: próximo golpe corpo a corpo bonificado. skillId -> epoch millis em que a janela expira. Não persistido (janela de 5s). */
    private static final Map<UUID, Long> GOLPE_PRECISO_WINDOW = new HashMap<>();
    /** PLACEHOLDER: bônus de dano do Golpe Preciso não tem valor numérico definido no documento. */
    private static final float GOLPE_PRECISO_BONUS_DAMAGE = 3.0F;

    public static void armGolpePreciso(UUID playerId, long windowMillis) {
        GOLPE_PRECISO_WINDOW.put(playerId, System.currentTimeMillis() + windowMillis);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntityLiving();
        LivingEntity attacker = event.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) event.getSource().getEntity() : null;

        // ---- Dano RECEBIDO pelo jogador (vitima) ----
        if (victim instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) victim;
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                float amount = event.getAmount();

                if (stats.hasUnlockedSkill("vampiro_sangue_espesso")) {
                    amount *= (2F / 3F); // "reduz 1/3 do dano recebido"
                }
                if (stats.hasUnlockedSkill("lobisomem_resistencia_bestial")) {
                    amount *= 0.75F;
                }

                event.setAmount(amount);
            });
        }

        // ---- Flecha especial dos Caçadores (Munição Especial) - independe de quem atirou ter capability ----
        net.minecraft.entity.Entity directSource = event.getSource().getDirectEntity();
        if (directSource != null && directSource.getPersistentData().getBoolean("eclipse_hunter_special_arrow")
                && SupernaturalEntities.isSupernatural(victim)) {
            event.setAmount(event.getAmount() + 3.0F);
        }

        // ---- Dano CAUSADO pelo jogador (atacante) ----
        if (attacker instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) attacker;
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                float amount = event.getAmount();

                if (stats.hasUnlockedSkill("lobisomem_garras_bestiais")) {
                    amount += 0.5F;
                }
                if (stats.hasUnlockedSkill("cacadores_treinamento_com_prata")
                        && player.getMainHandItem().getItem() instanceof SilverWeapon
                        && SupernaturalEntities.isSupernatural(victim)) {
                    amount *= 1.25F; // "aumenta a eficiencia" - valor nao especificado, placeholder +25%
                }
                if (stats.hasUnlockedSkill("cacadores_conhecimento_anatomico") && SupernaturalEntities.isSupernatural(victim)) {
                    amount *= 1.15F; // "+15% de dano contra criaturas sobrenaturais"
                }

                Long window = GOLPE_PRECISO_WINDOW.get(player.getUUID());
                if (window != null && System.currentTimeMillis() < window) {
                    amount += GOLPE_PRECISO_BONUS_DAMAGE;
                    GOLPE_PRECISO_WINDOW.remove(player.getUUID()); // consome no primeiro golpe, como descrito no documento
                }

                event.setAmount(amount);

                // Regeneração Bestial: cura 1 HP ao acertar um golpe (aproximação de
                // "regenera 1 por segundo enquanto bate" - ver nota em WerewolfSkillEffects).
                if (stats.hasUnlockedSkill("lobisomem_regeneracao_bestial") && player.isAlive()) {
                    player.heal(1.0F);
                }
            });
        }
    }
}
