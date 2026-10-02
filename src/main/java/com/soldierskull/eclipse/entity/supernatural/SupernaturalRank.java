package com.soldierskull.eclipse.entity.supernatural;

/**
 * Classificacao de perigo (item 5 e 7 da Fase 14). NAO e uma escala
 * linear de HP - e categoria de design. BOSS existe na arquitetura
 * mesmo sem nenhum boss implementado nesta fase (item 56/64).
 */
public enum SupernaturalRank {
    TRIVIAL,
    COMMON,
    DANGEROUS,
    ELITE,
    MINIBOSS,
    BOSS
}
