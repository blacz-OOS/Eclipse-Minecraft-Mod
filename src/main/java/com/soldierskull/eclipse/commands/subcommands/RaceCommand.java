package com.soldierskull.eclipse.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import com.soldierskull.eclipse.commands.CommandUtil;
import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

/**
 * /eclipse race get|set|clear <target> [tipo]
 * /eclipse race level set|add|remove <target> <valor>
 * /eclipse race xp set|add|remove <target> <valor>
 * /eclipse race rank <target>
 *
 * "set"/"clear" continuam sendo o ponto de entrada admin/debug para
 * RaceType. O gatilho REAL de transformação em jogo agora existe (Fase
 * 9 - ver com.soldierskull.eclipse.transformation.TransformationManager
 * e /eclipse transform) - "set"/"clear" seguem úteis pra testes e para
 * casos administrativos. level/xp/rank são a Fase 2/3/4: progressão
 * racial 0-12 e o rank derivado dela, totalmente separados do nível
 * geral do jogador.
 */
public class RaceCommand {

    private static final DynamicCommandExceptionType INVALID_TYPE = new DynamicCommandExceptionType(
            value -> new StringTextComponent("Raca invalida: " + value));

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("race")

                .then(
                        Commands.literal("get")
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .executes(context -> {

                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                    RaceType race = stats.getRace();

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent(
                                                                    "Raca de " + player.getName().getString() + " = " + race.getDisplayName()),
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
                                                                    RaceType tipo = parseType(context);

                                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                                    boolean ok = stats.setRace(tipo);
                                                                    if (!ok) {
                                                                        context.getSource().sendFailure(
                                                                                new StringTextComponent(
                                                                                        player.getName().getString() + " esta na faccao Cacadores - so pode ser Humano. Remova da faccao primeiro."));
                                                                        return 0;
                                                                    }
                                                                    CommandUtil.sync(player, stats);

                                                                    context.getSource().sendSuccess(
                                                                            new StringTextComponent(
                                                                                    "Raca de " + player.getName().getString() + " definida para " + tipo.getDisplayName()),
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
                                                    stats.clearRace();
                                                    CommandUtil.sync(player, stats);

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent(
                                                                    "Raca de " + player.getName().getString() + " revertida para Humano (nivel/XP racial zerados)"),
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
                                                    String rank = stats.getRacialRank();

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent(
                                                                    "Rank racial de " + player.getName().getString() + " = "
                                                                            + (rank != null ? rank : "(sem rank - " + stats.getRace().getDisplayName() + ")")
                                                                            + " (nivel racial " + stats.getRacialLevel() + ")"),
                                                            false);

                                                    return 1;

                                                })
                                )
                )

                .then(
                        Commands.literal("level")
                                .then(buildLevel("set", (stats, valor) -> stats.setRacialLevel(valor)))
                                .then(buildLevel("add", (stats, valor) -> stats.addRacialLevel(valor)))
                                .then(buildLevel("remove", (stats, valor) -> stats.removeRacialLevel(valor)))
                )

                .then(
                        Commands.literal("xp")
                                .then(buildLevel("set", (stats, valor) -> stats.setRacialXp(valor)))
                                .then(buildLevel("add", (stats, valor) -> stats.addRacialXp(valor)))
                                .then(buildLevel("remove", (stats, valor) -> stats.removeRacialXp(valor)))
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
                                                                    "Nivel racial de " + player.getName().getString() + " = " + stats.getRacialLevel()
                                                                            + " (XP racial " + stats.getRacialXp() + "/" + stats.getRacialMaxXp() + ")"),
                                                            true);

                                                    return 1;

                                                })
                                )
                );
    }

    private static RaceType parseType(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String raw = StringArgumentType.getString(context, "tipo");
        try {
            return RaceType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw INVALID_TYPE.create(raw);
        }
    }

}
