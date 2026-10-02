package com.soldierskull.eclipse.network;

import com.soldierskull.eclipse.stats.AttributeType;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import com.soldierskull.eclipse.stats.StatAttributeHandler;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class PacketAddStat {

    private final AttributeType attribute;

    public PacketAddStat(AttributeType attribute) {
        this.attribute = attribute;
    }

    public static void encode(PacketAddStat msg, PacketBuffer buffer) {
        buffer.writeEnum(msg.attribute);
    }

    public static PacketAddStat decode(PacketBuffer buffer) {
        return new PacketAddStat(buffer.readEnum(AttributeType.class));
    }

    public static void handle(PacketAddStat msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                if (stats.addPointTo(msg.attribute)) {
                    StatAttributeHandler.apply(player, stats);
                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new PacketSyncStats(stats));
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}