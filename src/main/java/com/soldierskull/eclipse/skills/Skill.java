package com.soldierskull.eclipse.skills;

import java.util.Collections;
import java.util.List;

import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.stats.PlayerStats;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Um nó da árvore de habilidades - de RAÇA ou de FACÇÃO.
 *
 * Os campos de nome e descrição armazenam CHAVES DE TRADUÇÃO,
 * nunca o texto diretamente.
 *
 * Exemplo:
 *   displayName = "skill.eclipse.vampiric_sense.name"
 *   description = "skill.eclipse.vampiric_sense.description"
 *
 * Dessa forma, o texto exibido será resolvido de acordo com
 * o idioma escolhido pelo jogador.
 */
public class Skill {

    private final String id;

    /** Chave de tradução do nome da habilidade. */
    private final String displayName;

    /** Chave de tradução da descrição da habilidade. */
    private final String description;

    private final SkillOwnerType ownerType;
    private final RaceType raceOwner;
    private final FactionType factionOwner;
    private final SkillType type;
    private final int requiredLevel;
    private final int pointCost;
    private final List<String> prerequisiteSkillIds;

    /**
     * Cooldown em MILISSEGUNDOS de tempo real.
     * 0 = sem cooldown.
     */
    private final long cooldownMillis;

    /**
     * Custo em Common Energy para ativar.
     * 0 = sem custo / ainda não definido.
     */
    private final int energyCost;

    private final SkillEffect effect;

    /**
     * Nome do arquivo da textura da skill, sem .png.
     * Se vazio/null, usa o próprio id.
     */
    private final String textureName;

    private Skill(
            String id,
            String displayName,
            String description,
            SkillOwnerType ownerType,
            RaceType raceOwner,
            FactionType factionOwner,
            SkillType type,
            int requiredLevel,
            int pointCost,
            List<String> prerequisiteSkillIds,
            long cooldownMillis,
            int energyCost,
            SkillEffect effect,
            String textureName) {

        this.id = id;

        /*
         * Agora estes campos são CHAVES DE TRADUÇÃO.
         * O texto real fica nos arquivos lang.
         */
        this.displayName = displayName;
        this.description = description;

        this.ownerType = ownerType;
        this.raceOwner = raceOwner;
        this.factionOwner = factionOwner;
        this.type = type;
        this.requiredLevel = requiredLevel;
        this.pointCost = pointCost;

        this.prerequisiteSkillIds = prerequisiteSkillIds == null
                ? Collections.emptyList()
                : prerequisiteSkillIds;

        this.cooldownMillis = cooldownMillis;
        this.energyCost = energyCost;
        this.effect = effect;

        this.textureName = textureName == null || textureName.trim().isEmpty()
                ? id
                : textureName;
    }

    /**
     * Mantém compatibilidade com as skills existentes.
     *
     * displayName e description devem ser CHAVES DE TRADUÇÃO.
     */
    public static Skill ofRace(
            String id,
            String displayName,
            String description,
            RaceType race,
            SkillType type,
            int requiredRacialLevel,
            int pointCost,
            List<String> prerequisiteSkillIds,
            long cooldownMillis,
            int energyCost,
            SkillEffect effect) {

        return ofRace(
                id,
                displayName,
                description,
                race,
                type,
                requiredRacialLevel,
                pointCost,
                prerequisiteSkillIds,
                cooldownMillis,
                energyCost,
                effect,
                null
        );
    }

    /**
     * Versão com nome de textura personalizado.
     *
     * displayName e description devem ser CHAVES DE TRADUÇÃO.
     */
    public static Skill ofRace(
            String id,
            String displayName,
            String description,
            RaceType race,
            SkillType type,
            int requiredRacialLevel,
            int pointCost,
            List<String> prerequisiteSkillIds,
            long cooldownMillis,
            int energyCost,
            SkillEffect effect,
            String textureName) {

        return new Skill(
                id,
                displayName,
                description,
                SkillOwnerType.RACE,
                race,
                null,
                type,
                requiredRacialLevel,
                pointCost,
                prerequisiteSkillIds,
                cooldownMillis,
                energyCost,
                effect,
                textureName
        );
    }

    public static Skill ofFaction(
            String id,
            String displayName,
            String description,
            FactionType faction,
            SkillType type,
            int requiredFactionLevel,
            int pointCost,
            List<String> prerequisiteSkillIds,
            long cooldownMillis,
            int energyCost,
            SkillEffect effect) {

        return ofFaction(
                id,
                displayName,
                description,
                faction,
                type,
                requiredFactionLevel,
                pointCost,
                prerequisiteSkillIds,
                cooldownMillis,
                energyCost,
                effect,
                null
        );
    }

    /**
     * Versão com nome de textura personalizado.
     *
     * displayName e description devem ser CHAVES DE TRADUÇÃO.
     */
    public static Skill ofFaction(
            String id,
            String displayName,
            String description,
            FactionType faction,
            SkillType type,
            int requiredFactionLevel,
            int pointCost,
            List<String> prerequisiteSkillIds,
            long cooldownMillis,
            int energyCost,
            SkillEffect effect,
            String textureName) {

        return new Skill(
                id,
                displayName,
                description,
                SkillOwnerType.FACTION,
                null,
                faction,
                type,
                requiredFactionLevel,
                pointCost,
                prerequisiteSkillIds,
                cooldownMillis,
                energyCost,
                effect,
                textureName
        );
    }

    /**
     * Verifica se a habilidade pode ser desbloqueada.
     */
    public boolean canUnlock(PlayerStats stats) {

        if (this.ownerType == SkillOwnerType.RACE) {

            if (stats.getRace() != this.raceOwner) {
                return false;
            }

            if (stats.getRacialLevel() < this.requiredLevel) {
                return false;
            }

            if (stats.getSkillPoints() < this.pointCost) {
                return false;
            }

        } else {

            if (stats.getFaction() != this.factionOwner) {
                return false;
            }

            if (stats.getFactionLevel() < this.requiredLevel) {
                return false;
            }

            if (stats.getFactionSkillPoints() < this.pointCost) {
                return false;
            }
        }

        for (String prereq : this.prerequisiteSkillIds) {

            if (!stats.hasUnlockedSkill(prereq)) {
                return false;
            }
        }

        return true;
    }

    public String getId() {
        return this.id;
    }

    /**
     * Retorna a CHAVE de tradução do nome.
     *
     * Não use este método diretamente para desenhar o texto na tela.
     * Para exibição, use getDisplayNameComponent().
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Retorna o nome já como componente traduzível.
     *
     * O Minecraft resolve a tradução de acordo com o idioma atual.
     */
    public ITextComponent getDisplayNameComponent() {
        return new TranslationTextComponent(this.displayName);
    }

    /**
     * Retorna a CHAVE de tradução da descrição.
     *
     * Para exibição, prefira getDescriptionComponent().
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Retorna a descrição já como componente traduzível.
     */
    public ITextComponent getDescriptionComponent() {
        return new TranslationTextComponent(this.description);
    }

    public SkillOwnerType getOwnerType() {
        return this.ownerType;
    }

    public RaceType getRaceOwner() {
        return this.raceOwner;
    }

    public FactionType getFactionOwner() {
        return this.factionOwner;
    }

    public SkillType getType() {
        return this.type;
    }

    public int getRequiredLevel() {
        return this.requiredLevel;
    }

    public int getPointCost() {
        return this.pointCost;
    }

    public List<String> getPrerequisiteSkillIds() {
        return this.prerequisiteSkillIds;
    }

    public long getCooldownMillis() {
        return this.cooldownMillis;
    }

    public int getEnergyCost() {
        return this.energyCost;
    }

    public SkillEffect getEffect() {
        return this.effect;
    }

    public String getTextureName() {
        return this.textureName;
    }
}