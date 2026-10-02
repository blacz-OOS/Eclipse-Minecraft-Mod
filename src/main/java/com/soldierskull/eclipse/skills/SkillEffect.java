package com.soldierskull.eclipse.skills;

import net.minecraft.entity.player.ServerPlayerEntity;
import com.soldierskull.eclipse.stats.PlayerStats;

/**
 * O efeito de verdade de uma {@link Skill}. Separado da definição do nó
 * (id/custo/pré-requisitos) de propósito - veja o comentário em cima de
 * Skill.java para o motivo.
 *
 * onUnlock()   - chamado uma vez, quando a skill é desbloqueada. Para
 *                skills PASSIVE, normalmente é aqui que o efeito
 *                permanente é ligado (ex.: registrar o jogador para
 *                receber um MobEffect enquanto a skill estiver
 *                desbloqueada - a aplicação contínua ainda precisaria de
 *                um listener de tick separado, fora de escopo aqui).
 * onActivate() - chamado quando uma skill ACTIVE é acionada pelo
 *                jogador (via item, tecla, comando), depois que
 *                SkillManager já validou cooldown e custo de energia.
 *
 * Implementações concretas (ex.: "Vampiro: Sentidos Aguçados") ainda
 * precisam ser escritas quando você definir o que cada habilidade faz.
 */
public interface SkillEffect {

    default void onUnlock(ServerPlayerEntity player, PlayerStats stats) {
        // sem efeito por padrao - skills sem logica de unlock (ex.: so
        // servem de pre-requisito para outras) podem deixar isso vazio.
    }

    default void onActivate(ServerPlayerEntity player, PlayerStats stats) {
        // sem efeito por padrao - so relevante para skills ACTIVE.
    }
}
