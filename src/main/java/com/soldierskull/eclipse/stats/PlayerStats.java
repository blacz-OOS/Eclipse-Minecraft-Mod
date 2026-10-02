package com.soldierskull.eclipse.stats;

import java.util.EnumMap;
import net.minecraft.nbt.CompoundNBT;
import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.race.RaceProgression;
import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.faction.FactionProgression;

/**
 * Holds all persistent RPG-style data for a player: level/XP, distributable
 * attributes, the two energy resources (Common and Abyssal), every
 * Affinity, and every Reputation.
 *
 * This class only stores data and contains the pure game-rule logic
 * (XP curve, point spending, regen, conversion, reputation interlink).
 * It never touches the player entity directly - applying the numbers to
 * real Minecraft attributes is done by {@link StatAttributeHandler}, and
 * network sync is done by the packet classes. This keeps responsibilities
 * separated as requested in the task.
 */
public class PlayerStats {

    // Todos os valores de balanceamento (taxas, maximos, custos) vivem em
    // StatBalance - nao declare constantes de balanceamento aqui de novo.

    // Affinity and Reputation are both fixed 0-100 scales.
    private static final int AFFINITY_REPUTATION_MIN = 0;
    private static final int AFFINITY_REPUTATION_MAX = 100;

    private int level = 1;
    private int currentXp = 0;
    private int maxXp = 100;
    private int pointsToDistribute = 0;

    private final EnumMap<AttributeType, Integer> attributes = new EnumMap<>(AttributeType.class);

    private int commonEnergy = StatBalance.COMMON_ENERGY_BASE_MAX;
    private int commonEnergyMax = StatBalance.COMMON_ENERGY_BASE_MAX;
    private int corruption = 0;
    private int corruptionMax = StatBalance.CORRUPTION_BASE_MAX;

    // Fractional regen accumulators. Intentionally NOT persisted - losing
    // a fraction of a second of regen on world reload is not noticeable.
    private float hpRegenAccumulator = 0F;
    private float commonEnergyRegenAccumulator = 0F;

    // ---- Affinity / Reputation storage --------------------------------
    // EnumMap, keyed by the enum itself, so adding a new AffinityType or
    // ReputationFaction constant is enough to make it "exist" here - no
    // other line in this class needs to change. Missing entries default
    // to 0 via getOrDefault() in the getters below, so a freshly-added
    // enum constant is simply 0% for players who already have a save.
    private final EnumMap<AffinityType, Integer> affinities = new EnumMap<>(AffinityType.class);
    private final EnumMap<ReputationFaction, Integer> reputations = new EnumMap<>(ReputationFaction.class);

    // ---- Race / racial progression --------------------------------------
    // Todo jogador comeca HUMANO (regra 14 do doc de design). Nivel/XP
    // raciais sao TOTALMENTE separados do nivel/XP gerais acima - por
    // decisao de design confirmada, nao compartilham nenhuma variavel.
    // O rank (ex.: "Duque", "Alfa") NUNCA e guardado - e sempre derivado
    // on-the-fly via RaceProgression.getRank(race, racialLevel), entao a
    // tabela de ranks so precisa ser editada em um lugar (RaceProgression).
    //
    // Ainda em aberto (nao inventar sem confirmar): a transformacao real
    // (Fase 9, gatilho = 50% afinidade + Abismo) ainda nao chama
    // setRace()/addRacialXp() automaticamente - por enquanto esses metodos
    // so sao acionados via /eclipse race (admin/debug) e pelo item de
    // ritual de exemplo (BasicRitualItem). Nenhum efeito de gameplay por
    // raca (buffs, habilidades) existe ainda - isso e Fase 8/pilar de
    // arvore de habilidades.
    private RaceType race = RaceType.HUMAN;
    private int racialLevel = 0;
    private int racialXp = 0;
    private int racialMaxXp = RaceProgression.getMaxXpFor(1);

    // ---- Faction / faction progression -----------------------------------
    // Mesmo padrão do bloco de raça acima, mas para Facção (sistema
    // independente - ver FactionType). Padrão é NENHUMA (nem todo jogador
    // pertence a uma facção). Fonte de XP de facção ainda em aberto.
    private FactionType faction = FactionType.NONE;
    private int factionLevel = 0;
    private int factionXp = 0;
    private int factionMaxXp = FactionProgression.getMaxXpFor(1);

    // ---- Skills -----------------------------------------------------------
    // Pontos raciais: +1 por nivel racial (CONFIRMADO pela especificacao
    // consolidada, tabela nivel->pontos acumulados, sem bonus extra em
    // rank-up). Pontos de faccao: mesmo esquema (1 por nivel de faccao) -
    // isto e uma INFERENCIA a partir de "XP de faccao segue o mesmo
    // esquema do racial" (nao foi dito explicitamente sobre PONTOS de
    // habilidade de faccao, so sobre a fonte de XP) - confirme se estiver
    // errado. Pools sao independentes: skillPoints so gasta em skills de
    // RACA, factionSkillPoints so em skills de FACCAO.
    private static final int SKILL_POINTS_PER_RACIAL_LEVEL = 1;
    private static final int SKILL_POINTS_PER_FACTION_LEVEL = 1;
    private int skillPoints = 0;
    private int factionSkillPoints = 0;
    private final java.util.Set<String> unlockedSkills = new java.util.LinkedHashSet<>();

    // Cooldown de skills ATIVAS/TRANSFORMACAO, persistente por design
    // (regra confirmada: "cooldown deve persistir", contando tambem
    // tempo offline). Guarda skillId -> epoch millis (System.currentTimeMillis())
    // em que a skill volta a ficar disponivel - NUNCA um "tempo restante",
    // exatamente pela razao explicada na especificacao (tempo restante
    // fica inconsistente se o servidor ficar desligado por muito tempo).
    private final java.util.Map<String, Long> skillCooldownEnd = new java.util.HashMap<>();

    // ---- Transformação (Fase 9) --------------------------------------
    // Ver com.soldierskull.eclipse.transformation.TransformationManager
    // para a lógica de gatilho/progresso. Aqui só o armazenamento: raça
    // alvo (null-safe via HUMANO = "nenhuma transformação em curso") e
    // o dia de Minecraft (mundo) em que ela começou.
    private RaceType transformationTargetRace = RaceType.HUMAN;
    private long transformationStartDay = -1L;
    /** True assim que o jogador sai do Abismo uma vez durante a transformação em curso - usado pra conquista "O Corrompido". */
    private boolean transformationLeftAbyss = false;

    /** True enquanto o bônus de +15 no máximo de Corrupção da Máscara do Culto está aplicado - ver CultistMaskHandler. */
    private boolean cultistMaskBonus = false;

    // ---- Quests (Fase 11) --------------------------------------------
    // Ids das quests ativas (aceitas, nao entregues) e completas
    // (entregues - usado tambem pra checar pre-requisito de cadeia via
    // Quest.meetsRequirements()). Progresso por objetivo (so precisa pra
    // KILL_MOB - COLLECT_ITEM e checado ao vivo no inventario, nao tem
    // contador salvo) fica em questObjectiveProgress, chave composta
    // "questId#indiceDoObjetivo".
    private final java.util.Set<String> activeQuests = new java.util.LinkedHashSet<>();
    private final java.util.Set<String> completedQuests = new java.util.LinkedHashSet<>();
    private final java.util.Map<String, Integer> questObjectiveProgress = new java.util.HashMap<>();

    // ---- Sistema de Rituais (Bloco E) -----------------------------------
    // Ids (ResourceLocation.toString()) dos rituais conhecidos - rituais de
    // progressao racial/faccao sao adicionados automaticamente quando o
    // requisito de afinidade/reputacao bate (ver RitualManager); rituais
    // GENERAL vem de QuestReward.ritualsUnlocked.
    private final java.util.Set<String> knownRituals = new java.util.LinkedHashSet<>();

    // ---- Reputation interlink table ------------------------------------
    // For each faction, the list of "rivals" that LOSE reputation when
    // that faction GAINS reputation (see addReputation() below for the
    // exact rule). This is the only place the design doc's relationship
    // table lives - to change who is rivals with whom, edit only here.
    private static final EnumMap<ReputationFaction, ReputationFaction[]> REPUTATION_RIVALS =
            new EnumMap<>(ReputationFaction.class);

    static {
        REPUTATION_RIVALS.put(ReputationFaction.HUNTERS,
                new ReputationFaction[]{ReputationFaction.VAMPIRES, ReputationFaction.WEREWOLFS});
        REPUTATION_RIVALS.put(ReputationFaction.VAMPIRES,
                new ReputationFaction[]{ReputationFaction.HUNTERS, ReputationFaction.WEREWOLFS});
        REPUTATION_RIVALS.put(ReputationFaction.WEREWOLFS,
                new ReputationFaction[]{ReputationFaction.HUNTERS, ReputationFaction.VAMPIRES});
        // Cultistas is the odd one out in the design doc: gaining Cultista
        // reputation drops ALL THREE other factions, but none of the other
        // three drop Cultistas in return.
        REPUTATION_RIVALS.put(ReputationFaction.CULTISTS,
                new ReputationFaction[]{ReputationFaction.HUNTERS, ReputationFaction.VAMPIRES, ReputationFaction.WEREWOLFS});
    }

    public void addXp(int amount) {
        this.currentXp += amount;
        while (this.currentXp >= this.maxXp) {
            this.currentXp -= this.maxXp;
            this.level++;
            this.pointsToDistribute += 3;
            this.maxXp = (int) (this.maxXp * 1.5D);
        }
    }

    /** Diretamente define o XP atual (sem disparar level-up). Usado por comandos. */
    public void setXp(int value) {
        this.currentXp = Math.max(0, value);
    }

    /** Remove XP (nunca abaixo de 0). Usado por comandos. */
    public void removeXp(int amount) {
        setXp(this.currentXp - amount);
    }

    /** Diretamente define o nível (mínimo 1). Usado por comandos. */
    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public void addLevel(int amount) {
        setLevel(this.level + amount);
    }

    public void removeLevel(int amount) {
        setLevel(this.level - amount);
    }

    /** Diretamente define os pontos de atributo disponíveis (mínimo 0). Usado por comandos. */
    public void setPoints(int value) {
        this.pointsToDistribute = Math.max(0, value);
    }

    public void addPoints(int amount) {
        setPoints(this.pointsToDistribute + amount);
    }

    public void removePoints(int amount) {
        setPoints(this.pointsToDistribute - amount);
    }

    /**
     * Define diretamente o valor de um atributo (STRENGTH, DEFENSE, etc.),
     * ignorando pointsToDistribute. Diferente de addPointTo(), que gasta um
     * ponto de distribuição - este método é para uso administrativo/comandos.
     */
    public void setAttribute(AttributeType type, int value) {
        this.attributes.put(type, Math.max(0, value));
        if (type == AttributeType.ENERGY) {
            recalculateCommonEnergyMax();
        }
    }

    public void addAttribute(AttributeType type, int amount) {
        setAttribute(type, getAttribute(type) + amount);
    }

    public void removeAttribute(AttributeType type, int amount) {
        setAttribute(type, getAttribute(type) - amount);
    }

    /**
     * Spends one attribute point on the given stat, if any are available.
     * Returns true if a point was actually spent (caller should re-apply
     * real attributes and sync to client when this returns true).
     */
    public boolean addPointTo(AttributeType type) {
        if (this.pointsToDistribute <= 0) {
            return false;
        }
        attributes.merge(type, 1, Integer::sum);
        this.pointsToDistribute--;
        if (type == AttributeType.ENERGY) {
            recalculateCommonEnergyMax();
        }
        return true;
    }

    /** Current value (0 se nunca gastou ponto aqui) para o atributo dado. */


    private void recalculateCommonEnergyMax() {
        this.commonEnergyMax = StatBalance.COMMON_ENERGY_BASE_MAX + getAttribute(AttributeType.ENERGY) * StatBalance.COMMON_ENERGY_MAX_PER_ENERGY;
        if (this.commonEnergy > this.commonEnergyMax) {
            this.commonEnergy = this.commonEnergyMax;
        }
    }

    /**
     * Called once per second (every 20 ticks) on the server for the owning
     * player. Applies natural HP regen (Constitution) and Common Energy
     * regen (Energy). Does not touch the entity directly except for
     * healing, which the caller is expected to gate on isAlive()/isAlive.
     */
    public float tickHpRegenAndGetHealAmount() {
        this.hpRegenAccumulator += getAttribute(AttributeType.CONSTITUTION) * StatBalance.HP_REGEN_PER_CONSTITUTION;
        if (this.hpRegenAccumulator >= 1F) {
            int wholeHeal = (int) this.hpRegenAccumulator;
            this.hpRegenAccumulator -= wholeHeal;
            return wholeHeal;
        }
        return 0F;
    }

    public void tickCommonEnergyRegen() {
        if (this.commonEnergy >= this.commonEnergyMax) {
            // Ja cheio: nao deixa o acumulador crescer sem limite enquanto
            // espera, senao um "estouro" de regen aconteceria assim que o
            // jogador gastasse energia.
            this.commonEnergyRegenAccumulator = 0F;
            return;
        }
        // Regen passivo: uma base fixa sempre ativa, mais um bonus por ponto
        // investido no atributo Energy. Antes disso dependia inteiramente do
        // atributo, entao um jogador sem pontos em Energy nunca regenerava.
        float regenRate = StatBalance.COMMON_ENERGY_PASSIVE_REGEN_BASE
                + getAttribute(AttributeType.ENERGY) * StatBalance.COMMON_ENERGY_REGEN_PER_ENERGY;
        this.commonEnergyRegenAccumulator += regenRate;
        if (this.commonEnergyRegenAccumulator >= 1F) {
            int wholeGain = (int) this.commonEnergyRegenAccumulator;
            this.commonEnergyRegenAccumulator -= wholeGain;
            this.commonEnergy = Math.min(this.commonEnergyMax, this.commonEnergy + wholeGain);
        }
    }

    /**
     * Spends Corruption (Corruption) to permanently raise the Common
     * Energy MAXIMUM by CORRUPTION_TO_COMMON_MAX_GAIN. Does NOT fill current
     * Common Energy - it's a cap upgrade, not a refill. Never allows the
     * reverse conversion.
     */
    public boolean convertAbyssalToCommon() {
        if (this.corruption < StatBalance.CORRUPTION_TO_COMMON_COST) {
            return false;
        }
        this.corruption -= StatBalance.CORRUPTION_TO_COMMON_COST;
        this.commonEnergyMax += StatBalance.CORRUPTION_TO_COMMON_MAX_GAIN;
        return true;
    }

    /** Used by the Abyss Shard item (temporary restoration method). */
    public boolean restoreCorruption(int amount) {
        if (this.corruption >= this.corruptionMax) {
            return false;
        }
        this.corruption = Math.min(this.corruptionMax, this.corruption + amount);
        return true;
    }

    /** Diretamente define a Common Energy, respeitando o máximo atual. Usado por comandos. */
    public void setCommonEnergy(int value) {
        this.commonEnergy = clamp(value, 0, this.commonEnergyMax);
    }

    public void addCommonEnergy(int amount) {
        setCommonEnergy(this.commonEnergy + amount);
    }

    public void removeCommonEnergy(int amount) {
        setCommonEnergy(this.commonEnergy - amount);
    }

    /** Diretamente define a Corruption, respeitando o máximo atual. Usado por comandos. */
    public void setCorruption(int value) {
        this.corruption = clamp(value, 0, this.corruptionMax);
    }

    public void addCorruption(int amount) {
        setCorruption(this.corruption + amount);
    }

    public void removeCorruption(int amount) {
        setCorruption(this.corruption - amount);
    }

    /** Aumenta permanentemente o maximo de Energia Abissal ("Corrupcao" - ver nota em CultistSkillEffects). Usado por Corrupcao Controlada. */
    public void addCorruptionMax(int amount) {
        this.corruptionMax = Math.max(0, this.corruptionMax + amount);
    }

    // ---- Race API ---------------------------------------------------------

    /** Raça atual do jogador. Nunca é null - o padrão é RaceType.HUMANO. */
    public RaceType getRace() {
        return this.race;
    }

    /**
     * Define a raça do jogador diretamente. Uso administrativo/debug (via
     * /eclipse race set) e pelo item de ritual de exemplo, enquanto o
     * gatilho real de transformação (Fase 9) não existe. NÃO reseta nível
     * ou XP racial - trocar de raça sem passar por clearRace() mantém a
     * progressão acumulada (decisão intencional: /eclipse race set é uma
     * ferramenta de teste, não deveria apagar progresso por acidente).
     *
     * REGRA CONFIRMADA: a facção Caçadores é composta APENAS por humanos.
     * Se o jogador está em Caçadores, só pode virar/continuar HUMANO -
     * tentar virar VAMPIRO/LOBISOMEM enquanto Caçador é rejeitado (a
     * decisão de negociar a saída da facção antes é do chamador, não
     * feita automaticamente aqui). Retorna false se a troca foi rejeitada.
     */
    public boolean setRace(RaceType race) {
        if (this.faction == FactionType.HUNTERS && race != RaceType.HUMAN) {
            return false;
        }
        this.race = race;
        return true;
    }

    /**
     * Reverte o jogador para HUMANO e ZERA nível/XP racial. Isto é uma
     * suposição de design (não especificada no documento) - se
     * "voltar a ser Humano" deve preservar a progressão racial para uma
     * eventual re-transformação, avise que este comportamento precisa
     * mudar.
     */
    public void clearRace() {
        this.race = RaceType.HUMAN;
        this.racialLevel = 0;
        this.racialXp = 0;
        this.racialMaxXp = RaceProgression.getMaxXpFor(1);
    }

    /** Rank atual (ex.: "Duque", "Alfa"), ou null se a raça não tem tabela de rank (hoje, só HUMANO). */
    public String getRacialRank() {
        return RaceProgression.getRank(this.race, this.racialLevel);
    }

    /**
     * Adiciona XP racial, subindo de nível racial (e recalculando
     * racialMaxXp via RaceProgression) até o limite de
     * RaceProgression.MAX_RACIAL_LEVEL. XP que sobraria além do nível
     * máximo é descartado (não acumula infinitamente).
     */
    public void addRacialXp(int amount) {
        if (this.racialLevel >= RaceProgression.MAX_RACIAL_LEVEL) {
            return;
        }
        this.racialXp += amount;
        while (this.racialXp >= this.racialMaxXp && this.racialLevel < RaceProgression.MAX_RACIAL_LEVEL) {
            this.racialXp -= this.racialMaxXp;
            this.racialLevel++;
            this.racialMaxXp = RaceProgression.getMaxXpFor(this.racialLevel + 1);
            this.skillPoints += SKILL_POINTS_PER_RACIAL_LEVEL;
        }
        if (this.racialLevel >= RaceProgression.MAX_RACIAL_LEVEL) {
            this.racialXp = 0;
        }
    }

    /** Diretamente define o XP racial atual (sem disparar level-up). Usado por comandos. */
    public void setRacialXp(int value) {
        this.racialXp = Math.max(0, value);
    }

    /** Remove XP racial (nunca abaixo de 0). Usado por comandos. */
    public void removeRacialXp(int amount) {
        setRacialXp(this.racialXp - amount);
    }

    /** Diretamente define o nível racial (0 a MAX_RACIAL_LEVEL). Usado por comandos. */
    public void setRacialLevel(int level) {
        this.racialLevel = Math.max(0, Math.min(RaceProgression.MAX_RACIAL_LEVEL, level));
        this.racialMaxXp = RaceProgression.getMaxXpFor(this.racialLevel + 1);
    }

    public void addRacialLevel(int amount) {
        setRacialLevel(this.racialLevel + amount);
    }

    public void removeRacialLevel(int amount) {
        setRacialLevel(this.racialLevel - amount);
    }

    // ---- Faction API --------------------------------------------------

    /** Facção atual do jogador. Nunca é null - padrão é FactionType.NENHUMA. */
    public FactionType getFaction() {
        return this.faction;
    }

    /**
     * Define a facção diretamente. Mantém nível/XP acumulados (mesma
     * lógica de setRace()).
     *
     * REGRA CONFIRMADA: Caçadores só aceita jogadores HUMANO - qualquer
     * outra raça é rejeitada (retorna false). Cultistas não tem essa
     * restrição: pode ser Vampiro/Lobisomem/Humano e Cultista ao mesmo
     * tempo, raça e facção são compatíveis livremente fora desse caso.
     */
    public boolean setFaction(FactionType faction) {
        if (faction == FactionType.HUNTERS && this.race != RaceType.HUMAN) {
            return false;
        }
        this.faction = faction;
        return true;
    }

    /** Reverte para NENHUMA e zera nível/XP de facção (mesma suposição feita em clearRace(), confirmada). */
    public void clearFaction() {
        this.faction = FactionType.NONE;
        this.factionLevel = 0;
        this.factionXp = 0;
        this.factionMaxXp = FactionProgression.getMaxXpFor(1);
    }

    /** Rank atual de facção (ex.: "Mestre", "Bispo"), ou null se NENHUMA. */
    public String getFactionRank() {
        return FactionProgression.getRank(this.faction, this.factionLevel);
    }

    public void addFactionXp(int amount) {
        if (this.factionLevel >= FactionProgression.MAX_FACTION_LEVEL) {
            return;
        }
        this.factionXp += amount;
        while (this.factionXp >= this.factionMaxXp && this.factionLevel < FactionProgression.MAX_FACTION_LEVEL) {
            this.factionXp -= this.factionMaxXp;
            this.factionLevel++;
            this.factionMaxXp = FactionProgression.getMaxXpFor(this.factionLevel + 1);
            this.factionSkillPoints += SKILL_POINTS_PER_FACTION_LEVEL;
        }
        if (this.factionLevel >= FactionProgression.MAX_FACTION_LEVEL) {
            this.factionXp = 0;
        }
    }

    public void setFactionXp(int value) {
        this.factionXp = Math.max(0, value);
    }

    public void removeFactionXp(int amount) {
        setFactionXp(this.factionXp - amount);
    }

    public void setFactionLevel(int level) {
        this.factionLevel = Math.max(0, Math.min(FactionProgression.MAX_FACTION_LEVEL, level));
        this.factionMaxXp = FactionProgression.getMaxXpFor(this.factionLevel + 1);
    }

    public void addFactionLevel(int amount) {
        setFactionLevel(this.factionLevel + amount);
    }

    public void removeFactionLevel(int amount) {
        setFactionLevel(this.factionLevel - amount);
    }

    // ---- Skills API -----------------------------------------------------

    public int getSkillPoints() {
        return this.skillPoints;
    }

    public void addSkillPoints(int amount) {
        this.skillPoints = Math.max(0, this.skillPoints + amount);
    }

    public void removeSkillPoints(int amount) {
        this.skillPoints = Math.max(0, this.skillPoints - amount);
    }

    public boolean hasUnlockedSkill(String skillId) {
        return this.unlockedSkills.contains(skillId);
    }

    /** Marca a skill como desbloqueada. NÃO cobra pontos nem valida nada - use SkillManager.unlock() para isso. */
    public void unlockSkill(String skillId) {
        this.unlockedSkills.add(skillId);
    }

    public java.util.Set<String> getUnlockedSkills() {
        return java.util.Collections.unmodifiableSet(this.unlockedSkills);
    }

    public int getFactionSkillPoints() {
        return this.factionSkillPoints;
    }

    public void addFactionSkillPoints(int amount) {
        this.factionSkillPoints = Math.max(0, this.factionSkillPoints + amount);
    }

    public void removeFactionSkillPoints(int amount) {
        this.factionSkillPoints = Math.max(0, this.factionSkillPoints - amount);
    }

    // ---- Cooldown de skills (persistente, epoch millis) ------------------

    /** Epoch millis em que a skill volta a ficar pronta. 0 = nunca usada / já pronta. */
    public long getSkillCooldownEnd(String skillId) {
        Long value = this.skillCooldownEnd.get(skillId);
        return value == null ? 0L : value;
    }

    public boolean isSkillReady(String skillId) {
        return System.currentTimeMillis() >= getSkillCooldownEnd(skillId);
    }

    public void setSkillCooldownEnd(String skillId, long epochMillis) {
        this.skillCooldownEnd.put(skillId, epochMillis);
    }

    public java.util.Map<String, Long> getAllSkillCooldowns() {
        return java.util.Collections.unmodifiableMap(this.skillCooldownEnd);
    }

    // ---- Transformação API (Fase 9) --------------------------------------

    public boolean isTransforming() {
        return this.transformationStartDay >= 0L;
    }

    public RaceType getTransformationTargetRace() {
        return this.transformationTargetRace;
    }

    public long getTransformationStartDay() {
        return this.transformationStartDay;
    }

    /** Inicia a contagem. Não valida nada (raça já Humana, afinidade, Abismo) - isso é responsabilidade do TransformationManager. */
    public void startTransformation(RaceType targetRace, long currentWorldDay) {
        this.transformationTargetRace = targetRace;
        this.transformationStartDay = currentWorldDay;
        this.transformationLeftAbyss = false;
    }

    /** Cancela/limpa uma transformação em curso, sem completá-la (a raça do jogador não muda). */
    public void clearTransformation() {
        this.transformationTargetRace = RaceType.HUMAN;
        this.transformationStartDay = -1L;
        this.transformationLeftAbyss = false;
    }

    public boolean didLeaveAbyssDuringTransformation() {
        return this.transformationLeftAbyss;
    }

    public void markLeftAbyssDuringTransformation() {
        this.transformationLeftAbyss = true;
    }

    public boolean hasCultistMaskBonus() {
        return this.cultistMaskBonus;
    }

    public void setCultistMaskBonus(boolean value) {
        this.cultistMaskBonus = value;
    }

    // ---- Sistema de Rituais API (Bloco E) --------------------------------

    public boolean knowsRitual(String ritualId) {
        return this.knownRituals.contains(ritualId);
    }

    public java.util.Set<String> getKnownRituals() {
        return java.util.Collections.unmodifiableSet(this.knownRituals);
    }

    public void learnRitual(String ritualId) {
        this.knownRituals.add(ritualId);
    }

    // ---- Quests API (Fase 11) --------------------------------------------

    public boolean hasActiveQuest(String questId) {
        return this.activeQuests.contains(questId);
    }

    public boolean hasCompletedQuest(String questId) {
        return this.completedQuests.contains(questId);
    }

    public java.util.Set<String> getActiveQuests() {
        return java.util.Collections.unmodifiableSet(this.activeQuests);
    }

    public java.util.Set<String> getCompletedQuests() {
        return java.util.Collections.unmodifiableSet(this.completedQuests);
    }

    public void startQuest(String questId) {
        this.activeQuests.add(questId);
    }

    /** Move a quest de ativa pra completa (não confere recompensa - isso é o QuestManager). */
    public void finishQuest(String questId) {
        this.activeQuests.remove(questId);
        this.completedQuests.add(questId);
        // limpa o progresso salvo dessa quest (objetivos futuros, se for repetivel, comecam do zero de novo)
        this.questObjectiveProgress.keySet().removeIf(key -> key.startsWith(questId + "#"));
    }

    /** Cancela uma quest ativa sem completar (não concede recompensa, não marca como completa). */
    public void abandonQuest(String questId) {
        this.activeQuests.remove(questId);
        this.questObjectiveProgress.keySet().removeIf(key -> key.startsWith(questId + "#"));
    }

    public int getQuestObjectiveProgress(String questId, int objectiveIndex) {
        Integer value = this.questObjectiveProgress.get(questId + "#" + objectiveIndex);
        return value == null ? 0 : value;
    }

    public void setQuestObjectiveProgress(String questId, int objectiveIndex, int value) {
        this.questObjectiveProgress.put(questId + "#" + objectiveIndex, value);
    }

    // ---- Affinity API ----------------------------------------------------

    /** Current Affinity (0-100) for the given race. Defaults to 0 if never set. */
    public int getAffinity(AffinityType type) {
        return this.affinities.getOrDefault(type, 0);
    }

    /**
     * Raises (or lowers, with a negative amount) Affinity for one race.
     * Always clamped to 0-100. This is the ONE method an item/event/quest
     * needs to call to change an Affinity - see ExampleAffinityItem.java
     * for a full working example.
     */
    public void addAffinity(AffinityType type, int amount) {
        int updated = clamp(getAffinity(type) + amount, AFFINITY_REPUTATION_MIN, AFFINITY_REPUTATION_MAX);
        this.affinities.put(type, updated);
    }

    /** Diretamente define a Afinidade (0-100) para a raça dada. Usado por comandos. */
    public void setAffinity(AffinityType type, int value) {
        this.affinities.put(type, clamp(value, AFFINITY_REPUTATION_MIN, AFFINITY_REPUTATION_MAX));
    }

    /** Remove Afinidade sem disparar nenhum efeito colateral. Usado por comandos. */
    public void removeAffinity(AffinityType type, int amount) {
        addAffinity(type, -amount);
    }

    // ---- Reputation API ----------------------------------------------------

    /** Current Reputation (0-100) for the given faction. Defaults to 0 if never set. */
    public int getReputation(ReputationFaction faction) {
        return this.reputations.getOrDefault(faction, 0);
    }

    /**
     * Raises (or lowers, with a negative amount) Reputation for one
     * faction.
     *
     * Interlink rule (per the design doc): a POSITIVE change also lowers
     * that faction's "rivals" (see REPUTATION_RIVALS above) by the exact
     * same amount, 1-for-1. A negative/zero change only affects the
     * target faction - the doc only specifies spreading for gains, not
     * for losses. Every value is clamped to 0-100 independently, so a
     * rival that's already at 0 simply stays at 0 instead of going
     * negative.
     */
    public void addReputation(ReputationFaction faction, int amount) {
        int updated = clamp(getReputation(faction) + amount, AFFINITY_REPUTATION_MIN, AFFINITY_REPUTATION_MAX);
        this.reputations.put(faction, updated);

        if (amount > 0) {
            ReputationFaction[] rivals = REPUTATION_RIVALS.get(faction);
            if (rivals != null) {
                for (ReputationFaction rival : rivals) {
                    int rivalUpdated = clamp(getReputation(rival) - amount, AFFINITY_REPUTATION_MIN, AFFINITY_REPUTATION_MAX);
                    this.reputations.put(rival, rivalUpdated);
                }
            }
        }
    }

    /**
     * Diretamente define a Reputação (0-100) para a facção dada, SEM disparar
     * o efeito de rivalidade (esse efeito só ocorre em addReputation com
     * amount positivo). Uso administrativo/comandos.
     */
    public void setReputation(ReputationFaction faction, int value) {
        this.reputations.put(faction, clamp(value, AFFINITY_REPUTATION_MIN, AFFINITY_REPUTATION_MAX));
    }

    /** Remove Reputação sem disparar o efeito de rivalidade. Usado por comandos. */
    public void removeReputation(ReputationFaction faction, int amount) {
        int updated = clamp(getReputation(faction) - amount, AFFINITY_REPUTATION_MIN, AFFINITY_REPUTATION_MAX);
        this.reputations.put(faction, updated);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /** Copies all persistent data from another instance (used on player death/clone). */
    public void copyFrom(PlayerStats old) {
        this.level = old.level;
        this.currentXp = old.currentXp;
        this.maxXp = old.maxXp;
        this.pointsToDistribute = old.pointsToDistribute;
        this.attributes.clear();
        this.attributes.putAll(old.attributes);
        this.commonEnergy = old.commonEnergy;
        this.commonEnergyMax = old.commonEnergyMax;
        this.corruption = old.corruption;
        this.corruptionMax = old.corruptionMax;
        this.affinities.clear();
        this.affinities.putAll(old.affinities);
        this.reputations.clear();
        this.reputations.putAll(old.reputations);
        this.race = old.race;
        this.racialLevel = old.racialLevel;
        this.racialXp = old.racialXp;
        this.racialMaxXp = old.racialMaxXp;
        this.faction = old.faction;
        this.factionLevel = old.factionLevel;
        this.factionXp = old.factionXp;
        this.factionMaxXp = old.factionMaxXp;
        this.skillPoints = old.skillPoints;
        this.factionSkillPoints = old.factionSkillPoints;
        this.unlockedSkills.clear();
        this.unlockedSkills.addAll(old.unlockedSkills);
        this.skillCooldownEnd.clear();
        this.skillCooldownEnd.putAll(old.skillCooldownEnd);
        this.transformationTargetRace = old.transformationTargetRace;
        this.transformationStartDay = old.transformationStartDay;
        this.transformationLeftAbyss = old.transformationLeftAbyss;
        this.cultistMaskBonus = old.cultistMaskBonus;
        this.activeQuests.clear();
        this.activeQuests.addAll(old.activeQuests);
        this.knownRituals.addAll(old.knownRituals);
        this.completedQuests.clear();
        this.completedQuests.addAll(old.completedQuests);
        this.questObjectiveProgress.clear();
        this.questObjectiveProgress.putAll(old.questObjectiveProgress);
    }

    public void loadFromNbt(CompoundNBT nbt) {
        this.level = nbt.getInt("Level");
        this.currentXp = nbt.getInt("Xp");
        this.maxXp = nbt.getInt("MaxXp");
        this.pointsToDistribute = nbt.getInt("Points");
        this.attributes.clear();
        for (AttributeType type : AttributeType.values()) {
            String key = "Attribute_" + type.name();
            this.attributes.put(type, nbt.contains(key) ? nbt.getInt(key) : 0);}
        this.commonEnergy = nbt.contains("CommonEnergy") ? nbt.getInt("CommonEnergy") : StatBalance.COMMON_ENERGY_BASE_MAX;
        // Le a chave nova; se nao existir (save de antes do rename pra
        // Corruption), cai pro nome antigo pra nao perder o progresso de
        // quem ja tinha mundo salvo.
        this.corruption = nbt.contains("Corruption") ? nbt.getInt("Corruption")
                : nbt.contains("AbyssalEnergy") ? nbt.getInt("AbyssalEnergy") : 0;
        this.corruptionMax = nbt.contains("CorruptionMax") ? nbt.getInt("CorruptionMax") : StatBalance.CORRUPTION_BASE_MAX;
        recalculateCommonEnergyMax();
        if (this.commonEnergy > this.commonEnergyMax) {
            this.commonEnergy = this.commonEnergyMax;
        }

        // One NBT key per enum constant ("Affinity_VAMPIRICA", etc.). A
        // constant added after this save simply won't have a matching key
        // yet, so it correctly loads as 0 via the contains() check.
        this.affinities.clear();
        for (AffinityType type : AffinityType.values()) {
            String key = "Affinity_" + type.name();
            this.affinities.put(type, nbt.contains(key) ? nbt.getInt(key) : 0);
        }

        this.reputations.clear();
        for (ReputationFaction faction : ReputationFaction.values()) {
            String key = "Reputation_" + faction.name();
            this.reputations.put(faction, nbt.contains(key) ? nbt.getInt(key) : 0);
        }

        // NOTA DE MIGRAÇÃO: em saves antigos, "Race" foi gravado como um
        // nome de AffinityType (VAMPIRICA/LUPINA), não de RaceType. Como
        // os nomes não batem (RaceType usa VAMPIRO/LOBISOMEM), o
        // valueOf() abaixo lançaria IllegalArgumentException para esses
        // saves - por isso o try/catch cai para HUMANO em vez de quebrar
        // o carregamento. Se você tem saves de teste com raça antiga
        // definida e quer migrá-los em vez de resetar, avise.
        RaceType loadedRace = RaceType.HUMAN;
        if (nbt.contains("Race")) {
            try {
                loadedRace = RaceType.valueOf(nbt.getString("Race"));
            } catch (IllegalArgumentException ignored) {
                // save antigo com valor de AffinityType -> assume HUMANO
            }
        }
        this.race = loadedRace;
        this.racialLevel = nbt.contains("RacialLevel") ? nbt.getInt("RacialLevel") : 0;
        this.racialXp = nbt.contains("RacialXp") ? nbt.getInt("RacialXp") : 0;
        this.racialMaxXp = nbt.contains("RacialMaxXp") ? nbt.getInt("RacialMaxXp") : RaceProgression.getMaxXpFor(this.racialLevel + 1);

        FactionType loadedFaction = FactionType.NONE;
        if (nbt.contains("Faction")) {
            try {
                loadedFaction = FactionType.valueOf(nbt.getString("Faction"));
            } catch (IllegalArgumentException ignored) {
                // valor desconhecido/antigo -> assume NENHUMA
            }
        }
        this.faction = loadedFaction;
        this.factionLevel = nbt.contains("FactionLevel") ? nbt.getInt("FactionLevel") : 0;
        this.factionXp = nbt.contains("FactionXp") ? nbt.getInt("FactionXp") : 0;
        this.factionMaxXp = nbt.contains("FactionMaxXp") ? nbt.getInt("FactionMaxXp") : FactionProgression.getMaxXpFor(this.factionLevel + 1);

        this.skillPoints = nbt.contains("SkillPoints") ? nbt.getInt("SkillPoints") : 0;
        this.factionSkillPoints = nbt.contains("FactionSkillPoints") ? nbt.getInt("FactionSkillPoints") : 0;
        this.unlockedSkills.clear();
        if (nbt.contains("UnlockedSkills")) {
            net.minecraft.nbt.ListNBT list = nbt.getList("UnlockedSkills", 8); // 8 = TAG_STRING
            for (int i = 0; i < list.size(); i++) {
                this.unlockedSkills.add(list.getString(i));
            }
        }
        this.skillCooldownEnd.clear();
        if (nbt.contains("SkillCooldowns")) {
            net.minecraft.nbt.CompoundNBT cooldowns = nbt.getCompound("SkillCooldowns");
            for (String skillId : cooldowns.getAllKeys()) {
                this.skillCooldownEnd.put(skillId, cooldowns.getLong(skillId));
            }
        }

        RaceType loadedTransformTarget = RaceType.HUMAN;
        if (nbt.contains("TransformationTargetRace")) {
            try {
                loadedTransformTarget = RaceType.valueOf(nbt.getString("TransformationTargetRace"));
            } catch (IllegalArgumentException ignored) {
                // valor desconhecido/antigo -> assume sem transformacao em curso
            }
        }
        this.transformationTargetRace = loadedTransformTarget;
        this.transformationStartDay = nbt.contains("TransformationStartDay") ? nbt.getLong("TransformationStartDay") : -1L;
        this.transformationLeftAbyss = nbt.getBoolean("TransformationLeftAbyss");
        this.cultistMaskBonus = nbt.getBoolean("CultistMaskBonus");

        this.activeQuests.clear();
        if (nbt.contains("ActiveQuests")) {
            net.minecraft.nbt.ListNBT list = nbt.getList("ActiveQuests", 8);
            for (int i = 0; i < list.size(); i++) {
                this.activeQuests.add(list.getString(i));
            }
        }
        this.knownRituals.clear();
        if (nbt.contains("KnownRituals")) {
            net.minecraft.nbt.ListNBT list = nbt.getList("KnownRituals", 8);
            for (int i = 0; i < list.size(); i++) {
                this.knownRituals.add(list.getString(i));
            }
        }
        this.completedQuests.clear();
        if (nbt.contains("CompletedQuests")) {
            net.minecraft.nbt.ListNBT list = nbt.getList("CompletedQuests", 8);
            for (int i = 0; i < list.size(); i++) {
                this.completedQuests.add(list.getString(i));
            }
        }
        this.questObjectiveProgress.clear();
        if (nbt.contains("QuestObjectiveProgress")) {
            net.minecraft.nbt.CompoundNBT progress = nbt.getCompound("QuestObjectiveProgress");
            for (String key : progress.getAllKeys()) {
                this.questObjectiveProgress.put(key, progress.getInt(key));
            }
        }
    }

    public void writeToNbt(CompoundNBT nbt) {
        nbt.putInt("Level", this.level);
        nbt.putInt("Xp", this.currentXp);
        nbt.putInt("MaxXp", this.maxXp);
        nbt.putInt("Points", this.pointsToDistribute);
        for (AttributeType type : AttributeType.values()) {
            nbt.putInt("Attribute_" + type.name(), getAttribute(type));
        }
        nbt.putInt("CommonEnergy", this.commonEnergy);
        nbt.putInt("Corruption", this.corruption);
        nbt.putInt("CorruptionMax", this.corruptionMax);

        for (AffinityType type : AffinityType.values()) {
            nbt.putInt("Affinity_" + type.name(), getAffinity(type));
        }
        for (ReputationFaction faction : ReputationFaction.values()) {
            nbt.putInt("Reputation_" + faction.name(), getReputation(faction));
        }
        nbt.putString("Race", this.race.name());
        nbt.putInt("RacialLevel", this.racialLevel);
        nbt.putInt("RacialXp", this.racialXp);
        nbt.putInt("RacialMaxXp", this.racialMaxXp);

        nbt.putString("Faction", this.faction.name());
        nbt.putInt("FactionLevel", this.factionLevel);
        nbt.putInt("FactionXp", this.factionXp);
        nbt.putInt("FactionMaxXp", this.factionMaxXp);

        nbt.putInt("SkillPoints", this.skillPoints);
        nbt.putInt("FactionSkillPoints", this.factionSkillPoints);
        net.minecraft.nbt.ListNBT skillList = new net.minecraft.nbt.ListNBT();
        for (String skillId : this.unlockedSkills) {
            skillList.add(net.minecraft.nbt.StringNBT.valueOf(skillId));
        }
        nbt.put("UnlockedSkills", skillList);

        net.minecraft.nbt.CompoundNBT cooldowns = new net.minecraft.nbt.CompoundNBT();
        for (java.util.Map.Entry<String, Long> entry : this.skillCooldownEnd.entrySet()) {
            cooldowns.putLong(entry.getKey(), entry.getValue());
        }
        nbt.put("SkillCooldowns", cooldowns);

        nbt.putString("TransformationTargetRace", this.transformationTargetRace.name());
        nbt.putLong("TransformationStartDay", this.transformationStartDay);
        nbt.putBoolean("TransformationLeftAbyss", this.transformationLeftAbyss);
        nbt.putBoolean("CultistMaskBonus", this.cultistMaskBonus);

        net.minecraft.nbt.ListNBT activeQuestsList = new net.minecraft.nbt.ListNBT();
        for (String questId : this.activeQuests) {
            activeQuestsList.add(net.minecraft.nbt.StringNBT.valueOf(questId));
        }
        net.minecraft.nbt.ListNBT knownRitualsList = new net.minecraft.nbt.ListNBT();
        for (String ritualId : this.knownRituals) {
            knownRitualsList.add(net.minecraft.nbt.StringNBT.valueOf(ritualId));
        }
        nbt.put("KnownRituals", knownRitualsList);
        nbt.put("ActiveQuests", activeQuestsList);

        net.minecraft.nbt.ListNBT completedQuestsList = new net.minecraft.nbt.ListNBT();
        for (String questId : this.completedQuests) {
            completedQuestsList.add(net.minecraft.nbt.StringNBT.valueOf(questId));
        }
        nbt.put("CompletedQuests", completedQuestsList);

        net.minecraft.nbt.CompoundNBT progress = new net.minecraft.nbt.CompoundNBT();
        for (java.util.Map.Entry<String, Integer> entry : this.questObjectiveProgress.entrySet()) {
            progress.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("QuestObjectiveProgress", progress);
    }

    // ---- Getters -------------------------------------------------------
    public int getLevel() { return this.level; }
    public int getCurrentXp() { return this.currentXp; }
    public int getMaxXp() { return this.maxXp; }
    public int getPointsToDistribute() { return this.pointsToDistribute; }
    public int getAttribute(AttributeType type) { return this.attributes.getOrDefault(type, 0); }
    public int getCommonEnergy() { return this.commonEnergy; }
    public int getCommonEnergyMax() { return this.commonEnergyMax; }
    public int getCorruption() { return this.corruption; }
    public int getCorruptionMax() { return this.corruptionMax; }
    public int getRacialLevel() { return this.racialLevel; }
    public int getRacialXp() { return this.racialXp; }
    public int getRacialMaxXp() { return this.racialMaxXp; }
    public int getFactionLevel() { return this.factionLevel; }
    public int getFactionXp() { return this.factionXp; }
    public int getFactionMaxXp() { return this.factionMaxXp; }
}
