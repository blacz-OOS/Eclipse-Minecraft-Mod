package com.soldierskull.eclipse.ritual.effect;

import com.soldierskull.eclipse.ritual.RitualContext;
import com.soldierskull.eclipse.ritual.RitualEffect;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

/** Dá XP de facção ao iniciador - usado por {@code Cultist Initiation} (Bloco E, progressão de facção, não transformação de raça). */
public class AddFactionXpEffect implements RitualEffect {

    private final int amount;

    public AddFactionXpEffect(int amount) {
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
                stats.addFactionXp(this.amount));
    }
}
