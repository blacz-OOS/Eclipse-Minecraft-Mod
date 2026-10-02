package com.soldierskull.eclipse.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.soldierskull.eclipse.network.PacketHandler;
import com.soldierskull.eclipse.network.PacketSyncStats;
import com.soldierskull.eclipse.stats.PlayerStats;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Shared plumbing used by every /eclipse subcommand: fetching the caller's
 * PlayerStats capability and pushing a fresh sync packet to their client
 * afterwards (same pattern ServerEvents.syncTo already uses elsewhere).
 */
public final class CommandUtil {

    private static final SimpleCommandExceptionType NO_PLAYER_STATS =
            new SimpleCommandExceptionType(new StringTextComponent("Este jogador nao possui PlayerStats (capability ausente)."));

    private CommandUtil() {
    }

    /** Fetches the command sender as a player and throws a friendly error if run from console/command block. */
    public static ServerPlayerEntity getPlayer(CommandSource source) throws CommandSyntaxException {
        return source.getPlayerOrException();
    }

    /**
     * Fetches the explicit <target> player argument every /eclipse subcommand
     * now takes (e.g. "/eclipse energy set <target> <valor>"), instead of
     * always applying to whoever ran the command.
     */
    public static ServerPlayerEntity getTarget(CommandContext<CommandSource> ctx, String argName) throws CommandSyntaxException {
        return EntityArgument.getPlayer(ctx, argName);
    }

    /** Fetches the PlayerStats capability for a player, throwing if somehow absent. */
    public static PlayerStats getStats(ServerPlayerEntity player) throws CommandSyntaxException {
        return player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                .orElseThrow(NO_PLAYER_STATS::create);
    }

    /** Pushes the player's current PlayerStats to their client - call after any command that changes a value. */
    public static void sync(ServerPlayerEntity player, PlayerStats stats) {
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncStats(stats));
    }
}
