package com.soldierskull.eclipse.quests;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.stats.AffinityType;
import com.soldierskull.eclipse.stats.ReputationFaction;

public final class QuestRegistry {

    private static final Map<String, Quest> QUESTS = new LinkedHashMap<>();

    static {
        // CORREÇÃO: raceRequirement alterado de RaceType.HUMAN para null.
        // Assim, qualquer jogador sem facção oposta pode aceitar a missão inicial dos Caçadores.
        register(new Quest(
                "cacadores_primeira_cacada",
                "quest.eclipse.cacadores_primeira_cacada.title",
                "quest.eclipse.cacadores_primeira_cacada.desc",
                null, FactionType.HUNTERS,
                objectives(new QuestObjective(QuestObjectiveType.KILL_MOB, "minecraft:zombie", 5, "quest.eclipse.cacadores_primeira_cacada.obj.0")),
                QuestReward.builder()
                        .generalXp(20)
                        .factionXp(10)
                        .reputation(ReputationFaction.HUNTERS, 5)
                        .joinFaction(FactionType.HUNTERS)
                        .build(),
                null,
                false,
                true
        ));

        register(new Quest(
                "cacadores_cacada_sobrenatural",
                "quest.eclipse.cacadores_cacada_sobrenatural.title",
                "quest.eclipse.cacadores_cacada_sobrenatural.desc",
                null, FactionType.HUNTERS,
                objectives(new QuestObjective(QuestObjectiveType.KILL_MOB, "eclipse:dry_body", 1, "quest.eclipse.cacadores_cacada_sobrenatural.obj.0")),
                QuestReward.builder()
                        .factionXp(15)
                        .reputation(ReputationFaction.HUNTERS, 10)
                        .item("eclipse:holy_water", 2)
                        .build(),
                prereqs("cacadores_primeira_cacada"),
                false
        ));

        register(new Quest(
                "cacadores_entrega_prata",
                "quest.eclipse.cacadores_entrega_prata.title",
                "quest.eclipse.cacadores_entrega_prata.desc",
                null, FactionType.HUNTERS,
                objectives(new QuestObjective(QuestObjectiveType.COLLECT_ITEM, "eclipse:pure_silver", 4, "quest.eclipse.cacadores_entrega_prata.obj.0")),
                QuestReward.builder()
                        .factionXp(15)
                        .factionSkillPoints(1)
                        .reputation(ReputationFaction.HUNTERS, 10)
                        .reputation(ReputationFaction.VAMPIRES, -5)
                        .reputation(ReputationFaction.WEREWOLFS, -5)
                        .build(),
                prereqs("cacadores_cacada_sobrenatural"),
                false
        ));

        register(new Quest(
                "vampiro_sede_inicial",
                "quest.eclipse.vampiro_sede_inicial.title",
                "quest.eclipse.vampiro_sede_inicial.desc",
                RaceType.VAMPIRE, null,
                objectives(new QuestObjective(QuestObjectiveType.KILL_MOB, "minecraft:spider", 3, "quest.eclipse.vampiro_sede_inicial.obj.0")),
                QuestReward.builder()
                        .racialXp(15)
                        .affinity(AffinityType.VAMPIRIC, 5)
                        .build(),
                null,
                false
        ));

        register(new Quest(
                "lobisomem_primeira_presa",
                "quest.eclipse.lobisomem_primeira_presa.title",
                "quest.eclipse.lobisomem_primeira_presa.desc",
                RaceType.WEREWOLF, null,
                objectives(new QuestObjective(QuestObjectiveType.KILL_MOB, "minecraft:skeleton", 3, "quest.eclipse.lobisomem_primeira_presa.obj.0")),
                QuestReward.builder()
                        .racialXp(15)
                        .affinity(AffinityType.LUPINE, 5)
                        .build(),
                null,
                false
        ));

        // CORREÇÃO: Quantidade ajustada para 3x Osso Corrompido (eclipse:corrupted_bone)
        register(new Quest(
                "cultistas_primeiro_sacrificio",
                "quest.eclipse.cultistas_primeiro_sacrificio.title",
                "quest.eclipse.cultistas_primeiro_sacrificio.desc",
                null, FactionType.CULTISTS,
                objectives(new QuestObjective(QuestObjectiveType.COLLECT_ITEM, "eclipse:corrupted_bone", 3, "quest.eclipse.cultistas_primeiro_sacrificio.obj.0")),
                QuestReward.builder()
                        .factionXp(15)
                        .reputation(ReputationFaction.CULTISTS, 10)
                        .reputation(ReputationFaction.HUNTERS, -5)
                        .joinFaction(FactionType.CULTISTS)
                        .build(),
                null,
                false,
                true
        ));
    }

    private static List<QuestObjective> objectives(QuestObjective... objectives) {
        return Arrays.asList(objectives);
    }

    private static List<String> prereqs(String... ids) {
        return Arrays.asList(ids);
    }

    private QuestRegistry() {
    }

    public static void register(Quest quest) {
        QUESTS.put(quest.getId(), quest);
    }

    public static Quest get(String id) {
        return QUESTS.get(id);
    }

    public static Collection<Quest> getAll() {
        return QUESTS.values();
    }
}