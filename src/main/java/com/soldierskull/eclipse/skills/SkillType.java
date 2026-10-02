package com.soldierskull.eclipse.skills;

/**
 * Categoria de uma {@link Skill}, conforme a especificação consolidada
 * (substitui a versão anterior que tinha PASSIVE/ACTIVE/CONDITIONAL -
 * CONDITIONAL foi removido, TRANSFORMATION foi adicionado).
 *
 * PASSIVE        - fica permanentemente ativa após desbloqueada.
 * ACTIVE         - acionada pelo jogador; pode ter custo de energia,
 *                  cooldown, duração, alcance, condições de uso.
 * TRANSFORMATION - altera temporariamente a forma/estado do jogador
 *                  (ex.: Forma de Névoa, Forma Alfa, Ascensão Vampírica).
 *                  Tem suas próprias regras de duração/cooldown/efeitos.
 */
public enum SkillType {
    PASSIVE,
    ACTIVE,
    TRANSFORMATION
}
