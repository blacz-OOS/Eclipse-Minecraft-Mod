package com.soldierskull.eclipse.network;

import java.util.function.Supplier;

import com.soldierskull.eclipse.skills.SkillManager;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Cliente -> servidor: "quero gastar ponto pra desbloquear esta skill".
 * Irmão gêmeo de {@link PacketActivateSkill} - mesma estrutura, só troca
 * activate() por unlock(). Usado pela SkillTreeScreen (clique num nó
 * disponível); a validação real (raça/facção, nível, pré-requisito,
 * pontos) acontece inteira no servidor via SkillManager.unlock(), a
 * tela nunca decide se o desbloqueio vale - só evita visualmente que o
 * jogador clique num nó obviamente inválido.
 */
public class PacketUnlockSkill {

    private final String skillId;

    public PacketUnlockSkill(String skillId) {
        this.skillId = skillId;
    }

    public static void encode(PacketUnlockSkill msg, PacketBuffer buffer) {
        buffer.writeUtf(msg.skillId);
    }

    public static PacketUnlockSkill decode(PacketBuffer buffer) {
        return new PacketUnlockSkill(buffer.readUtf());
    }

    public static void handle(PacketUnlockSkill msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                SkillManager.Result result = SkillManager.unlock(player, stats, msg.skillId);
                // Antes o resultado era descartado: clicar num nó bloqueado e num
                // desbloqueado davam o mesmo feedback (nenhum). Agora o jogador vê o
                // motivo na action bar.
                player.displayClientMessage(new StringTextComponent(describe(result)), true);
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncStats(stats));
            });
        });
        ctx.get().setPacketHandled(true);
    }

    /** Texto curto pra action bar - cada Result do SkillManager.unlock() tem um motivo distinto. */
    private static String describe(SkillManager.Result result) {
        switch (result) {
            case OK: return "Habilidade desbloqueada!";
            case JA_DESBLOQUEADA: return "Você já desbloqueou esta habilidade.";
            case NIVEL_INSUFICIENTE: return "Nível insuficiente para esta habilidade.";
            case PONTOS_INSUFICIENTES: return "Pontos de habilidade insuficientes.";
            case PRE_REQUISITO_FALTANDO: return "Desbloqueie os pré-requisitos primeiro.";
            case DONO_INCOMPATIVEL: return "Esta habilidade não pertence à sua raça/facção.";
            case SKILL_INEXISTENTE: return "Habilidade inexistente.";
            default: return "Não foi possível desbloquear.";
        }
    }
}
