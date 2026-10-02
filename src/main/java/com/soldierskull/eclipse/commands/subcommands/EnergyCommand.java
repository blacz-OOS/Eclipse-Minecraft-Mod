package com.soldierskull.eclipse.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.soldierskull.eclipse.commands.CommandUtil;
import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

/** /eclipse energy set|add|remove <target> <valor> - Common Energy. */
public class EnergyCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("energy")

                .then(

                        Commands.literal("set")

                                .then(

                                        Commands.argument("target", EntityArgument.player())

                                                .then(
                                                        Commands.argument("valor", IntegerArgumentType.integer())

                                                                .executes(context -> {

                                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                                    int valor = IntegerArgumentType.getInteger(context, "valor");

                                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                                    stats.setCommonEnergy(valor);
                                                                    CommandUtil.sync(player, stats);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent(
                                                                                    "Energy de " + player.getName().getString() + " = " + stats.getCommonEnergy()),
                                                                            true);

                                                                    return 1;

                                                                })
                                                )

                                )

                )

                .then(

                        Commands.literal("add")

                                .then(

                                        Commands.argument("target", EntityArgument.player())

                                                .then(
                                                        Commands.argument("valor", IntegerArgumentType.integer())

                                                                .executes(context -> {

                                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                                    int valor = IntegerArgumentType.getInteger(context, "valor");

                                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                                    stats.addCommonEnergy(valor);
                                                                    CommandUtil.sync(player, stats);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent(
                                                                                    "Energy de " + player.getName().getString() + " = " + stats.getCommonEnergy()),
                                                                            true);

                                                                    return 1;

                                                                })
                                                )

                                )

                )

                .then(

                        Commands.literal("remove")

                                .then(

                                        Commands.argument("target", EntityArgument.player())

                                                .then(
                                                        Commands.argument("valor", IntegerArgumentType.integer())

                                                                .executes(context -> {

                                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                                    int valor = IntegerArgumentType.getInteger(context, "valor");

                                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                                    stats.removeCommonEnergy(valor);
                                                                    CommandUtil.sync(player, stats);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent(
                                                                                    "Energy de " + player.getName().getString() + " = " + stats.getCommonEnergy()),
                                                                            true);

                                                                    return 1;

                                                                })
                                                )

                                )

                );

    }

}
