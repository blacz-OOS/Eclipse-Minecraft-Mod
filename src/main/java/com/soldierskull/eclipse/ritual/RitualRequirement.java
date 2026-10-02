package com.soldierskull.eclipse.ritual;

/**
 * Uma condição que um {@link RitualContext} precisa satisfazer pro
 * altar chegar/permanecer em {@link RitualState#READY}/{@code RUNNING}.
 * Modular de propósito (Bloco C/#19) - implementações concretas
 * (TimeRequirement, MoonPhaseRequirement, DimensionRequirement,
 * RaceRequirement, FactionRequirement, AffinityRequirement,
 * EclipseRequirement, AltarTierRequirement...) ficam em
 * {@code ritual.requirement}, uma classe por arquivo, cada uma só
 * implementando {@link #test(RitualContext)}.
 *
 * {@link #describe()} existe pro overlay do altar (Bloco B/#9-10)
 * conseguir mostrar PRA QUE o requisito não bateu, sem precisar de uma
 * segunda hierarquia de "razão de falha" - decisão confirmada.
 */
public interface RitualRequirement {

    boolean test(RitualContext context);

    /**
     * Requisitos do jogador só podem ser verificados quando alguém tenta
     * iniciar o ritual. O altar ainda pode validar mundo, círculo e oferendas
     * enquanto está ocioso.
     */
    default boolean requiresInitiator() {
        return false;
    }

    /** Texto curto pro overlay, ex.: "Precisa ser noite", "Lua cheia exigida". */
    String describe();
}
