package com.soldierskull.eclipse.ritual;

/**
 * Tamanho do círculo ritualístico exigido por um {@link Ritual}. O
 * número é o lado do quadrado (ímpar, o altar/núcleo fica no centro).
 * Decisão confirmada: os três tamanhos existem desde o início, não só o
 * 7x7.
 */
public enum RitualCircleSize {

    SMALL(7),
    MEDIUM(11),
    LARGE(15);

    private final int size;

    RitualCircleSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    /** Distância do centro até a borda (raio em blocos). */
    public int getRadius() {
        return this.size / 2;
    }
}
