package com.soldierskull.eclipse.skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.stats.PlayerStats;

/**
 * Registro central de todos os nós de habilidade - de RAÇA e de FACÇÃO -
 * com os números exatos da especificação (nível, custo, tipo, energia,
 * cooldown, pré-requisito).
 *
 * Os nomes e descrições das habilidades utilizam chaves de tradução,
 * permitindo que o texto exibido acompanhe o idioma selecionado pelo jogador.
 */
public final class SkillTree {

    private static final Map<RaceType, Map<String, Skill>> RACE_TREES =
            new EnumMap<>(RaceType.class);

    private static final Map<FactionType, Map<String, Skill>> FACTION_TREES =
            new EnumMap<>(FactionType.class);

    private static long s(double seconds) {
        return (long) (seconds * 1000L);
    }

    static {
        for (RaceType race : RaceType.values()) {
            RACE_TREES.put(race, new LinkedHashMap<>());
        }

        for (FactionType faction : FactionType.values()) {
            FACTION_TREES.put(faction, new LinkedHashMap<>());
        }

        // ================= VAMPIRO =================

        registerRace(Skill.ofRace(
                "vampiro_sentidos_vampiricos",
                "skill.eclipse.vampiro_sentidos_vampiricos.name",
                "skill.eclipse.vampiro_sentidos_vampiricos.description",
                RaceType.VAMPIRE,
                SkillType.PASSIVE,
                1,
                1,
                null,
                0,
                0,
                null,
                "vampiric_sense"
        ));

        registerRace(Skill.ofRace(
                "vampiro_presas_vampiricas",
                "skill.eclipse.vampiro_presas_vampiricas.name",
                "skill.eclipse.vampiro_presas_vampiricas.description",
                RaceType.VAMPIRE,
                SkillType.ACTIVE,
                2,
                1,
                null,
                s(2),
                5,
                VampireSkillEffects.PRESAS_VAMPIRICAS,
                "vampire_fangs"
        ));

        registerRace(Skill.ofRace(
                "vampiro_visao_noturna",
                "skill.eclipse.vampiro_visao_noturna.name",
                "skill.eclipse.vampiro_visao_noturna.description",
                RaceType.VAMPIRE,
                SkillType.PASSIVE,
                2,
                1,
                list("vampiro_sentidos_vampiricos"),
                0,
                0,
                VampireSkillEffects.VISAO_NOTURNA,
                "night_vision"
        ));

        registerRace(Skill.ofRace(
                "vampiro_regeneracao_sanguinea",
                "skill.eclipse.vampiro_regeneracao_sanguinea.name",
                "skill.eclipse.vampiro_regeneracao_sanguinea.description",
                RaceType.VAMPIRE,
                SkillType.PASSIVE,
                3,
                2,
                null,
                0,
                0,
                null,
                "blood_regeneration"
        ));

        registerRace(Skill.ofRace(
                "vampiro_velocidade_predatoria",
                "skill.eclipse.vampiro_velocidade_predatoria.name",
                "skill.eclipse.vampiro_velocidade_predatoria.description",
                RaceType.VAMPIRE,
                SkillType.PASSIVE,
                3,
                1,
                null,
                0,
                0,
                SkillAttributeHandler.asEffect("vampiro_velocidade_predatoria"),
                "predatory_speed"
        ));

        registerRace(Skill.ofRace(
                "vampiro_passo_sombrio",
                "skill.eclipse.vampiro_passo_sombrio.name",
                "skill.eclipse.vampiro_passo_sombrio.description",
                RaceType.VAMPIRE,
                SkillType.ACTIVE,
                4,
                2,
                null,
                s(20),
                10,
                VampireSkillEffects.PASSO_SOMBRIO,
                "shadow_step"
        ));

        registerRace(Skill.ofRace(
                "vampiro_fome_de_sangue",
                "skill.eclipse.vampiro_fome_de_sangue.name",
                "skill.eclipse.vampiro_fome_de_sangue.description",
                RaceType.VAMPIRE,
                SkillType.ACTIVE,
                5,
                2,
                null,
                s(60),
                30,
                VampireSkillEffects.FOME_DE_SANGUE,
                "blood_frenzy"
        ));

        registerRace(Skill.ofRace(
                "vampiro_sangue_espesso",
                "skill.eclipse.vampiro_sangue_espesso.name",
                "skill.eclipse.vampiro_sangue_espesso.description",
                RaceType.VAMPIRE,
                SkillType.PASSIVE,
                5,
                1,
                null,
                0,
                0,
                null,
                "thick_blood"
        ));

        registerRace(Skill.ofRace(
                "vampiro_flecha_de_sangue",
                "skill.eclipse.vampiro_flecha_de_sangue.name",
                "skill.eclipse.vampiro_flecha_de_sangue.description",
                RaceType.VAMPIRE,
                SkillType.ACTIVE,
                6,
                2,
                null,
                s(10),
                15,
                VampireSkillEffects.FLECHA_DE_SANGUE,
                "blood_arrow"
        ));

        registerRace(Skill.ofRace(
                "vampiro_dominacao_sanguinea",
                "skill.eclipse.vampiro_dominacao_sanguinea.name",
                "skill.eclipse.vampiro_dominacao_sanguinea.description",
                RaceType.VAMPIRE,
                SkillType.ACTIVE,
                6,
                3,
                null,
                s(90),
                50,
                VampireSkillEffects.DOMINACAO_SANGUINEA,
                "blood_domination"
        ));

        registerRace(Skill.ofRace(
                "vampiro_forma_de_nevoa",
                "skill.eclipse.vampiro_forma_de_nevoa.name",
                "skill.eclipse.vampiro_forma_de_nevoa.description",
                RaceType.VAMPIRE,
                SkillType.TRANSFORMATION,
                7,
                3,
                null,
                s(90),
                25,
                VampireSkillEffects.FORMA_DE_NEVOA,
                "mist_form"
        ));

        registerRace(Skill.ofRace(
                "vampiro_regeneracao_superior",
                "skill.eclipse.vampiro_regeneracao_superior.name",
                "skill.eclipse.vampiro_regeneracao_superior.description",
                RaceType.VAMPIRE,
                SkillType.PASSIVE,
                8,
                3,
                list("vampiro_regeneracao_sanguinea"),
                0,
                0,
                null,
                "superior_regeneration"
        ));

        registerRace(Skill.ofRace(
                "vampiro_hipnose",
                "skill.eclipse.vampiro_hipnose.name",
                "skill.eclipse.vampiro_hipnose.description",
                RaceType.VAMPIRE,
                SkillType.ACTIVE,
                9,
                3,
                null,
                s(100),
                0,
                VampireSkillEffects.HIPNOSE,
                "hypnosis"
        ));

        registerRace(Skill.ofRace(
                "vampiro_velocidade_sobrenatural",
                "skill.eclipse.vampiro_velocidade_sobrenatural.name",
                "skill.eclipse.vampiro_velocidade_sobrenatural.description",
                RaceType.VAMPIRE,
                SkillType.ACTIVE,
                10,
                3,
                list("vampiro_velocidade_predatoria"),
                s(40),
                10,
                VampireSkillEffects.VELOCIDADE_SOBRENATURAL,
                "supernatural_speed"
        ));

        registerRace(Skill.ofRace(
                "vampiro_senhor_do_sangue",
                "skill.eclipse.vampiro_senhor_do_sangue.name",
                "skill.eclipse.vampiro_senhor_do_sangue.description",
                RaceType.VAMPIRE,
                SkillType.ACTIVE,
                11,
                4,
                null,
                s(200),
                75,
                VampireSkillEffects.SENHOR_DO_SANGUE,
                "lord_of_blood"
        ));

        registerRace(Skill.ofRace(
                "vampiro_ascensao_vampirica",
                "skill.eclipse.vampiro_ascensao_vampirica.name",
                "skill.eclipse.vampiro_ascensao_vampirica.description",
                RaceType.VAMPIRE,
                SkillType.TRANSFORMATION,
                12,
                5,
                null,
                s(300),
                100,
                VampireSkillEffects.ASCENSAO_VAMPIRICA,
                "vampiric_ascension"
        ));

        // ================= LOBISOMEM =================

        registerRace(Skill.ofRace(
                "lobisomem_instinto_bestial",
                "skill.eclipse.lobisomem_instinto_bestial.name",
                "skill.eclipse.lobisomem_instinto_bestial.description",
                RaceType.WEREWOLF,
                SkillType.PASSIVE,
                2,
                1,
                null,
                0,
                0,
                null,
                "beast_instinct"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_garras_bestiais",
                "skill.eclipse.lobisomem_garras_bestiais.name",
                "skill.eclipse.lobisomem_garras_bestiais.description",
                RaceType.WEREWOLF,
                SkillType.PASSIVE,
                2,
                1,
                null,
                0,
                0,
                null,
                "bestial_claws"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_olfato_predatorio",
                "skill.eclipse.lobisomem_olfato_predatorio.name",
                "skill.eclipse.lobisomem_olfato_predatorio.description",
                RaceType.WEREWOLF,
                SkillType.PASSIVE,
                2,
                1,
                null,
                0,
                0,
                null,
                "predatory_smell"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_forca_bestial",
                "skill.eclipse.lobisomem_forca_bestial.name",
                "skill.eclipse.lobisomem_forca_bestial.description",
                RaceType.WEREWOLF,
                SkillType.PASSIVE,
                3,
                2,
                null,
                0,
                0,
                SkillAttributeHandler.asEffect("lobisomem_forca_bestial"),
                "bestial_strength"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_resistencia_bestial",
                "skill.eclipse.lobisomem_resistencia_bestial.name",
                "skill.eclipse.lobisomem_resistencia_bestial.description",
                RaceType.WEREWOLF,
                SkillType.PASSIVE,
                3,
                1,
                null,
                0,
                0,
                null,
                "bestial_resistance"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_investida",
                "skill.eclipse.lobisomem_investida.name",
                "skill.eclipse.lobisomem_investida.description",
                RaceType.WEREWOLF,
                SkillType.ACTIVE,
                4,
                2,
                null,
                s(20),
                10,
                WerewolfSkillEffects.INVESTIDA,
                "charge"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_furia",
                "skill.eclipse.lobisomem_furia.name",
                "skill.eclipse.lobisomem_furia.description",
                RaceType.WEREWOLF,
                SkillType.ACTIVE,
                5,
                2,
                null,
                s(60),
                25,
                WerewolfSkillEffects.FURIA,
                "fury"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_regeneracao_bestial",
                "skill.eclipse.lobisomem_regeneracao_bestial.name",
                "skill.eclipse.lobisomem_regeneracao_bestial.description",
                RaceType.WEREWOLF,
                SkillType.PASSIVE,
                5,
                2,
                null,
                0,
                0,
                null,
                "bestial_regeneration"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_rugido_intimidador",
                "skill.eclipse.lobisomem_rugido_intimidador.name",
                "skill.eclipse.lobisomem_rugido_intimidador.description",
                RaceType.WEREWOLF,
                SkillType.ACTIVE,
                6,
                2,
                null,
                s(60),
                40,
                WerewolfSkillEffects.RUGIDO_INTIMIDADOR,
                "intimidating_roar"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_transformacao_parcial",
                "skill.eclipse.lobisomem_transformacao_parcial.name",
                "skill.eclipse.lobisomem_transformacao_parcial.description",
                RaceType.WEREWOLF,
                SkillType.TRANSFORMATION,
                7,
                3,
                null,
                0,
                50,
                WerewolfSkillEffects.TRANSFORMACAO_PARCIAL,
                "partial_transformation"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_salto_predatorio",
                "skill.eclipse.lobisomem_salto_predatorio.name",
                "skill.eclipse.lobisomem_salto_predatorio.description",
                RaceType.WEREWOLF,
                SkillType.ACTIVE,
                8,
                2,
                null,
                s(30),
                0,
                WerewolfSkillEffects.SALTO_PREDATORIO,
                "predatory_leap"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_furia_lunar",
                "skill.eclipse.lobisomem_furia_lunar.name",
                "skill.eclipse.lobisomem_furia_lunar.description",
                RaceType.WEREWOLF,
                SkillType.ACTIVE,
                9,
                3,
                null,
                0,
                60,
                WerewolfSkillEffects.FURIA_LUNAR,
                "lunar_fury"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_pele_de_ferro",
                "skill.eclipse.lobisomem_pele_de_ferro.name",
                "skill.eclipse.lobisomem_pele_de_ferro.description",
                RaceType.WEREWOLF,
                SkillType.PASSIVE,
                10,
                3,
                null,
                0,
                0,
                SkillAttributeHandler.asEffect("lobisomem_pele_de_ferro"),
                "iron_skin"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_predador_alfa",
                "skill.eclipse.lobisomem_predador_alfa.name",
                "skill.eclipse.lobisomem_predador_alfa.description",
                RaceType.WEREWOLF,
                SkillType.PASSIVE,
                11,
                4,
                null,
                0,
                0,
                SkillAttributeHandler.asEffect("lobisomem_predador_alfa"),
                "alpha_predator"
        ));

        registerRace(Skill.ofRace(
                "lobisomem_forma_alfa",
                "skill.eclipse.lobisomem_forma_alfa.name",
                "skill.eclipse.lobisomem_forma_alfa.description",
                RaceType.WEREWOLF,
                SkillType.TRANSFORMATION,
                12,
                5,
                null,
                s(300),
                100,
                WerewolfSkillEffects.FORMA_ALFA,
                "alpha_form"
        ));

        // ================= CAÇADORES =================

        registerFaction(Skill.ofFaction(
                "cacadores_conhecimento_de_monstros",
                "skill.eclipse.cacadores_conhecimento_de_monstros.name",
                "skill.eclipse.cacadores_conhecimento_de_monstros.description",
                FactionType.HUNTERS,
                SkillType.PASSIVE,
                1,
                1,
                null,
                0,
                0,
                null,
                "monster_knowledge"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_treinamento_com_prata",
                "skill.eclipse.cacadores_treinamento_com_prata.name",
                "skill.eclipse.cacadores_treinamento_com_prata.description",
                FactionType.HUNTERS,
                SkillType.PASSIVE,
                2,
                1,
                null,
                0,
                0,
                null,
                "silver_training"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_rastreador",
                "skill.eclipse.cacadores_rastreador.name",
                "skill.eclipse.cacadores_rastreador.description",
                FactionType.HUNTERS,
                SkillType.PASSIVE,
                2,
                1,
                null,
                0,
                0,
                null,
                "tracker"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_golpe_preciso",
                "skill.eclipse.cacadores_golpe_preciso.name",
                "skill.eclipse.cacadores_golpe_preciso.description",
                FactionType.HUNTERS,
                SkillType.ACTIVE,
                3,
                2,
                null,
                s(15),
                25,
                HunterSkillEffects.GOLPE_PRECISO,
                "precise_strike"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_armadilha_de_cacador",
                "skill.eclipse.cacadores_armadilha_de_cacador.name",
                "skill.eclipse.cacadores_armadilha_de_cacador.description",
                FactionType.HUNTERS,
                SkillType.ACTIVE,
                4,
                2,
                null,
                s(30),
                33,
                HunterSkillEffects.ARMADILHA,
                "hunters_trap"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_preparacao_alquimica",
                "skill.eclipse.cacadores_preparacao_alquimica.name",
                "skill.eclipse.cacadores_preparacao_alquimica.description",
                FactionType.HUNTERS,
                SkillType.PASSIVE,
                5,
                2,
                null,
                0,
                0,
                HunterSkillEffects.PREPARACAO_ALQUIMICA,
                "alchemical_preparation"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_marca_do_cacador",
                "skill.eclipse.cacadores_marca_do_cacador.name",
                "skill.eclipse.cacadores_marca_do_cacador.description",
                FactionType.HUNTERS,
                SkillType.ACTIVE,
                6,
                2,
                null,
                s(45),
                50,
                HunterSkillEffects.MARCA_DO_CACADOR,
                "hunters_mark"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_armadilha_de_prata",
                "skill.eclipse.cacadores_armadilha_de_prata.name",
                "skill.eclipse.cacadores_armadilha_de_prata.description",
                FactionType.HUNTERS,
                SkillType.ACTIVE,
                7,
                3,
                null,
                s(45),
                58,
                HunterSkillEffects.ARMADILHA,
                "silver_trap"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_conhecimento_anatomico",
                "skill.eclipse.cacadores_conhecimento_anatomico.name",
                "skill.eclipse.cacadores_conhecimento_anatomico.description",
                FactionType.HUNTERS,
                SkillType.PASSIVE,
                8,
                2,
                null,
                0,
                0,
                null,
                "anatomical_knowledge"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_municao_especial",
                "skill.eclipse.cacadores_municao_especial.name",
                "skill.eclipse.cacadores_municao_especial.description",
                FactionType.HUNTERS,
                SkillType.PASSIVE,
                9,
                3,
                null,
                0,
                0,
                HunterSkillEffects.MUNICAO_ESPECIAL,
                "special_ammunition"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_ritual_de_contencao",
                "skill.eclipse.cacadores_ritual_de_contencao.name",
                "skill.eclipse.cacadores_ritual_de_contencao.description",
                FactionType.HUNTERS,
                SkillType.ACTIVE,
                10,
                3,
                null,
                s(120),
                83,
                HunterSkillEffects.RITUAL_DE_CONTENCAO,
                "containment_ritual"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_mestre_cacador",
                "skill.eclipse.cacadores_mestre_cacador.name",
                "skill.eclipse.cacadores_mestre_cacador.description",
                FactionType.HUNTERS,
                SkillType.PASSIVE,
                11,
                4,
                null,
                0,
                0,
                null,
                "master_hunter"
        ));

        registerFaction(Skill.ofFaction(
                "cacadores_arsenal_do_exterminador",
                "skill.eclipse.cacadores_arsenal_do_exterminador.name",
                "skill.eclipse.cacadores_arsenal_do_exterminador.description",
                FactionType.HUNTERS,
                SkillType.ACTIVE,
                12,
                5,
                null,
                s(300),
                100,
                HunterSkillEffects.ARSENAL_DO_EXTERMINADOR,
                "exterminators_arsenal"
        ));

        // ================= CULTISTAS =================

        registerFaction(Skill.ofFaction(
                "cultistas_conhecimento_proibido",
                "skill.eclipse.cultistas_conhecimento_proibido.name",
                "skill.eclipse.cultistas_conhecimento_proibido.description",
                FactionType.CULTISTS,
                SkillType.PASSIVE,
                1,
                1,
                null,
                0,
                0,
                null,
                "forbidden_knowledge"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_marca_do_culto",
                "skill.eclipse.cultistas_marca_do_culto.name",
                "skill.eclipse.cultistas_marca_do_culto.description",
                FactionType.CULTISTS,
                SkillType.PASSIVE,
                2,
                1,
                null,
                0,
                0,
                null,
                "cult_mark"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_sussurros_do_abismo",
                "skill.eclipse.cultistas_sussurros_do_abismo.name",
                "skill.eclipse.cultistas_sussurros_do_abismo.description",
                FactionType.CULTISTS,
                SkillType.ACTIVE,
                2,
                1,
                null,
                0,
                0,
                null,
                "whispers_of_the_abyss"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_pequeno_ritual",
                "skill.eclipse.cultistas_pequeno_ritual.name",
                "skill.eclipse.cultistas_pequeno_ritual.description",
                FactionType.CULTISTS,
                SkillType.ACTIVE,
                3,
                2,
                null,
                0,
                0,
                null,
                "minor_ritual"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_sacrificio",
                "skill.eclipse.cultistas_sacrificio.name",
                "skill.eclipse.cultistas_sacrificio.description",
                FactionType.CULTISTS,
                SkillType.ACTIVE,
                4,
                2,
                null,
                0,
                0,
                CultistSkillEffects.SACRIFICIO,
                "sacrifice"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_corrupcao_controlada",
                "skill.eclipse.cultistas_corrupcao_controlada.name",
                "skill.eclipse.cultistas_corrupcao_controlada.description",
                FactionType.CULTISTS,
                SkillType.PASSIVE,
                5,
                2,
                null,
                0,
                0,
                CultistSkillEffects.CORRUPCAO_CONTROLADA,
                "controlled_corruption"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_ritual_de_invocacao",
                "skill.eclipse.cultistas_ritual_de_invocacao.name",
                "skill.eclipse.cultistas_ritual_de_invocacao.description",
                FactionType.CULTISTS,
                SkillType.ACTIVE,
                6,
                3,
                null,
                0,
                0,
                CultistSkillEffects.RITUAL_DE_INVOCACAO,
                "summoning_ritual"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_pacto_abissal",
                "skill.eclipse.cultistas_pacto_abissal.name",
                "skill.eclipse.cultistas_pacto_abissal.description",
                FactionType.CULTISTS,
                SkillType.ACTIVE,
                7,
                3,
                null,
                0,
                0,
                CultistSkillEffects.PACTO_ABISSAL,
                "abyssal_pact"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_ritual_maior",
                "skill.eclipse.cultistas_ritual_maior.name",
                "skill.eclipse.cultistas_ritual_maior.description",
                FactionType.CULTISTS,
                SkillType.ACTIVE,
                8,
                3,
                null,
                0,
                0,
                null,
                "greater_ritual"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_olho_do_abismo",
                "skill.eclipse.cultistas_olho_do_abismo.name",
                "skill.eclipse.cultistas_olho_do_abismo.description",
                FactionType.CULTISTS,
                SkillType.PASSIVE,
                9,
                3,
                null,
                0,
                0,
                null,
                "eye_of_the_abyss"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_invocacao_superior",
                "skill.eclipse.cultistas_invocacao_superior.name",
                "skill.eclipse.cultistas_invocacao_superior.description",
                FactionType.CULTISTS,
                SkillType.ACTIVE,
                10,
                4,
                null,
                0,
                0,
                CultistSkillEffects.INVOCACAO_SUPERIOR,
                "superior_summoning"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_voz_do_abismo",
                "skill.eclipse.cultistas_voz_do_abismo.name",
                "skill.eclipse.cultistas_voz_do_abismo.description",
                FactionType.CULTISTS,
                SkillType.ACTIVE,
                11,
                4,
                null,
                0,
                0,
                null,
                "voice_of_the_abyss"
        ));

        registerFaction(Skill.ofFaction(
                "cultistas_ritual_do_eclipse",
                "skill.eclipse.cultistas_ritual_do_eclipse.name",
                "skill.eclipse.cultistas_ritual_do_eclipse.description",
                FactionType.CULTISTS,
                SkillType.ACTIVE,
                12,
                5,
                null,
                0,
                0,
                CultistSkillEffects.RITUAL_DO_ECLIPSE,
                "eclipse_ritual"
        ));
    }

    private static List<String> list(String... ids) {
        return Arrays.asList(ids);
    }

    private SkillTree() {
    }

    public static void registerRace(Skill skill) {
        RACE_TREES.get(skill.getRaceOwner()).put(skill.getId(), skill);
    }

    public static void registerFaction(Skill skill) {
        FACTION_TREES.get(skill.getFactionOwner()).put(skill.getId(), skill);
    }

    public static Skill getRaceSkill(RaceType race, String id) {
        return RACE_TREES.get(race).get(id);
    }

    public static Skill getFactionSkill(FactionType faction, String id) {
        return FACTION_TREES.get(faction).get(id);
    }

    /**
     * Busca por id em qualquer raça/facção.
     */
    public static Skill find(String id) {
        for (Map<String, Skill> tree : RACE_TREES.values()) {
            Skill skill = tree.get(id);

            if (skill != null) {
                return skill;
            }
        }

        for (Map<String, Skill> tree : FACTION_TREES.values()) {
            Skill skill = tree.get(id);

            if (skill != null) {
                return skill;
            }
        }

        return null;
    }

    public static Collection<Skill> getAllForRace(RaceType race) {
        return RACE_TREES.get(race).values();
    }

    public static Collection<Skill> getAllForFaction(FactionType faction) {
        return FACTION_TREES.get(faction).values();
    }

    /**
     * Skills de raça que o jogador poderia desbloquear agora,
     * mas ainda não desbloqueou.
     */
    public static List<Skill> getUnlockableRaceSkills(PlayerStats stats) {
        List<Skill> result = new ArrayList<>();

        for (Skill skill : RACE_TREES.get(stats.getRace()).values()) {
            if (!stats.hasUnlockedSkill(skill.getId()) && skill.canUnlock(stats)) {
                result.add(skill);
            }
        }

        return result;
    }

    /**
     * Skills de facção que o jogador poderia desbloquear agora,
     * mas ainda não desbloqueou.
     */
    public static List<Skill> getUnlockableFactionSkills(PlayerStats stats) {
        List<Skill> result = new ArrayList<>();

        for (Skill skill : FACTION_TREES.get(stats.getFaction()).values()) {
            if (!stats.hasUnlockedSkill(skill.getId()) && skill.canUnlock(stats)) {
                result.add(skill);
            }
        }

        return result;
    }
}