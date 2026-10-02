package com.soldierskull.eclipse.ritual.requirement;

import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.ritual.RitualContext;
import com.soldierskull.eclipse.ritual.RitualRequirement;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

/** Exige que o iniciador pertença a uma facção específica ({@code FactionType}). */
public class FactionRequirement implements RitualRequirement {

    private final FactionType required;

    public FactionRequirement(FactionType required) {
        this.required = required;
    }

    @Override
    public boolean test(RitualContext context) {
        if (context.getInitiator() == null) {
            return false; // sondagem sem jogador definido - nao avaliavel ainda
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
                .map(stats -> stats.getFaction() == this.required)
                .orElse(false);
    }

    @Override
    public boolean requiresInitiator() {
        return true;
    }

    @Override
    public String describe() {
        return "Precisa pertencer a facção: " + this.required.getDisplayName();
    }
}
