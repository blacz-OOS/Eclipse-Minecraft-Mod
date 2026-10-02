package com.soldierskull.eclipse.entity.supernatural;

/**
 * Marca qualquer entidade da Fase 14 (e futuras) como pertencente a
 * categoria SUPERNATURAL, sem precisar de listas manuais em outros
 * sistemas (item 9 e 55: missoes Hunter consultam esta categoria
 * ao inves de listar entidade por entidade).
 */
public interface ISupernaturalMob {
    SupernaturalOrigin getSupernaturalOrigin();
    SupernaturalRank getSupernaturalRank();
}
