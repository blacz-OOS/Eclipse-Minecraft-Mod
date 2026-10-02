package com.soldierskull.eclipse.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.stats.AffinityType;
import com.soldierskull.eclipse.stats.AttributeType;
import com.soldierskull.eclipse.stats.ReputationFaction;

public final class ClientStatsCache {

    public static int level = 1;
    public static int currentXp = 0;
    public static int maxXp = 100;
    public static int points = 0;
    public static int[] attributes = new int[AttributeType.values().length];
    public static int commonEnergy = 0;
    public static int commonEnergyMax = 0;
    public static int corruption = 0;
    public static int corruptionMax = 0;
    public static int[] affinities = new int[AffinityType.values().length];
    public static int[] reputations = new int[ReputationFaction.values().length];
    public static int race = RaceType.HUMAN.ordinal();
    public static int racialLevel = 0;
    public static int racialXp = 0;
    public static int racialMaxXp = 0;
    public static int faction = FactionType.NONE.ordinal();
    public static int factionLevel = 0;
    public static int factionXp = 0;
    public static int factionMaxXp = 0;
    public static int skillPoints = 0;
    public static int factionSkillPoints = 0;
    public static Set<String> unlockedSkills = new HashSet<>();
    /** skillId -> epoch millis em que volta a ficar pronta (mesmo formato persistente do servidor). */
    public static Map<String, Long> skillCooldowns = new HashMap<>();

    private ClientStatsCache() {
    }

    public static void update(int level, int currentXp, int maxXp, int points, int[] attributes,
                              int commonEnergy, int commonEnergyMax, int corruption, int corruptionMax,
                              int[] affinities, int[] reputations, int race, int racialLevel, int racialXp, int racialMaxXp,
                              int faction, int factionLevel, int factionXp, int factionMaxXp,
                              int skillPoints, int factionSkillPoints, String[] unlockedSkills,
                              String[] cooldownSkillIds, long[] cooldownEndMillis) {
        ClientStatsCache.level = level;
        ClientStatsCache.currentXp = currentXp;
        ClientStatsCache.maxXp = maxXp;
        ClientStatsCache.points = points;
        ClientStatsCache.attributes = attributes;
        ClientStatsCache.commonEnergy = commonEnergy;
        ClientStatsCache.commonEnergyMax = commonEnergyMax;
        ClientStatsCache.corruption = corruption;
        ClientStatsCache.corruptionMax = corruptionMax;
        ClientStatsCache.affinities = affinities;
        ClientStatsCache.reputations = reputations;
        ClientStatsCache.race = race;
        ClientStatsCache.racialLevel = racialLevel;
        ClientStatsCache.racialXp = racialXp;
        ClientStatsCache.racialMaxXp = racialMaxXp;
        ClientStatsCache.faction = faction;
        ClientStatsCache.factionLevel = factionLevel;
        ClientStatsCache.factionXp = factionXp;
        ClientStatsCache.factionMaxXp = factionMaxXp;
        ClientStatsCache.skillPoints = skillPoints;
        ClientStatsCache.factionSkillPoints = factionSkillPoints;
        ClientStatsCache.unlockedSkills = new HashSet<>(Arrays.asList(unlockedSkills));

        Map<String, Long> cooldowns = new HashMap<>();
        for (int i = 0; i < cooldownSkillIds.length; i++) {
            cooldowns.put(cooldownSkillIds[i], cooldownEndMillis[i]);
        }
        ClientStatsCache.skillCooldowns = cooldowns;
    }

    public static int getAttribute(AttributeType type) {
        return attributes[type.ordinal()];
    }

    public static int getAffinity(AffinityType type) {
        return affinities[type.ordinal()];
    }

    public static int getReputation(ReputationFaction faction) {
        return reputations[faction.ordinal()];
    }

    public static RaceType getRace() {
        return RaceType.values()[race];
    }

    public static FactionType getFaction() {
        return FactionType.values()[faction];
    }

    /** Segundos restantes de cooldown de uma skill, ou 0 se já pronta. Cliente usa System.currentTimeMillis() local, igual ao servidor. */
    public static long getSkillCooldownSecondsRemaining(String skillId) {
        Long end = skillCooldowns.get(skillId);
        if (end == null) {
            return 0L;
        }
        long remainingMillis = end - System.currentTimeMillis();
        return remainingMillis > 0 ? remainingMillis / 1000L : 0L;
    }
}
