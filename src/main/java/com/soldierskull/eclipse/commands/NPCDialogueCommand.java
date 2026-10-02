package com.soldierskull.eclipse.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import com.soldierskull.eclipse.entity.npc.dialogue.NPCDialogue;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Comando escondido usado pelo motor de diálogo (Etapa 3) - dispara
 * quando o jogador clica numa opção de diálogo do chat. Precisa ser um
 * comando TOP-LEVEL separado de {@code /eclipse} de propósito: a raiz
 * de {@code /eclipse} exige permissão 2 (OP), mas o clique numa opção
 * de diálogo de NPC tem que funcionar pra qualquer jogador normal.
 *
 * Não é pra ser digitado manualmente (por isso o nome pouco óbvio),
 * mas nada impede - na pior das hipóteses, alguém "trapaceia" reabrindo
 * uma opção de diálogo que já tinha visto, o que não é diferente de
 * clicar de novo no NPC.
 */
public final class NPCDialogueCommand {

    private NPCDialogueCommand() {
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("eclipsenpc")
                        .then(Commands.argument("npcId", StringArgumentType.word())
                                .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                        .executes(context -> {
                                            ServerPlayerEntity player;
                                            try {
                                                player = context.getSource().getPlayerOrException();
                                            } catch (Exception e) {
                                                return 0;
                                            }
                                            String npcId = StringArgumentType.getString(context, "npcId");
                                            int index = IntegerArgumentType.getInteger(context, "index");
                                            NPCDialogue.handleOption(player, npcId, index);
                                            return 1;
                                        })))
        );
    }
}
