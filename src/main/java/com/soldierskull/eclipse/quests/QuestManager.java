package com.soldierskull.eclipse.quests;

import java.util.List;

import com.soldierskull.eclipse.network.PacketHandler;
import com.soldierskull.eclipse.network.PacketSyncStats;
import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public final class QuestManager {

    private QuestManager() {
    }

    public enum AcceptResult {
        OK,
        QUEST_INEXISTENTE,
        JA_ATIVA,
        JA_COMPLETA_NAO_REPETIVEL,
        REQUISITOS_NAO_ATENDIDOS
    }

    public enum CompleteResult {
        OK,
        QUEST_INEXISTENTE,
        NAO_ESTA_ATIVA,
        OBJETIVOS_INCOMPLETOS
    }

    public static AcceptResult accept(PlayerStats stats, String questId) {
        Quest quest = QuestRegistry.get(questId);
        if (quest == null) {
            return AcceptResult.QUEST_INEXISTENTE;
        }
        if (stats.hasActiveQuest(questId)) {
            return AcceptResult.JA_ATIVA;
        }
        if (stats.hasCompletedQuest(questId) && !quest.isRepeatable()) {
            return AcceptResult.JA_COMPLETA_NAO_REPETIVEL;
        }
        if (!quest.meetsRequirements(stats)) {
            return AcceptResult.REQUISITOS_NAO_ATENDIDOS;
        }

        stats.startQuest(questId);
        return AcceptResult.OK;
    }

    public static void onMobKilled(PlayerStats stats, String killedEntityTypeId) {
        for (String questId : stats.getActiveQuests()) {
            Quest quest = QuestRegistry.get(questId);
            if (quest == null) {
                continue;
            }
            List<QuestObjective> objectives = quest.getObjectives();
            for (int i = 0; i < objectives.size(); i++) {
                QuestObjective objective = objectives.get(i);
                if (objective.getType() == QuestObjectiveType.KILL_MOB && objective.getTargetId().equals(killedEntityTypeId)) {
                    int current = stats.getQuestObjectiveProgress(questId, i);
                    if (current < objective.getRequiredAmount()) {
                        stats.setQuestObjectiveProgress(questId, i, current + 1);
                    }
                }
            }
        }
    }

    public static boolean canComplete(ServerPlayerEntity player, PlayerStats stats, String questId) {
        Quest quest = QuestRegistry.get(questId);
        if (quest == null || !stats.hasActiveQuest(questId)) {
            return false;
        }
        List<QuestObjective> objectives = quest.getObjectives();
        for (int i = 0; i < objectives.size(); i++) {
            if (!isObjectiveSatisfied(player, stats, questId, i, objectives.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isObjectiveSatisfied(ServerPlayerEntity player, PlayerStats stats, String questId, int index, QuestObjective objective) {
        if (objective.getType() == QuestObjectiveType.KILL_MOB) {
            return stats.getQuestObjectiveProgress(questId, index) >= objective.getRequiredAmount();
        }
        if (objective.getType() == QuestObjectiveType.COLLECT_ITEM) {
            return countItemInInventory(player, objective.getTargetId()) >= objective.getRequiredAmount();
        }
        return false;
    }

    // MUDANÇA: 'public' para permitir checar contagem ao vivo no dialogo dos NPCs
    public static int countItemInInventory(ServerPlayerEntity player, String itemId) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item == null) {
            return 0;
        }
        int total = 0;
        for (ItemStack stack : player.inventory.items) {
            if (stack.getItem() == item) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static void consumeItemFromInventory(ServerPlayerEntity player, String itemId, int amount) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item == null) {
            return;
        }
        int remaining = amount;
        for (ItemStack stack : player.inventory.items) {
            if (remaining <= 0) {
                break;
            }
            if (stack.getItem() == item) {
                int take = Math.min(remaining, stack.getCount());
                stack.shrink(take);
                remaining -= take;
            }
        }
    }

    public static CompleteResult complete(ServerPlayerEntity player, PlayerStats stats, String questId) {
        Quest quest = QuestRegistry.get(questId);
        if (quest == null) {
            return CompleteResult.QUEST_INEXISTENTE;
        }
        if (!stats.hasActiveQuest(questId)) {
            return CompleteResult.NAO_ESTA_ATIVA;
        }
        if (!canComplete(player, stats, questId)) {
            return CompleteResult.OBJETIVOS_INCOMPLETOS;
        }

        for (QuestObjective objective : quest.getObjectives()) {
            if (objective.getType() == QuestObjectiveType.COLLECT_ITEM) {
                consumeItemFromInventory(player, objective.getTargetId(), objective.getRequiredAmount());
            }
        }

        grantReward(player, stats, quest.getReward());
        stats.finishQuest(questId);

        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncStats(stats));
        return CompleteResult.OK;
    }

    private static void grantReward(ServerPlayerEntity player, PlayerStats stats, QuestReward reward) {
        if (reward.getGeneralXp() > 0) {
            stats.addXp(reward.getGeneralXp());
        }
        if (reward.getRacialXp() > 0) {
            stats.addRacialXp(reward.getRacialXp());
        }
        if (reward.getFactionXp() > 0) {
            stats.addFactionXp(reward.getFactionXp());
        }
        if (reward.getSkillPoints() > 0) {
            stats.addSkillPoints(reward.getSkillPoints());
        }
        if (reward.getFactionSkillPoints() > 0) {
            stats.addFactionSkillPoints(reward.getFactionSkillPoints());
        }
        for (QuestReward.ItemReward itemReward : reward.getItems()) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemReward.itemId));
            if (item != null) {
                player.addItem(new ItemStack(item, itemReward.count));
            }
        }
        reward.getAffinityChanges().forEach(stats::addAffinity);
        reward.getReputationChanges().forEach(stats::addReputation);
        for (String ritualId : reward.getRitualsUnlocked()) {
            stats.learnRitual(ritualId);
        }
        if (reward.getFactionToJoin() != null) {
            stats.setFaction(reward.getFactionToJoin());
        }
    }
}