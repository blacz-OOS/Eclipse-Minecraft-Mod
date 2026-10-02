package com.soldierskull.eclipse.race;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Toda raça que um jogador pode ter no Eclipse. Isso é DIFERENTE de
 * {@link com.soldierskull.eclipse.stats.AffinityType} (afinidade 0-100%)
 * e de {@link com.soldierskull.eclipse.stats.ReputationFaction} (reputação
 * de facção 0-100%) - os três sistemas são independentes, por decisão de
 * design confirmada.
 *
 * Todo jogador começa HUMANO. A raça só muda via transformação (Fase 9 -
 * ainda não implementada; depende de 50% de Afinidade + entrar no Abismo).
 *
 * HOW TO ADICIONAR UMA NOVA RAÇA (ex.: uma raça "Fada"):
 *   1. Adicione uma constante aqui, ex.: FADA("Fada"),
 *   2. Adicione a tabela de ranks dela em
 *   Persistência, sync (PacketSyncStats/ClientStatsCache) e o rank ficam
 *   automáticos assim que passos 1 e 2 forem feitos.
 */
public enum RaceType {

    HUMAN,
    VAMPIRE,
    WEREWOLF;

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("race.eclipse." + this.name().toLowerCase());
    }
}