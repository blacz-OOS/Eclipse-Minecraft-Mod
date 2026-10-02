package com.soldierskull.eclipse.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.soldierskull.eclipse.commands.CommandUtil;
import com.soldierskull.eclipse.stats.AffinityType;
import com.soldierskull.eclipse.stats.AttributeType;
import com.soldierskull.eclipse.stats.PlayerStats;
import com.soldierskull.eclipse.stats.ReputationFaction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

/** /eclipse debug <target> - dumps every PlayerStats value for that player to chat. */
public class DebugCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("debug")

                .then(

                        Commands.argument("target", EntityArgument.player())

                                .executes(context -> {

                                    ServerPlayerEntity player = CommandUtil.getTarget(context, "target");
                                    PlayerStats stats = CommandUtil.getStats(player);

                                    StringBuilder sb = new StringBuilder();
                                    sb.append("=== Eclipse Stats: ").append(player.getName().getString()).append(" ===\n");
                                    sb.append("Level: ").append(stats.getLevel())
                                            .append(" | XP: ").append(stats.getCurrentXp()).append("/").append(stats.getMaxXp())
                                            .append(" | Points: ").append(stats.getPointsToDistribute()).append("\n");
                                    sb.append("Common Energy: ").append(stats.getCommonEnergy()).append("/").append(stats.getCommonEnergyMax())
                                            .append(" | Corruption: ").append(stats.getCorruption()).append("/").append(stats.getCorruptionMax())
                                            .append("\n");
                                    sb.append("Race: ").append(stats.getRace().getDisplayName())
                                            .append(" | Racial Level: ").append(stats.getRacialLevel())
                                            .append(" | Racial XP: ").append(stats.getRacialXp()).append("/").append(stats.getRacialMaxXp())
                                            .append(" | Rank: ").append(stats.getRacialRank() != null ? stats.getRacialRank() : "-")
                                            .append("\n");
                                    sb.append("Faction: ").append(stats.getFaction().getDisplayName())
                                            .append(" | Faction Level: ").append(stats.getFactionLevel())
                                            .append(" | Faction XP: ").append(stats.getFactionXp()).append("/").append(stats.getFactionMaxXp())
                                            .append(" | Rank: ").append(stats.getFactionRank() != null ? stats.getFactionRank() : "-")
                                            .append("\n");
                                    sb.append("Skill Points: ").append(stats.getSkillPoints())
                                            .append(" | Faction Skill Points: ").append(stats.getFactionSkillPoints())
                                            .append(" | Unlocked: ").append(stats.getUnlockedSkills())
                                            .append("\n");

                                    for (AttributeType type : AttributeType.values()) {
                                        sb.append(type.getDisplayName()).append("=").append(stats.getAttribute(type)).append("  ");
                                    }
                                    sb.append("\n");

                                    for (AffinityType type : AffinityType.values()) {
                                        sb.append("Affinity[").append(type.getDisplayName()).append("]=").append(stats.getAffinity(type)).append("  ");
                                    }
                                    sb.append("\n");

                                    for (ReputationFaction faction : ReputationFaction.values()) {
                                        sb.append("Reputation[").append(faction.getDisplayName()).append("]=").append(stats.getReputation(faction)).append("  ");
                                    }

                                    context.getSource().sendSuccess(new StringTextComponent(sb.toString()), false);

                                    return 1;

                                })

                );

    }

}
