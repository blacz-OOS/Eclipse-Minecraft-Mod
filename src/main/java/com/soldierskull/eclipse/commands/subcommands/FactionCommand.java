package com.soldierskull.eclipse.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import com.soldierskull.eclipse.commands.CommandUtil;
import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

/**
 * /eclipse faction get|set|clear <target> [tipo]
 * /eclipse faction level set|add|remove <target> <valor>
 * /eclipse faction xp set|add|remove <target> <valor>
 * /eclipse faction rank <target>
 *
 * Espelha RaceCommand ponto a ponto - mesma estrutura, mas para
 * FactionType/factionLevel/factionXp. Sistema completamente independente
 * do racial (nível de facção != nível racial, rank de facção != rank
 * racial), como confirmado.
 */
public class FactionCommand {

    private static final DynamicCommandExceptionType INVALID_TYPE = new DynamicCommandExceptionType(
            value -> new StringTextComponent("Faccao invalida: " + value));

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("faction")

                .then(
                        Commands.literal("get")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .executes(context -> {

                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                    FactionType faction = stats.getFaction();

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent(
                                                                    "Faccao de " + player.getName().getString() + " = " + faction.getDisplayName()),
                                                            false);

                                                    return 1;

                                                })
                                )
                )

                .then(
                        Commands.literal("set")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .then(
                                                        Commands.argument("tipo", StringArgumentType.word())
                                                                .executes(context -> {

                                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                                    FactionType tipo = parseType(context);

                                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                                    boolean ok = stats.setFaction(tipo);
                                                                    if (!ok) {
                                                                        context.getSource().sendFailure(
                                                                                new StringTextComponent(
                                                                                        player.getName().getString() + " nao e Humano - Cacadores so aceita humanos."));
                                                                        return 0;
                                                                    }
                                                                    CommandUtil.sync(player, stats);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent(
                                                                                    "Faccao de " + player.getName().getString() + " definida para " + tipo.getDisplayName()),
                                                                            true);

                                                                    return 1;

                                                                })
                                                )
                                )
                )

                .then(
                        Commands.literal("clear")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .executes(context -> {

                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                    stats.clearFaction();
                                                    CommandUtil.sync(player, stats);

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent(
                                                                    "Faccao de " + player.getName().getString() + " revertida para Nenhuma (nivel/XP de faccao zerados)"),
                                                            true);

                                                    return 1;

                                                })
                                )
                )

                .then(
                        Commands.literal("rank")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .executes(context -> {

                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                    String rank = stats.getFactionRank();

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent(
                                                                    "Rank de faccao de " + player.getName().getString() + " = "
                                                                            + (rank != null ? rank : "(sem rank - " + stats.getFaction().getDisplayName() + ")")
                                                                            + " (nivel de faccao " + stats.getFactionLevel() + ")"),
                                                            false);

                                                    return 1;

                                                })
                                )
                )

                .then(
                        Commands.literal("level")
                                .then(buildLevel("set", (stats, valor) -> stats.setFactionLevel(valor)))
                                .then(buildLevel("add", (stats, valor) -> stats.addFactionLevel(valor)))
                                .then(buildLevel("remove", (stats, valor) -> stats.removeFactionLevel(valor)))
                )

                .then(
                        Commands.literal("xp")
                                .then(buildLevel("set", (stats, valor) -> stats.setFactionXp(valor)))
                                .then(buildLevel("add", (stats, valor) -> stats.addFactionXp(valor)))
                                .then(buildLevel("remove", (stats, valor) -> stats.removeFactionXp(valor)))
                );

    }

    private interface Op {
        void apply(PlayerStats stats, int valor);
    }

    private static LiteralArgumentBuilder<CommandSource> buildLevel(String literalName, Op op) {
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
                                                                    "Nivel de faccao de " + player.getName().getString() + " = " + stats.getFactionLevel()
                                                                            + " (XP de faccao " + stats.getFactionXp() + "/" + stats.getFactionMaxXp() + ")"),
                                                            true);

                                                    return 1;

                                                })
                                )
                );
    }

    private static FactionType parseType(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String raw = StringArgumentType.getString(context, "tipo");
        try {
            return FactionType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw INVALID_TYPE.create(raw);
        }
    }

}
