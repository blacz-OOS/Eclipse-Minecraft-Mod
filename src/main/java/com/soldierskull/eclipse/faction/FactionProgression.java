package com.soldierskull.eclipse.faction;

import java.util.EnumMap;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.soldierskull.eclipse.race.RaceProgression;

/**
 * Lógica PURA de progressão de facção - mesmo papel que
 * {@link RaceProgression} tem para raça, mesmo padrão de implementação.
 * Quem guarda o nível/XP de facção de cada jogador é PlayerStats.
 */
public final class FactionProgression {

    /** Nível de facção máximo (regra 7 do doc: 1-12, igual à raça). */
    public static final int MAX_FACTION_LEVEL = 12;

    private static final EnumMap<FactionType, NavigableMap<Integer, String>> RANKS = new EnumMap<>(FactionType.class);

    static {
        NavigableMap<Integer, String> hunters = new TreeMap<>();
        hunters.put(1, "Recruta");
        hunters.put(3, "Soldado");
        hunters.put(5, "Exorcista");
        hunters.put(7, "Mestre");
        hunters.put(12, "Grão-Mestre");
        RANKS.put(FactionType.HUNTERS, hunters);

        NavigableMap<Integer, String> cultists = new TreeMap<>();
        cultists.put(1, "Iniciado");
        cultists.put(3, "Cultista");
        cultists.put(5, "Bispo");
        cultists.put(7, "Arcebispo");
        cultists.put(12, "Líder");
        RANKS.put(FactionType.CULTISTS, cultists);

        // NENHUMA intencionalmente sem tabela: getRank() retorna null.
    }

    private FactionProgression() {
    }

    public static String getRank(FactionType faction, int factionLevel) {
        NavigableMap<Integer, String> table = RANKS.get(faction);
        if (table == null) {
            return null;
        }
        int clamped = Math.max(1, Math.min(MAX_FACTION_LEVEL, factionLevel));
        return table.floorEntry(clamped).getValue();
    }

    /**
     * PLACEHOLDER, mesmo status do equivalente racial: curva de XP de
     * facção ainda não definida pelo design (a fonte de XP de facção
     * também está em aberto - ver pergunta feita ao usuário). Reaproveita
     * a mesma curva exponencial da raça só para manter os dois sistemas
     * consistentes até haver números reais.
     */
    public static int getMaxXpFor(int factionLevel) {
        return RaceProgression.getMaxXpFor(factionLevel);
    }
}
