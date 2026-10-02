package com.soldierskull.eclipse.network;

import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Client -> server request to spend Corruption (Corruption) to
 * permanently raise the player's Common Energy MAXIMUM (see
 * StatBalance.CORRUPTION_TO_COMMON_COST / CORRUPTION_TO_COMMON_MAX_GAIN).
 * Triggered by the "Converter" button on the Status Screen.
 */
public class PacketConvertEnergy {

    public PacketConvertEnergy() {
    }

    public static void encode(PacketConvertEnergy msg, PacketBuffer buffer) {
        // no payload needed
    }

    public static PacketConvertEnergy decode(PacketBuffer buffer) {
        return new PacketConvertEnergy();
    }

    public static void handle(PacketConvertEnergy msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                if (stats.convertAbyssalToCommon()) {
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new PacketSyncStats(stats));
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
