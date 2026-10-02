package com.soldierskull.eclipse.quests;

import java.util.Collections;
import java.util.List;

import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Uma quest: id, objetivos (1 ou mais), recompensa, requisito opcional
 * de raça/facção, e pré-requisitos.
 */
public class Quest {

    private final String id;
    private final String displayNameKey;
    private final String descriptionKey;
    private final RaceType raceRequirement; // null = qualquer raça
    private final FactionType factionRequirement; // null = qualquer facção
    private final List<QuestObjective> objectives;
    private final QuestReward reward;
    private final List<String> prerequisiteQuestIds;
    private final boolean repeatable;
    private final boolean factionEntryPoint;

    public Quest(String id, String displayNameKey, String descriptionKey, RaceType raceRequirement,
                 FactionType factionRequirement, List<QuestObjective> objectives, QuestReward reward,
                 List<String> prerequisiteQuestIds, boolean repeatable) {
        this(id, displayNameKey, descriptionKey, raceRequirement, factionRequirement, objectives, reward,
                prerequisiteQuestIds, repeatable, false);
    }

    public Quest(String id, String displayNameKey, String descriptionKey, RaceType raceRequirement,
                 FactionType factionRequirement, List<QuestObjective> objectives, QuestReward reward,
                 List<String> prerequisiteQuestIds, boolean repeatable, boolean factionEntryPoint) {
        this.id = id;
        this.displayNameKey = displayNameKey;
        this.descriptionKey = descriptionKey;
        this.raceRequirement = raceRequirement;
        this.factionRequirement = factionRequirement;
        this.objectives = Collections.unmodifiableList(objectives);
        this.reward = reward;
        this.prerequisiteQuestIds = prerequisiteQuestIds == null ? Collections.emptyList() : Collections.unmodifiableList(prerequisiteQuestIds);
        this.repeatable = repeatable;
        this.factionEntryPoint = factionEntryPoint;
    }

    /** True se `stats` atende raça/facção e todos os pré-requisitos. */
    public boolean meetsRequirements(PlayerStats stats) {
        if (this.raceRequirement != null && stats.getRace() != this.raceRequirement) {
            return false;
        }
        if (this.factionRequirement != null) {
            if (this.factionEntryPoint) {
                if (stats.getFaction() != FactionType.NONE && stats.getFaction() != this.factionRequirement) {
                    return false;
                }
            } else if (stats.getFaction() != this.factionRequirement) {
                return false;
            }
        }
        for (String prereq : this.prerequisiteQuestIds) {
            if (!stats.hasCompletedQuest(prereq)) {
                return false;
            }
        }
        return true;
    }

    public String getId() { return this.id; }
    public String getDisplayNameKey() { return this.displayNameKey; }
    public String getDescriptionKey() { return this.descriptionKey; }
    public ITextComponent getDisplayName() { return new TranslationTextComponent(this.displayNameKey); }
    public ITextComponent getDescription() { return new TranslationTextComponent(this.descriptionKey); }
    public RaceType getRaceRequirement() { return this.raceRequirement; }
    public FactionType getFactionRequirement() { return this.factionRequirement; }
    public List<QuestObjective> getObjectives() { return this.objectives; }
    public QuestReward getReward() { return this.reward; }
    public List<String> getPrerequisiteQuestIds() { return this.prerequisiteQuestIds; }
    public boolean isRepeatable() { return this.repeatable; }
    public boolean isFactionEntryPoint() { return this.factionEntryPoint; }
}