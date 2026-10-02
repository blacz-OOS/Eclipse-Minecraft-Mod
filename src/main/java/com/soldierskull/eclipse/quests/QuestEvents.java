package com.soldierskull.eclipse.quests;

import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "eclipse")
public class QuestEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) event.getSource().getEntity();

        String entityTypeId = Registry.ENTITY_TYPE.getKey(event.getEntityLiving().getType()).toString();

        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats ->
                QuestManager.onMobKilled(stats, entityTypeId));
    }
}
