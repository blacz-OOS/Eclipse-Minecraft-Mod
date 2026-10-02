package com.soldierskull.eclipse.network;

import java.util.function.Supplier;

import com.soldierskull.eclipse.skills.SkillManager;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Cliente -> servidor: "quero ativar a skill com este id". Usado pela
 * keybind "Ataque Básico" (ver client/KeyInit.java / event/ClientEvents.java)
 * e é o ponto de entrada genérico pra qualquer gatilho futuro (outro
 * keybind, item, GUI de árvore de habilidades) - toda a validação real
 * (raça/facção, desbloqueada, cooldown, energia) acontece no servidor via
 * SkillManager.activate(), nunca confiando no que o cliente mandou além
 * do id.
 */
public class PacketActivateSkill {

    private final String skillId;

    public PacketActivateSkill(String skillId) {
        this.skillId = skillId;
    }

    public static void encode(PacketActivateSkill msg, PacketBuffer buffer) {
        buffer.writeUtf(msg.skillId);
    }

    public static PacketActivateSkill decode(PacketBuffer buffer) {
        return new PacketActivateSkill(buffer.readUtf());
    }

    public static void handle(PacketActivateSkill msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                SkillManager.activate(player, stats, msg.skillId);
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncStats(stats));
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
