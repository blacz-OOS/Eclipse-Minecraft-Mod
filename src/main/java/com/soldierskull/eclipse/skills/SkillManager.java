package com.soldierskull.eclipse.skills;

import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Lógica de servidor para desbloquear e ativar skills. O servidor é
 * sempre a autoridade - nenhum destes métodos confia em nada vindo do
 * cliente além do id da skill que ele está tentando usar.
 *
 * Cooldown é lido/gravado em PlayerStats (persistente, epoch millis) -
 * NÃO em memória. Isso é o que garante a regra confirmada: cooldown
 * sobrevive a logout/reinício de servidor e continua contando mesmo com
 * o jogador offline, porque System.currentTimeMillis() é tempo real,
 * não tick de jogo (que fica pausado com o servidor desligado).
 */
public final class SkillManager {

    private SkillManager() {
    }

    public enum Result {
        OK,
        DONO_INCOMPATIVEL,
        NIVEL_INSUFICIENTE,
        PONTOS_INSUFICIENTES,
        PRE_REQUISITO_FALTANDO,
        JA_DESBLOQUEADA,
        SKILL_INEXISTENTE,
        NAO_E_ATIVA,
        EM_COOLDOWN,
        ENERGIA_INSUFICIENTE
    }

    public static Result unlock(ServerPlayerEntity player, PlayerStats stats, String skillId) {
        Skill skill = SkillTree.find(skillId);
        if (skill == null) {
            return Result.SKILL_INEXISTENTE;
        }
        if (stats.hasUnlockedSkill(skillId)) {
            return Result.JA_DESBLOQUEADA;
        }

        boolean ownerOk = skill.getOwnerType() == SkillOwnerType.RACE
                ? stats.getRace() == skill.getRaceOwner()
                : stats.getFaction() == skill.getFactionOwner();
        if (!ownerOk) {
            return Result.DONO_INCOMPATIVEL;
        }

        int currentLevel = skill.getOwnerType() == SkillOwnerType.RACE ? stats.getRacialLevel() : stats.getFactionLevel();
        if (currentLevel < skill.getRequiredLevel()) {
            return Result.NIVEL_INSUFICIENTE;
        }

        for (String prereq : skill.getPrerequisiteSkillIds()) {
            if (!stats.hasUnlockedSkill(prereq)) {
                return Result.PRE_REQUISITO_FALTANDO;
            }
        }

        int availablePoints = skill.getOwnerType() == SkillOwnerType.RACE ? stats.getSkillPoints() : stats.getFactionSkillPoints();
        if (availablePoints < skill.getPointCost()) {
            return Result.PONTOS_INSUFICIENTES;
        }

        if (skill.getOwnerType() == SkillOwnerType.RACE) {
            stats.removeSkillPoints(skill.getPointCost());
        } else {
            stats.removeFactionSkillPoints(skill.getPointCost());
        }
        stats.unlockSkill(skillId);
        if (skill.getEffect() != null) {
            skill.getEffect().onUnlock(player, stats);
        }
        return Result.OK;
    }

    public static Result activate(ServerPlayerEntity player, PlayerStats stats, String skillId) {
        Skill skill = SkillTree.find(skillId);
        if (skill == null) {
            return Result.SKILL_INEXISTENTE;
        }
        if (skill.getType() != SkillType.ACTIVE && skill.getType() != SkillType.TRANSFORMATION) {
            return Result.NAO_E_ATIVA;
        }
        if (!stats.hasUnlockedSkill(skillId)) {
            return Result.PRE_REQUISITO_FALTANDO;
        }

        long now = System.currentTimeMillis();
        if (now < stats.getSkillCooldownEnd(skillId)) {
            return Result.EM_COOLDOWN;
        }

        if (skill.getEnergyCost() > 0 && stats.getCommonEnergy() < skill.getEnergyCost()) {
            return Result.ENERGIA_INSUFICIENTE;
        }

        if (skill.getEnergyCost() > 0) {
            stats.removeCommonEnergy(skill.getEnergyCost());
        }
        if (skill.getCooldownMillis() > 0) {
            // Timestamp absoluto de quando a skill volta a ficar pronta -
            // NUNCA "tempo restante" (ver comentário em PlayerStats.skillCooldownEnd).
            stats.setSkillCooldownEnd(skillId, now + skill.getCooldownMillis());
        }
        if (skill.getEffect() != null) {
            skill.getEffect().onActivate(player, stats);
        }
        return Result.OK;
    }
}
