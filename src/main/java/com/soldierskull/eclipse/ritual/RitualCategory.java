package com.soldierskull.eclipse.ritual;

/**
 * Escola/categoria de um {@link Ritual}. Cada altar só executa rituais
 * cuja categoria esteja na sua lista permitida (ver
 * {@code AltarType.getAllowedCategories()}, Fase seguinte) - decisão
 * confirmada na especificação (Bloco A/E):
 *
 * <pre>
 * Altar       -> GENERAL, HUNTER
 * Blood Altar -> GENERAL, BLOOD, VAMPIRE
 * Moon Altar  -> GENERAL, LUNAR, WEREWOLF
 * Abyss Altar -> GENERAL, ABYSSAL, ECLIPSE, ENDGAME, CULTIST
 * </pre>
 *
 * GENERAL é a única categoria universal (disponível em todo altar,
 * inclusive o ritual de bootstrap {@code channel_power}).
 */
public enum RitualCategory {

    GENERAL,
    BLOOD,
    LUNAR,
    ABYSSAL,
    VAMPIRE,
    WEREWOLF,
    HUNTER,
    CULTIST,
    ECLIPSE,
    ENDGAME
}
