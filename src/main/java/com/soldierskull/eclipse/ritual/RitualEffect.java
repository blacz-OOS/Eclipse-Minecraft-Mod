package com.soldierskull.eclipse.ritual;

/**
 * Um efeito aplicado quando o ritual chega em
 * {@link RitualState#COMPLETED} (Bloco C/#24). Implementações concretas
 * ficam em {@code ritual.effect} - ex.: {@code GiveItemEffect},
 * {@code StartTransformationEffect} (chama o
 * {@code TransformationManager} já existente), {@code TriggerEclipseEffect}
 * (chama o {@code EclipseEventManager} já existente),
 * {@code AddAffinityEffect}, {@code AddRitualPowerEffect}. Cada
 * {@link Ritual} carrega uma lista de efeitos - podem ser combinados
 * livremente, sem precisar de um resultado "único".
 */
public interface RitualEffect {

    void apply(RitualContext context);
}
