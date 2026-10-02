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

/** /eclipse points set|add|remove <target> <valor> - Points. */
public class PointsCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("points")

                .then(build("set", (stats, valor) -> stats.setPoints(valor)))
                .then(build("add", (stats, valor) -> stats.addPoints(valor)))
                .then(build("remove", (stats, valor) -> stats.removePoints(valor)));

    }

    private interface Op {
        void apply(PlayerStats stats, int valor);
    }

    private static LiteralArgumentBuilder<CommandSource> build(String literalName, Op op) {
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
                                                                    "Points de " + player.getName().getString() + " = " + stats.getPointsToDistribute()),
                                                            true);

                                                    return 1;

                                                })
                                )
                );
    }

}
