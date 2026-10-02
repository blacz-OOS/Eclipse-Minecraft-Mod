package com.soldierskull.eclipse.race;

import java.util.EnumMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Lógica PURA de progressão racial (nenhum dado de jogador vive aqui -
 * quem guarda o nível/XP racial de cada jogador é PlayerStats).
 *
 * Duas responsabilidades:
 *   1. Dado (raça, nível racial) -> nome do rank exibido.
 *   2. Dado o nível racial atual -> quanto de XP falta para o próximo nível.
 */
public final class RaceProgression {

    /** Nível racial máximo, igual para todas as raças. */
    public static final int MAX_RACIAL_LEVEL = 12;

    /**
     * Tabela geral:
     * RaceType -> (nível mínimo -> nome do rank)
     */
    private static final EnumMap<RaceType, NavigableMap<Integer, String>> RANKS =
            new EnumMap<>(RaceType.class);

    static {
        // ================= VAMPIRO =================
        NavigableMap<Integer, String> vampire = new TreeMap<>();
        vampire.put(1, "Newborn");
        vampire.put(3, "Vampire");
        vampire.put(5, "Lord");
        vampire.put(7, "Duke");
        vampire.put(12, "Vampire king");

        RANKS.put(RaceType.VAMPIRE, vampire);

        // ================= LOBISOMEM =================
        NavigableMap<Integer, String> werewolf = new TreeMap<>();
        werewolf.put(1, "Cub");
        werewolf.put(3, "Werewolf");
        werewolf.put(5, "Glabro");
        werewolf.put(7, "Hispo");
        werewolf.put(12, "Alpha");

        RANKS.put(RaceType.WEREWOLF, werewolf);

        // HUMANO intencionalmente sem tabela.
        // getRank() retorna null para HUMAN.
    }

    private RaceProgression() {
    }

    /**
     * Retorna o nome do rank correspondente ao nível racial.
     *
     * Exemplo:
     * nível 8 de Vampiro -> "Duque"
     * nível 11 de Lobisomem -> "Hispo"
     *
     * Retorna null para raças sem tabela de rank, como HUMANO.
     */
    public static String getRank(RaceType race, int racialLevel) {
        NavigableMap<Integer, String> table = RANKS.get(race);

        if (table == null) {
            return null;
        }

        int clamped = Math.max(1, Math.min(MAX_RACIAL_LEVEL, racialLevel));

        Map.Entry<Integer, String> entry = table.floorEntry(clamped);

        return entry != null ? entry.getValue() : null;
    }

    /**
     * PLACEHOLDER:
     * quanto de XP racial é necessário para completar
     * o nível racial dado.
     */
    public static int getMaxXpFor(int racialLevel) {
        int level = Math.max(1, racialLevel);
        return (int) (50 * Math.pow(1.4D, level - 1));
    }

    /**
     * PLACEHOLDER:
     * multiplicador de poder ligado ao nível racial.
     */
    public static double getRankPowerMultiplier(int racialLevel) {
        int level = Math.max(0, racialLevel);
        return Math.pow(1.15D, level);
    }
}