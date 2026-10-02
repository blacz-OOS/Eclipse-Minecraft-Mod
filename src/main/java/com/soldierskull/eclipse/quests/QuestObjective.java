package com.soldierskull.eclipse.quests;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Um objetivo de quest.
 */
public class QuestObjective {

    private final QuestObjectiveType type;
    private final String targetId;
    private final int requiredAmount;
    private final String descriptionKey;

    public QuestObjective(QuestObjectiveType type, String targetId, int requiredAmount, String descriptionKey) {
        this.type = type;
        this.targetId = targetId;
        this.requiredAmount = requiredAmount;
        this.descriptionKey = descriptionKey;
    }

    public QuestObjectiveType getType() { return this.type; }
    public String getTargetId() { return this.targetId; }
    public int getRequiredAmount() { return this.requiredAmount; }
    public String getDescriptionKey() { return this.descriptionKey; }
    public ITextComponent getDescription() { return new TranslationTextComponent(this.descriptionKey); }
}