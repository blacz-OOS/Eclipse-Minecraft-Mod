package com.soldierskull.eclipse.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import com.soldierskull.eclipse.commands.CommandUtil;
import com.soldierskull.eclipse.stats.PlayerStats;
import com.soldierskull.eclipse.stats.ReputationFaction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

/**
 * /eclipse reputation <faccao> set|add|remove <target> <valor>
 * <faccao> is validated against the real ReputationFaction enum at runtime
 * (currently CACADORES, VAMPIROS, LOBISOMENS, CULTISTAS). "add" reuses
 * PlayerStats.addReputation(), so a positive value still triggers the
 * existing rival-faction interlink; "set" and "remove" skip it.
 */
public class ReputationCommand {

    private static final DynamicCommandExceptionType INVALID_FACTION = new DynamicCommandExceptionType(
            value -> new StringTextComponent("Faccao invalida: " + value));

    private interface Op {
        void apply(PlayerStats stats, ReputationFaction faccao, int valor);
    }

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("reputation")

                .then(
                        Commands.argument("faccao", StringArgumentType.word())
                                .then(build("set", (stats, faccao, valor) -> stats.setReputation(faccao, valor)))
                                .then(build("add", (stats, faccao, valor) -> stats.addReputation(faccao, valor)))
                                .then(build("remove", (stats, faccao, valor) -> stats.removeReputation(faccao, valor)))
                );

    }

    private static LiteralArgumentBuilder<CommandSource> build(String literalName, Op op) {
        return Commands.literal(literalName)
                .then(
                        Commands.argument("target", EntityArgument.player())
                                .then(
                                        Commands.argument("valor", IntegerArgumentType.integer())
                                                .executes(context -> {

                                                    ReputationFaction faccao = parseFaction(context);
                                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                                    int valor = IntegerArgumentType.getInteger(context, "valor");

                                                    PlayerStats stats = CommandUtil.getStats(player);
                                                    op.apply(stats, faccao, valor);
                                                    CommandUtil.sync(player, stats);

                                                    context.getSource().sendSuccess(
                                                            new StringTextComponent(
                                                                    "Reputation[" + faccao.name() + "] de " + player.getName().getString()
                                                                            + " = " + stats.getReputation(faccao)),
                                                            true);

                                                    return 1;

                                                })
                                )
                );
    }

    private static ReputationFaction parseFaction(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String raw = StringArgumentType.getString(context, "faccao");
        try {
            return ReputationFaction.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw INVALID_FACTION.create(raw);
        }
    }

}
