package com.soldierskull.eclipse.quests;

/**
 * Tipos de objetivo que o motor de quests (Fase 11) já suporta,
 * conforme confirmado - matar mobs e coletar/entregar itens. Chegar em
 * coordenada e usar item específico ficaram de fora por enquanto (não
 * foram selecionados); a estrutura (QuestObjective/QuestManager) é
 * genérica o bastante pra receber novos tipos depois sem reescrever
 * nada, mas só esses dois têm lógica de progresso implementada.
 */
public enum QuestObjectiveType {
    /** Progresso incrementado por evento (LivingDeathEvent) - ver QuestEvents. */
    KILL_MOB,
    /** Progresso checado ao vivo contra o inventário do jogador na hora de entregar a quest - ver QuestManager. */
    COLLECT_ITEM
}
