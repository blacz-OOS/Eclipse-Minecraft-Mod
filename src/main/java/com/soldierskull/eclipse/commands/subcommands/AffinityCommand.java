package com.soldierskull.eclipse.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import com.soldierskull.eclipse.commands.CommandUtil;
import com.soldierskull.eclipse.stats.AffinityType;
import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

/**
 * /eclipse affinity <tipo> set|add|remove <target> <valor>
 * <tipo> is validated against the real AffinityType enum at runtime
 * (currently VAMPIRICA, LUPINA - see AffinityType.java).
 */
public class AffinityCommand {

    private static final DynamicCommandExceptionType INVALID_TYPE = new DynamicCommandExceptionType(
            value -> new StringTextComponent("Afinidade invalida: " + value));

    private interface Op {
        void apply(PlayerStats stats, AffinityType tipo, int valor);
    }

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("affinity")

                .then(
                        Commands.argument("tipo", StringArgumentType.word())
                                .then(build("set", (stats, tipo, valor) -> stats.setAffinity(tipo, valor)))
                                .then(build("add", (stats, tipo, valor) -> stats.addAffinity(tipo, valor)))
                                .then(build("remove", (stats, tipo, valor) -> stats.removeAffinity(tipo, valor)))
                );

    }

    private static LiteralArgumentBuilder<CommandSource> build(String literalName, Op op) {
        return Commands.literal(literalName)
                .then(
                        Commands.argument("target", EntityArgument.player())
                                .then(
                                        Commands.argument("valor", IntegerArgumentType.integer())
                                                .executes(context -> {

                                                    AffinityType tipo = parseType(context);
                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    int valor = IntegerArgumentType.getInteger(context, "valor");

                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                    op.apply(stats, tipo, valor);
                                                    CommandUtil.sync(player, stats);

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent(
                                                                    "Affinity[" + tipo.name() + "] de " + player.getName().getString()
                                                                            + " = " + stats.getAffinity(tipo)),
                                                            true);

                                                    return 1;

                                                })
                                )
                );
    }

    private static AffinityType parseType(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String raw = StringArgumentType.getString(context, "tipo");
        try {
            return AffinityType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw INVALID_TYPE.create(raw);
        }
    }

}
