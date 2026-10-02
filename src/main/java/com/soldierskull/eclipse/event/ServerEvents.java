package com.soldierskull.eclipse.event;

import com.soldierskull.eclipse.network.PacketHandler;
import com.soldierskull.eclipse.network.PacketSyncStats;
import com.soldierskull.eclipse.skills.BuffManager;
import com.soldierskull.eclipse.skills.DetectionEvents;
import com.soldierskull.eclipse.skills.EclipseEventManager;
import com.soldierskull.eclipse.skills.MovementEvents;
import com.soldierskull.eclipse.skills.SkillAttributeHandler;
import com.soldierskull.eclipse.skills.SummonRegistry;
import com.soldierskull.eclipse.skills.TrapRegistry;
import com.soldierskull.eclipse.stats.PlayerStats;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import com.soldierskull.eclipse.stats.StatAttributeHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = "eclipse")
public class ServerEvents {

    private static final int REGEN_TICK_INTERVAL = 20; // once per second

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity
                && !event.getObject().getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).isPresent()) {
            event.addCapability(new ResourceLocation("eclipse", "player_stats"), (ICapabilityProvider) new PlayerStatsProvider());
        }
    }

    @SubscribeEvent
    public static void onMobKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getSource().getEntity();
            LivingEntity victim = event.getEntityLiving();
            if (victim instanceof IMob) {
                int xpGained = (int) victim.getMaxHealth();
                player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                    stats.addXp(xpGained);
                    syncTo(player, stats);
                });
            }
        }
    }

    /**
     * Carries every stat over from the old player instance to the new one
     * on death, so nothing resets. Required by the persistence requirement
     * in the spec ("after death, all values must remain unchanged").
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(oldStats ->
                    event.getPlayer().getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(newStats ->
                            newStats.copyFrom(oldStats)));
        }
    }

    /** Attribute modifiers are not saved to disk (only the raw point counts are), so re-apply on login. */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                StatAttributeHandler.apply(player, stats);
                SkillAttributeHandler.reapplyAll(player, stats);
                syncTo(player, stats);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                StatAttributeHandler.apply(player, stats);
                SkillAttributeHandler.reapplyAll(player, stats);
                syncTo(player, stats);
            });
        }
    }

    /** Handles Constitution's HP regen and Energy's Common Energy regen, once per second. */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.player instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) event.player;

        MovementEvents.tickFogForm(player); // precisa ser todo tick, nao 1x/segundo (ver comentario na classe)

        if (player.tickCount % REGEN_TICK_INTERVAL != 0) {
            return;
        }
        BuffManager.tick(player);
        SummonRegistry.tick(); // global, so 1 call e suficiente mesmo com varios players online (chamado de novo nao tem custo, so verifica um mapa vazio na maioria das vezes)
        TrapRegistry.tick(); // global
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            DetectionEvents.tick(player, stats);
            boolean maskBonusChanged = com.soldierskull.eclipse.item.CultistMaskHandler.tick(player, stats);
            com.soldierskull.eclipse.transformation.TransformationManager.tick(player, stats);
            boolean changed = maskBonusChanged;
            if (player.isAlive() && player.getHealth() < player.getMaxHealth()) {
                float healAmount = stats.tickHpRegenAndGetHealAmount();
                if (healAmount > 0F) {
                    player.heal(healAmount);
                    changed = true;
                }
            }
            int beforeCommonEnergy = stats.getCommonEnergy();
            stats.tickCommonEnergyRegen();
            if (stats.getCommonEnergy() != beforeCommonEnergy) {
                changed = true;
            }
            if (changed) {
                syncTo(player, stats);
            }
        });
    }

    /** Eventos mundiais nao podem depender da quantidade de jogadores online
     * (o tick antigo rodava dentro do loop por jogador, entao um mundo com
     * 2 jogadores avancava o Eclipse 2x mais rapido, e um mundo sem ninguem
     * online nao avancava nada). */
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.world instanceof net.minecraft.world.server.ServerWorld) {
            EclipseEventManager.tick((net.minecraft.world.server.ServerWorld) event.world);
        }
    }

    private static void syncTo(ServerPlayerEntity player, PlayerStats stats) {
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncStats(stats));
    }
}
