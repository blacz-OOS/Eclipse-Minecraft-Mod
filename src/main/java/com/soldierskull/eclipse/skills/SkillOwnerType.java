package com.soldierskull.eclipse.skills;

/**
 * De qual sistema uma {@link Skill} depende: nível/pontos RACIAIS
 * (raça) ou nível/pontos de FACÇÃO. Existem porque, pela especificação
 * consolidada, os dois são pools completamente separados - uma skill de
 * facção nunca consome pontos raciais e vice-versa, e a checagem de
 * nível usa getRacialLevel() ou getFactionLevel() dependendo do caso.
 */
public enum SkillOwnerType {
    RACE,
    FACTION
}
