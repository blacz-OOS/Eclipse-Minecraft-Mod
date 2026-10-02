package com.soldierskull.eclipse.ritual.requirement;

import com.soldierskull.eclipse.ritual.RitualContext;
import com.soldierskull.eclipse.ritual.RitualRequirement;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

/**
 * Exige nível mínimo de facção (independente de qual facção - combine
 * com {@link FactionRequirement} pra travar facção + nível juntos, ex.:
 * "só Cultista level máximo" no Eclipse Ritual - decisão confirmada).
 */
public class FactionLevelRequirement implements RitualRequirement {

    private final int minLevel;

    public FactionLevelRequirement(int minLevel) {
        this.minLevel = minLevel;
    }

    @Override
    public boolean test(RitualContext context) {
        if (context.getInitiator() == null) {
            return false;
        }
        MinecraftServer server = context.getWorld().getServer();
        if (server == null) {
            return false;
        }
        ServerPlayerEntity player = server.getPlayerList().getPlayer(context.getInitiator());
        if (player == null) {
            return false;
        }
        return player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                .map(stats -> stats.getFactionLevel() >= this.minLevel)
                .orElse(false);
    }

    @Override
    public boolean requiresInitiator() {
        return true;
    }

    @Override
    public String describe() {
        return "Precisa de nível de facção " + this.minLevel + "+";
    }
}
