package com.soldierskull.eclipse.entity.npc.cultist;

import java.util.ArrayList;
import java.util.List;

import com.soldierskull.eclipse.entity.npc.dialogue.DialogueOption;
import com.soldierskull.eclipse.entity.npc.dialogue.NPCDialogue;
import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.quests.Quest;
import com.soldierskull.eclipse.quests.QuestManager;
import com.soldierskull.eclipse.quests.QuestObjective;
import com.soldierskull.eclipse.quests.QuestObjectiveType;
import com.soldierskull.eclipse.quests.QuestRegistry;
import com.soldierskull.eclipse.stats.PlayerStats;
import com.soldierskull.eclipse.stats.ReputationFaction;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public final class CultistDialogue {

    public static final String NPC_ID = "cultist";

    private CultistDialogue() {
    }

    public static void register() {
        NPCDialogue.register(NPC_ID, "Circle of the Abyss", CultistDialogue::buildMenu);
    }

    private static List<DialogueOption> buildMenu(ServerPlayerEntity player, PlayerStats stats) {
        List<DialogueOption> options = new ArrayList<>();

        options.add(new DialogueOption(
                new TranslationTextComponent("dialogue.eclipse.cultist.who_option").getString(),
                (p, s) -> p.displayClientMessage(new TranslationTextComponent("dialogue.eclipse.cultist.who_response"), false)));

        options.add(buildQuestOption(player, stats));

        options.add(new DialogueOption(
                new TranslationTextComponent("dialogue.eclipse.cultist.reputation_option").getString(),
                (p, s) -> p.displayClientMessage(
                        new TranslationTextComponent("dialogue.eclipse.cultist.reputation_response", s.getReputation(ReputationFaction.CULTISTS)), false)));

        options.add(new DialogueOption(
                new TranslationTextComponent("dialogue.eclipse.cultist.rites_option").getString(),
                (p, s) -> p.displayClientMessage(new TranslationTextComponent("dialogue.eclipse.cultist.rites_response"), false)));

        return options;
    }

    private static DialogueOption buildQuestOption(ServerPlayerEntity player, PlayerStats stats) {
        // 1. Entregar missão com objetivos concluídos
        for (Quest quest : QuestRegistry.getAll()) {
            if (quest.getFactionRequirement() != FactionType.CULTISTS) {
                continue;
            }
            if (stats.hasActiveQuest(quest.getId()) && QuestManager.canComplete(player, stats, quest.getId())) {
                String optionLabel = new TranslationTextComponent("dialogue.eclipse.quest.option.deliver", quest.getDisplayName()).getString();
                return new DialogueOption(optionLabel, (p, s) -> {
                    QuestManager.complete(p, s, quest.getId());
                    p.displayClientMessage(new TranslationTextComponent("message.eclipse.quest.delivered", quest.getDisplayName()), false);
                });
            }
        }

        // 2. Missão ativa porém ainda incompleta (Informa progresso ao clicar)
        for (Quest quest : QuestRegistry.getAll()) {
            if (quest.getFactionRequirement() != FactionType.CULTISTS) {
                continue;
            }
            if (stats.hasActiveQuest(quest.getId())) {
                String optionLabel = new TranslationTextComponent("dialogue.eclipse.quest.option.in_progress", quest.getDisplayName()).getString();
                return new DialogueOption(optionLabel, (p, s) -> {
                    p.displayClientMessage(new TranslationTextComponent("message.eclipse.quest.status_header", quest.getDisplayName()), false);
                    for (int i = 0; i < quest.getObjectives().size(); i++) {
                        QuestObjective obj = quest.getObjectives().get(i);
                        if (obj.getType() == QuestObjectiveType.KILL_MOB) {
                            int prog = s.getQuestObjectiveProgress(quest.getId(), i);
                            p.displayClientMessage(new TranslationTextComponent("message.eclipse.quest.progress_kill", obj.getDescription(), prog, obj.getRequiredAmount()), false);
                        } else if (obj.getType() == QuestObjectiveType.COLLECT_ITEM) {
                            int count = QuestManager.countItemInInventory(p, obj.getTargetId());
                            p.displayClientMessage(new TranslationTextComponent("message.eclipse.quest.progress_collect", obj.getDescription(), count, obj.getRequiredAmount()), false);
                        }
                    }
                });
            }
        }

        // 3. Aceitar nova missão disponível
        for (Quest quest : QuestRegistry.getAll()) {
            if (quest.getFactionRequirement() != FactionType.CULTISTS) {
                continue;
            }
            if (!stats.hasActiveQuest(quest.getId()) && !stats.hasCompletedQuest(quest.getId()) && quest.meetsRequirements(stats)) {
                String optionLabel = new TranslationTextComponent("dialogue.eclipse.quest.option.accept", quest.getDisplayName()).getString();
                return new DialogueOption(optionLabel, (p, s) -> {
                    QuestManager.AcceptResult result = QuestManager.accept(s, quest.getId());
                    if (result == QuestManager.AcceptResult.OK) {
                        p.displayClientMessage(new TranslationTextComponent("message.eclipse.quest.accepted", quest.getDisplayName(), quest.getDescription()), false);
                    }
                });
            }
        }

        // 4. Nenhuma missão disponível
        return new DialogueOption(
                new TranslationTextComponent("dialogue.eclipse.cultist.no_quest").getString(),
                (p, s) -> p.displayClientMessage(new TranslationTextComponent("dialogue.eclipse.cultist.no_quest_response"), false)
        );
    }
}