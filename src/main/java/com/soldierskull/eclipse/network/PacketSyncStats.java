package com.soldierskull.eclipse.network;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.soldierskull.eclipse.client.ClientStatsCache;
import com.soldierskull.eclipse.stats.AffinityType;
import com.soldierskull.eclipse.stats.AttributeType;
import com.soldierskull.eclipse.stats.PlayerStats;
import com.soldierskull.eclipse.stats.ReputationFaction;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketSyncStats {

    private final int level;
    private final int currentXp;
    private final int maxXp;
    private final int points;
    private final int[] attributes;
    private final int commonEnergy;
    private final int commonEnergyMax;
    private final int corruption;
    private final int corruptionMax;
    private final int[] affinities;
    private final int[] reputations;
    private final int race;
    private final int racialLevel;
    private final int racialXp;
    private final int racialMaxXp;
    private final int faction;
    private final int factionLevel;
    private final int factionXp;
    private final int factionMaxXp;
    private final int skillPoints;
    private final int factionSkillPoints;
    private final String[] unlockedSkills;
    private final String[] cooldownSkillIds;
    private final long[] cooldownEndMillis;

    public PacketSyncStats(PlayerStats stats) {
        this.level = stats.getLevel();
        this.currentXp = stats.getCurrentXp();
        this.maxXp = stats.getMaxXp();
        this.points = stats.getPointsToDistribute();
        this.commonEnergy = stats.getCommonEnergy();
        this.commonEnergyMax = stats.getCommonEnergyMax();
        this.corruption = stats.getCorruption();
        this.corruptionMax = stats.getCorruptionMax();

        AttributeType[] attributeTypes = AttributeType.values();
        this.attributes = new int[attributeTypes.length];
        for (int i = 0; i < attributeTypes.length; i++) {
            this.attributes[i] = stats.getAttribute(attributeTypes[i]);
        }

        AffinityType[] affinityTypes = AffinityType.values();
        this.affinities = new int[affinityTypes.length];
        for (int i = 0; i < affinityTypes.length; i++) {
            this.affinities[i] = stats.getAffinity(affinityTypes[i]);
        }

        ReputationFaction[] factions = ReputationFaction.values();
        this.reputations = new int[factions.length];
        for (int i = 0; i < factions.length; i++) {
            this.reputations[i] = stats.getReputation(factions[i]);
        }

        this.race = stats.getRace().ordinal();
        this.racialLevel = stats.getRacialLevel();
        this.racialXp = stats.getRacialXp();
        this.racialMaxXp = stats.getRacialMaxXp();

        this.faction = stats.getFaction().ordinal();
        this.factionLevel = stats.getFactionLevel();
        this.factionXp = stats.getFactionXp();
        this.factionMaxXp = stats.getFactionMaxXp();

        this.skillPoints = stats.getSkillPoints();
        this.factionSkillPoints = stats.getFactionSkillPoints();
        Set<String> unlocked = stats.getUnlockedSkills();
        this.unlockedSkills = unlocked.toArray(new String[0]);

        Map<String, Long> cooldowns = stats.getAllSkillCooldowns();
        this.cooldownSkillIds = cooldowns.keySet().toArray(new String[0]);
        this.cooldownEndMillis = new long[this.cooldownSkillIds.length];
        for (int i = 0; i < this.cooldownSkillIds.length; i++) {
            this.cooldownEndMillis[i] = cooldowns.get(this.cooldownSkillIds[i]);
        }
    }

    private PacketSyncStats(int level, int currentXp, int maxXp, int points, int[] attributes,
                            int commonEnergy, int commonEnergyMax, int corruption, int corruptionMax,
                            int[] affinities, int[] reputations, int race, int racialLevel, int racialXp, int racialMaxXp,
                            int faction, int factionLevel, int factionXp, int factionMaxXp,
                            int skillPoints, int factionSkillPoints, String[] unlockedSkills,
                            String[] cooldownSkillIds, long[] cooldownEndMillis) {
        this.level = level;
        this.currentXp = currentXp;
        this.maxXp = maxXp;
        this.points = points;
        this.attributes = attributes;
        this.commonEnergy = commonEnergy;
        this.commonEnergyMax = commonEnergyMax;
        this.corruption = corruption;
        this.corruptionMax = corruptionMax;
        this.affinities = affinities;
        this.reputations = reputations;
        this.race = race;
        this.racialLevel = racialLevel;
        this.racialXp = racialXp;
        this.racialMaxXp = racialMaxXp;
        this.faction = faction;
        this.factionLevel = factionLevel;
        this.factionXp = factionXp;
        this.factionMaxXp = factionMaxXp;
        this.skillPoints = skillPoints;
        this.factionSkillPoints = factionSkillPoints;
        this.unlockedSkills = unlockedSkills;
        this.cooldownSkillIds = cooldownSkillIds;
        this.cooldownEndMillis = cooldownEndMillis;
    }

    public static void encode(PacketSyncStats msg, PacketBuffer buf) {
        buf.writeVarInt(msg.level);
        buf.writeVarInt(msg.currentXp);
        buf.writeVarInt(msg.maxXp);
        buf.writeVarInt(msg.points);

        for (int value : msg.attributes) {
            buf.writeVarInt(value);
        }
        buf.writeVarInt(msg.commonEnergy);
        buf.writeVarInt(msg.commonEnergyMax);
        buf.writeVarInt(msg.corruption);
        buf.writeVarInt(msg.corruptionMax);

        for (int value : msg.affinities) {
            buf.writeVarInt(value);
        }
        for (int value : msg.reputations) {
            buf.writeVarInt(value);
        }

        buf.writeVarInt(msg.race);
        buf.writeVarInt(msg.racialLevel);
        buf.writeVarInt(msg.racialXp);
        buf.writeVarInt(msg.racialMaxXp);

        buf.writeVarInt(msg.faction);
        buf.writeVarInt(msg.factionLevel);
        buf.writeVarInt(msg.factionXp);
        buf.writeVarInt(msg.factionMaxXp);

        buf.writeVarInt(msg.skillPoints);
        buf.writeVarInt(msg.factionSkillPoints);

        buf.writeVarInt(msg.unlockedSkills.length);
        for (String skillId : msg.unlockedSkills) {
            buf.writeUtf(skillId);
        }

        buf.writeVarInt(msg.cooldownSkillIds.length);
        for (int i = 0; i < msg.cooldownSkillIds.length; i++) {
            buf.writeUtf(msg.cooldownSkillIds[i]);
            buf.writeLong(msg.cooldownEndMillis[i]);
        }
    }

    public static PacketSyncStats decode(PacketBuffer buf) {
        int level = buf.readVarInt();
        int currentXp = buf.readVarInt();
        int maxXp = buf.readVarInt();
        int points = buf.readVarInt();

        int[] attributes = new int[AttributeType.values().length];
        for (int i = 0; i < attributes.length; i++) {
            attributes[i] = buf.readVarInt();
        }

        int commonEnergy = buf.readVarInt();
        int commonEnergyMax = buf.readVarInt();
        int corruption = buf.readVarInt();
        int corruptionMax = buf.readVarInt();

        int[] affinities = new int[AffinityType.values().length];
        for (int i = 0; i < affinities.length; i++) {
            affinities[i] = buf.readVarInt();
        }
        int[] reputations = new int[ReputationFaction.values().length];
        for (int i = 0; i < reputations.length; i++) {
            reputations[i] = buf.readVarInt();
        }

        int race = buf.readVarInt();
        int racialLevel = buf.readVarInt();
        int racialXp = buf.readVarInt();
        int racialMaxXp = buf.readVarInt();

        int faction = buf.readVarInt();
        int factionLevel = buf.readVarInt();
        int factionXp = buf.readVarInt();
        int factionMaxXp = buf.readVarInt();

        int skillPoints = buf.readVarInt();
        int factionSkillPoints = buf.readVarInt();

        int skillCount = buf.readVarInt();
        String[] unlockedSkills = new String[skillCount];
        for (int i = 0; i < skillCount; i++) {
            unlockedSkills[i] = buf.readUtf();
        }

        int cooldownCount = buf.readVarInt();
        String[] cooldownSkillIds = new String[cooldownCount];
        long[] cooldownEndMillis = new long[cooldownCount];
        for (int i = 0; i < cooldownCount; i++) {
            cooldownSkillIds[i] = buf.readUtf();
            cooldownEndMillis[i] = buf.readLong();
        }

        return new PacketSyncStats(level, currentXp, maxXp, points, attributes,
                commonEnergy, commonEnergyMax, corruption, corruptionMax, affinities, reputations,
                race, racialLevel, racialXp, racialMaxXp,
                faction, factionLevel, factionXp, factionMaxXp,
                skillPoints, factionSkillPoints, unlockedSkills, cooldownSkillIds, cooldownEndMillis);
    }

    public static void handle(PacketSyncStats msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                net.minecraftforge.api.distmarker.Dist.CLIENT,
                () -> () -> ClientStatsCache.update(msg.level, msg.currentXp, msg.maxXp, msg.points, msg.attributes,
                        msg.commonEnergy, msg.commonEnergyMax, msg.corruption, msg.corruptionMax,
                        msg.affinities, msg.reputations, msg.race, msg.racialLevel, msg.racialXp, msg.racialMaxXp,
                        msg.faction, msg.factionLevel, msg.factionXp, msg.factionMaxXp,
                        msg.skillPoints, msg.factionSkillPoints, msg.unlockedSkills,
                        msg.cooldownSkillIds, msg.cooldownEndMillis)));
        ctx.get().setPacketHandled(true);
    }
}
