package com.soldierskull.eclipse.ritual.effect;

import com.soldierskull.eclipse.ritual.RitualContext;
import com.soldierskull.eclipse.ritual.RitualEffect;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

/**
 * Remove Corruption do jogador que iniciou o ritual - usado pelo
 * {@code channel_power} (Bloco F/Ritual 0), primeira etapa do pipeline
 * Corruption -> Channel Power -> Ritual Power -> Altar -> Ritual. A
 * metade "dar Ritual Power ao altar" é o {@link AddRitualPowerEffect},
 * separado de propósito: cada efeito faz uma coisa só.
 */
public class ConsumeCorruptionEffect implements RitualEffect {

    private final int amount;

    public ConsumeCorruptionEffect(int amount) {
        this.amount = amount;
    }

    @Override
    public void apply(RitualContext context) {
        if (context.getInitiator() == null) {
            return;
        }
        MinecraftServer server = context.getWorld().getServer();
        if (server == null) {
            return;
        }
        ServerPlayerEntity player = server.getPlayerList().getPlayer(context.getInitiator());
        if (player == null) {
            return;
        }
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats ->
                stats.removeCorruption(this.amount));
    }
}
