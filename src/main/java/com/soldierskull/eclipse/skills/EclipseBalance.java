package com.soldierskull.eclipse.skills;

/**
 * Fonte única de verdade pra qualquer duração/número relacionado ao
 * evento Eclipse (decisão da Fase 15: 15 minutos reais, centralizados,
 * sem número solto espalhado pelo código). Antes desta classe, a
 * duração aparecia hardcoded como placeholder em pelo menos 2 lugares
 * diferentes (CultistSkillEffects: 5min, RitualRegistry: 10min) -
 * nenhum deles 15min, e mudar um não mudava o outro. Agora é uma
 * constante só, em ticks (não milissegundos - EclipseEventManager.start
 * usa ticks diretamente, sem conversão de unidade no meio do caminho).
 */
public final class EclipseBalance {

    /** Duração padrão de QUALQUER Eclipse disparado manualmente (skill ou ritual). 15 min * 60s * 20 ticks/s. */
    public static final long DEFAULT_DURATION_TICKS = 15L * 60L * 20L;

    /** Duração do Eclipse que nasce sozinho (chance natural por noite). */
    public static final long NATURAL_DURATION_TICKS = 15L * 60L * 20L;

    /** 1 em X chance por noite, por dimensão, de nascer um Eclipse natural. */
    public static final int NATURAL_ECLIPSE_CHANCE = 28;

    private EclipseBalance() {
    }
}
