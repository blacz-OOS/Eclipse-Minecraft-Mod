package com.soldierskull.eclipse.commands.subcommands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.soldierskull.eclipse.commands.CommandUtil;
import com.soldierskull.eclipse.stats.PlayerStats;
import com.soldierskull.eclipse.transformation.AbyssLocation;
import com.soldierskull.eclipse.transformation.TransformationManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

/**
 * /eclipse transform status <target>
 * /eclipse transform cancel <target>
 * /eclipse transform forceabyss <target> <true|false>
 *
 * "forceabyss" é um atalho de teste: marca o jogador como estando no
 * Abismo mesmo sem a estrutura/dimensão real existir ainda (Fase 13) -
 * ver AbyssLocation. Sem isso, não dá pra testar o gatilho da
 * transformação (50% afinidade + Abismo) de jeito nenhum hoje.
 */
public class TransformCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("transform")

                .then(
                        Commands.literal("status")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .executes(context -> {

                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    PlayerStats stats = CommandUtil.getStats(player);

                                                    String message;
                                                    if (!stats.isTransforming()) {
                                                        message = player.getName().getString() + " nao esta em transformacao (raca atual: " + stats.getRace().getDisplayName() + ")";
                                                    } else {
                                                        long currentDay = ((net.minecraft.world.server.ServerWorld) player.level).getDayTime() / 24000L;
                                                        long elapsed = currentDay - stats.getTransformationStartDay();
                                                        message = player.getName().getString() + " transformando em " + stats.getTransformationTargetRace().getDisplayName()
                                                                + " - dia " + elapsed + "/" + TransformationManager.TRANSFORMATION_DURATION_DAYS;
                                                    }

                                                    context.getSource().sendSuccess(new StringTextComponent(message), false);
                                                    return 1;

                                                })
                                )
                )

                .then(
                        Commands.literal("cancel")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .executes(context -> {

                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                    TransformationManager.cancel(stats);

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent("Transformacao de " + player.getName().getString() + " cancelada."),
                                                            true);
                                                    return 1;

                                                })
                                )
                )

                .then(
                        Commands.literal("forceabyss")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .then(
                                                        Commands.argument("valor", BoolArgumentType.bool())
                                                                .executes(context -> {

                                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                                    boolean valor = BoolArgumentType.getBool(context, "valor");
                                                                    AbyssLocation.setDebugForced(player.getUUID(), valor);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent(
                                                                                    "AbyssLocation.isInAbyss(" + player.getName().getString() + ") forcado para " + valor
                                                                                            + " (debug - Fase 13 ainda nao existe de verdade)"),
                                                                            true);
                                                                    return 1;

                                                                })
                                                )
                                )
                );

    }

}
