package com.soldierskull.eclipse.commands.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.soldierskull.eclipse.commands.CommandUtil;
import com.soldierskull.eclipse.quests.Quest;
import com.soldierskull.eclipse.quests.QuestManager;
import com.soldierskull.eclipse.quests.QuestObjective;
import com.soldierskull.eclipse.quests.QuestRegistry;
import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

/**
 * /eclipse quest list <target>
 * /eclipse quest accept <target> <questId>
 * /eclipse quest complete <target> <questId>
 * /eclipse quest abandon <target> <questId>
 *
 * Ponto de entrada de teste/admin - o jeito "de verdade" do jogador
 * aceitar/entregar quest (livro, NPC) é Fase 12/14. QuestManager já faz
 * toda a validação real (requisitos, progresso, objetivos), então este
 * comando só chama a mesma lógica que qualquer outra entrada vai usar.
 */
public class QuestCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("quest")

                .then(
                        Commands.literal("list")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .executes(context -> {

                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    PlayerStats stats = CommandUtil.getStats(player);

                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append("Ativas: ").append(stats.getActiveQuests()).append("\n");
                                                    sb.append("Completas: ").append(stats.getCompletedQuests()).append("\n");
                                                    sb.append("Disponiveis:\n");
                                                    for (Quest quest : QuestRegistry.getAll()) {
                                                        boolean available = quest.meetsRequirements(stats)
                                                                && !stats.hasActiveQuest(quest.getId())
                                                                && (!stats.hasCompletedQuest(quest.getId()) || quest.isRepeatable());
                                                        sb.append(" - ").append(quest.getId()).append(" \"").append(quest.getDisplayName()).append("\"");
                                                        if (!available) {
                                                            sb.append(" (indisponivel: requisito/ja ativa/ja completa)");
                                                        }
                                                        for (int i = 0; i < quest.getObjectives().size(); i++) {
                                                            QuestObjective objective = quest.getObjectives().get(i);
                                                            sb.append("\n     * ").append(objective.getDescription());
                                                            if (stats.hasActiveQuest(quest.getId())) {
                                                                sb.append(" [").append(stats.getQuestObjectiveProgress(quest.getId(), i)).append("/").append(objective.getRequiredAmount()).append("]");
                                                            }
                                                        }
                                                        sb.append("\n");
                                                    }

                                                    context.getSource().sendSuccess(new StringTextComponent(sb.toString()), false);
                                                    return 1;

                                                })
                                )
                )

                .then(
                        Commands.literal("accept")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .then(
                                                        Commands.argument("questId", StringArgumentType.word())
                                                                .executes(context -> {

                                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                                    String questId = StringArgumentType.getString(context, "questId");
                                                                    PlayerStats stats = CommandUtil.getStats(player);

                                                                    QuestManager.AcceptResult result = QuestManager.accept(stats, questId);
                                                                    CommandUtil.sync(player, stats);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent("Accept '" + questId + "' para " + player.getName().getString() + " = " + result),
                                                                            true);
                                                                    return result == QuestManager.AcceptResult.OK ? 1 : 0;

                                                                })
                                                )
                                )
                )

                .then(
                        Commands.literal("complete")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .then(
                                                        Commands.argument("questId", StringArgumentType.word())
                                                                .executes(context -> {

                                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                                    String questId = StringArgumentType.getString(context, "questId");
                                                                    PlayerStats stats = CommandUtil.getStats(player);

                                                                    QuestManager.CompleteResult result = QuestManager.complete(player, stats, questId);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent("Complete '" + questId + "' para " + player.getName().getString() + " = " + result),
                                                                            true);
                                                                    return result == QuestManager.CompleteResult.OK ? 1 : 0;

                                                                })
                                                )
                                )
                )

                .then(
                        Commands.literal("abandon")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .then(
                                                        Commands.argument("questId", StringArgumentType.word())
                                                                .executes(context -> {

                                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                                    String questId = StringArgumentType.getString(context, "questId");
                                                                    PlayerStats stats = CommandUtil.getStats(player);

                                                                    stats.abandonQuest(questId);
                                                                    CommandUtil.sync(player, stats);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent("Quest '" + questId + "' abandonada por " + player.getName().getString()),
                                                                            true);
                                                                    return 1;

                                                                })
                                                )
                                )
                );

    }

}
