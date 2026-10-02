package com.soldierskull.eclipse.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import com.soldierskull.eclipse.commands.subcommands.*;

public class EclipseCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(

                Commands.literal("eclipse")

                        .requires(source -> source.hasPermission(2))

                        .then(EnergyCommand.register())

                        .then(CorruptionCommand.register())

                        .then(XPCommand.register())

                        .then(LevelCommand.register())

                        .then(PointsCommand.register())

                        .then(AffinityCommand.register())

                        .then(ReputationCommand.register())

                        .then(RaceCommand.register())

                        .then(FactionCommand.register())

                        .then(SkillCommand.register())

                        .then(TransformCommand.register())

                        .then(QuestCommand.register())

                        .then(DebugCommand.register())

                        .then(BookCommand.register())

        );

    }

}