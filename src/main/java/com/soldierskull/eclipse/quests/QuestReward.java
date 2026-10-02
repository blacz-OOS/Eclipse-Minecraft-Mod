package com.soldierskull.eclipse.quests;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.stats.AffinityType;
import com.soldierskull.eclipse.stats.ReputationFaction;

/**
 * Recompensa concedida ao entregar uma quest. Inclui Afinidade e
 * Reputação de propósito (confirmado) - adianta parte da integração que
 * formalmente é Fase 15/16, mas o mecanismo (addAffinity/addReputation)
 * já existe em PlayerStats desde o início, então não há nada "novo" pra
 * inventar aqui, só conectar.
 */
public class QuestReward {

    public static class ItemReward {
        public final String itemId; // registry name, ex.: "eclipse:abyss_shard"
        public final int count;

        public ItemReward(String itemId, int count) {
            this.itemId = itemId;
            this.count = count;
        }
    }

    private final int generalXp;
    private final int racialXp;
    private final int factionXp;
    private final int skillPoints;
    private final int factionSkillPoints;
    private final List<ItemReward> items;
    private final Map<AffinityType, Integer> affinityChanges;
    private final Map<ReputationFaction, Integer> reputationChanges;
    private final List<String> ritualsUnlocked; // ids de Ritual (ResourceLocation.toString()) - Bloco E
    private final FactionType factionToJoin; // null = não ingressa em nenhuma facção - Etapa 2

    private QuestReward(Builder builder) {
        this.generalXp = builder.generalXp;
        this.racialXp = builder.racialXp;
        this.factionXp = builder.factionXp;
        this.skillPoints = builder.skillPoints;
        this.factionSkillPoints = builder.factionSkillPoints;
        this.items = Collections.unmodifiableList(builder.items);
        this.affinityChanges = Collections.unmodifiableMap(builder.affinityChanges);
        this.reputationChanges = Collections.unmodifiableMap(builder.reputationChanges);
        this.ritualsUnlocked = Collections.unmodifiableList(builder.ritualsUnlocked);
        this.factionToJoin = builder.factionToJoin;
    }

    public int getGeneralXp() { return this.generalXp; }
    public int getRacialXp() { return this.racialXp; }
    public int getFactionXp() { return this.factionXp; }
    public int getSkillPoints() { return this.skillPoints; }
    public int getFactionSkillPoints() { return this.factionSkillPoints; }
    public List<ItemReward> getItems() { return this.items; }
    public Map<AffinityType, Integer> getAffinityChanges() { return this.affinityChanges; }
    public Map<ReputationFaction, Integer> getReputationChanges() { return this.reputationChanges; }
    public List<String> getRitualsUnlocked() { return this.ritualsUnlocked; }
    public FactionType getFactionToJoin() { return this.factionToJoin; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int generalXp = 0;
        private int racialXp = 0;
        private int factionXp = 0;
        private int skillPoints = 0;
        private int factionSkillPoints = 0;
        private final List<ItemReward> items = new java.util.ArrayList<>();
        private final Map<AffinityType, Integer> affinityChanges = new LinkedHashMap<>();
        private final Map<ReputationFaction, Integer> reputationChanges = new LinkedHashMap<>();
        private final List<String> ritualsUnlocked = new java.util.ArrayList<>();
        private FactionType factionToJoin = null;

        public Builder generalXp(int amount) { this.generalXp = amount; return this; }
        public Builder racialXp(int amount) { this.racialXp = amount; return this; }
        public Builder factionXp(int amount) { this.factionXp = amount; return this; }
        public Builder skillPoints(int amount) { this.skillPoints = amount; return this; }
        public Builder factionSkillPoints(int amount) { this.factionSkillPoints = amount; return this; }

        public Builder item(String itemId, int count) {
            this.items.add(new ItemReward(itemId, count));
            return this;
        }

        public Builder affinity(AffinityType type, int amount) {
            this.affinityChanges.put(type, amount);
            return this;
        }

        public Builder reputation(ReputationFaction faction, int amount) {
            this.reputationChanges.put(faction, amount);
            return this;
        }

        /** Desbloqueia um ritual GENERAL - único caminho pra isso (Bloco E, decisão confirmada). Use o id completo, ex.: "eclipse:abyss_crystal_charging". */
        public Builder ritual(String ritualId) {
            this.ritualsUnlocked.add(ritualId);
            return this;
        }

        /** Faz o jogador ingressar nessa facção ao completar - só deve ser usado em quests marcadas {@code factionEntryPoint} (Etapa 2). */
        public Builder joinFaction(FactionType faction) {
            this.factionToJoin = faction;
            return this;
        }

        public QuestReward build() {
            return new QuestReward(this);
        }
    }
}
