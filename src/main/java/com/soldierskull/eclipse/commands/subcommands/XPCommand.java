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

/** /eclipse xp set|add|remove <target> <valor> - XP. */
public class XPCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("xp")

                .then(build("set", (stats, valor) -> stats.setXp(valor)))
                .then(build("add", (stats, valor) -> stats.addXp(valor)))
                .then(build("remove", (stats, valor) -> stats.removeXp(valor)));

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
                                                                    "XP de " + player.getName().getString() + " = " + stats.getCurrentXp()),
                                                            true);

                                                    return 1;

                                                })
                                )
                );
    }

}
