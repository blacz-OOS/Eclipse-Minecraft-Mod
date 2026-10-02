package com.soldierskull.eclipse.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.soldierskull.eclipse.commands.CommandUtil;
import com.soldierskull.eclipse.skills.Skill;
import com.soldierskull.eclipse.skills.SkillManager;
import com.soldierskull.eclipse.skills.SkillTree;
import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

/**
 * /eclipse skill points set|add|remove <target> <valor>
 * /eclipse skill unlock <target> <skillId>
 * /eclipse skill activate <target> <skillId>
 * /eclipse skill list <target>
 *
 * "unlock"/"activate" passam por SkillManager, então toda a validação
 * (raça, nível, pontos, pré-requisitos, cooldown, energia) roda igual a
 * como rodaria num uso real em jogo - este comando é o jeito de testar a
 * árvore de habilidades sem precisar de um item/GUI ainda.
 */
public class SkillCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("skill")

                .then(
                        Commands.literal("points")
                                .then(buildPoints("set", (stats, valor) -> { stats.removeSkillPoints(stats.getSkillPoints()); stats.addSkillPoints(valor); }))
                                .then(buildPoints("add", PlayerStats::addSkillPoints))
                                .then(buildPoints("remove", PlayerStats::removeSkillPoints))
                )

                .then(
                        Commands.literal("factionpoints")
                                .then(buildFactionPoints("set", (stats, valor) -> { stats.removeFactionSkillPoints(stats.getFactionSkillPoints()); stats.addFactionSkillPoints(valor); }))
                                .then(buildFactionPoints("add", PlayerStats::addFactionSkillPoints))
                                .then(buildFactionPoints("remove", PlayerStats::removeFactionSkillPoints))
                )

                .then(
                        Commands.literal("unlock")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .then(
                                                        Commands.argument("skillId", StringArgumentType.word())
                                                                .executes(context -> {

                                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                                    String skillId = StringArgumentType.getString(context, "skillId");
                                                                    PlayerStats stats = CommandUtil.getStats(player);

                                                                    SkillManager.Result result = SkillManager.unlock(player, stats, skillId);
                                                                    CommandUtil.sync(player, stats);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent("Unlock '" + skillId + "' para " + player.getName().getString() + " = " + result),
                                                                            true);

                                                                    return result == SkillManager.Result.OK ? 1 : 0;

                                                                })
                                                )
                                )
                )

                .then(
                        Commands.literal("activate")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .then(
                                                        Commands.argument("skillId", StringArgumentType.word())
                                                                .executes(context -> {

                                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                                    String skillId = StringArgumentType.getString(context, "skillId");
                                                                    PlayerStats stats = CommandUtil.getStats(player);

                                                                    SkillManager.Result result = SkillManager.activate(player, stats, skillId);
                                                                    CommandUtil.sync(player, stats);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent("Activate '" + skillId + "' para " + player.getName().getString() + " = " + result),
                                                                            true);

                                                                    return result == SkillManager.Result.OK ? 1 : 0;

                                                                })
                                                )
                                )
                )

                .then(
                        Commands.literal("list")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .executes(context -> {

                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    PlayerStats stats = CommandUtil.getStats(player);

                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append("Pontos raciais: ").append(stats.getSkillPoints())
                                                            .append(" | Pontos de faccao: ").append(stats.getFactionSkillPoints())
                                                            .append(" | Desbloqueadas: ").append(stats.getUnlockedSkills()).append("\n");

                                                    sb.append("Raca (").append(stats.getRace().getDisplayName()).append("):\n");
                                                    for (Skill skill : SkillTree.getAllForRace(stats.getRace())) {
                                                        sb.append(" - ").append(skill.getId())
                                                                .append(" [").append(skill.getType()).append("]")
                                                                .append(" nivel>=").append(skill.getRequiredLevel())
                                                                .append(" custo=").append(skill.getPointCost())
                                                                .append(stats.hasUnlockedSkill(skill.getId()) ? " (DESBLOQUEADA)" : "")
                                                                .append("\n");
                                                    }

                                                    sb.append("Faccao (").append(stats.getFaction().getDisplayName()).append("):\n");
                                                    for (Skill skill : SkillTree.getAllForFaction(stats.getFaction())) {
                                                        sb.append(" - ").append(skill.getId())
                                                                .append(" [").append(skill.getType()).append("]")
                                                                .append(" nivel>=").append(skill.getRequiredLevel())
                                                                .append(" custo=").append(skill.getPointCost())
                                                                .append(stats.hasUnlockedSkill(skill.getId()) ? " (DESBLOQUEADA)" : "")
                                                                .append("\n");
                                                    }

                                                    context.getSource().sendSuccess(new StringTextComponent(sb.toString()), false);

                                                    return 1;

                                                })
                                )
                );

    }

    private interface Op {
        void apply(PlayerStats stats, int valor);
    }

    private static LiteralArgumentBuilder<CommandSource> buildPoints(String literalName, Op op) {
        return Commands.literal(literalName)
                .then(
                        Commands.argument("target", EntityArgument.player())
                                .then(
                                        Commands.argument("valor", IntegerArgumentType.integer())
                                                .executes(context -> {

                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    int valor = IntegerArgumentType.getInteger(context, "valor");

                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                    op.apply(stats, valor);
                                                    CommandUtil.sync(player, stats);

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent(
                                                                    "Pontos de skill racial de " + player.getName().getString() + " = " + stats.getSkillPoints()),
                                                            true);

                                                    return 1;

                                                })
                                )
                );
    }

    private static LiteralArgumentBuilder<CommandSource> buildFactionPoints(String literalName, Op op) {
        return Commands.literal(literalName)
                .then(
                        Commands.argument("target", EntityArgument.player())
                                .then(
                                        Commands.argument("valor", IntegerArgumentType.integer())
                                                .executes(context -> {

                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    int valor = IntegerArgumentType.getInteger(context, "valor");

                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                    op.apply(stats, valor);
                                                    CommandUtil.sync(player, stats);

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent(
                                                                    "Pontos de skill de faccao de " + player.getName().getString() + " = " + stats.getFactionSkillPoints()),
                                                            true);

                                                    return 1;

                                                })
                                )
                );
    }

}
