package com.soldierskull.eclipse.network;

import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import com.soldierskull.eclipse.stats.StatAttributeHandler;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Client -> server request for a fresh, authoritative PacketSyncStats.
 * Sent when the Status Screen is opened, so it always shows up-to-date
 * values immediately instead of relying only on the next event-driven
 * sync (login, respawn, point spent, energy change, etc.).
 */
public class PacketRequestStats {

    public PacketRequestStats() {
    }

    public static void encode(PacketRequestStats msg, PacketBuffer buffer) {
        // no payload needed
    }

    public static PacketRequestStats decode(PacketBuffer buffer) {
        return new PacketRequestStats();
    }

    public static void handle(PacketRequestStats msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                // Re-apply real attributes too, in case the player logged in
                // through a path that skipped it (defensive, cheap to redo).
                StatAttributeHandler.apply(player, stats);
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                        new PacketSyncStats(stats));
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
