package com.soldierskull.eclipse.stats;

/**
 * Fonte unica de verdade para todos os valores de balanceamento do mod.
 * Nenhuma outra classe deve declarar sua propria copia dessas constantes -
 * PlayerStats, StatAttributeHandler, itens, comandos, etc. sempre leem
 * daqui. Quando quiser rebalancear o Eclipse, altere apenas esta classe.
 */
public final class StatBalance {

    private StatBalance() {}

    // ===== Vida (Constitution) =====
    public static final float HEALTH_PER_CONSTITUTION = 1.0F;       // design choice: +1 max HP por ponto
    public static final float HP_REGEN_PER_CONSTITUTION = 0.2F;     // spec: +0.2 HP regen/ponto/seg

    // ===== Combate (Strength / Defense / Speed) =====
    public static final float ATTACK_DAMAGE_PER_STRENGTH = 0.5F;    // design choice: +0.5 dano de ataque/ponto
    public static final float ARMOR_PER_DEFENSE = 1.0F;             // design choice: +1 armadura/ponto (atributo vanilla ARMOR)
    public static final float MOVE_SPEED_PER_SPEED = 0.0015F;       // design choice: base e 0.1, entao 10 pts = +0.015

    // ===== Common Energy (Energy) =====
    public static final int COMMON_ENERGY_BASE_MAX = 100;
    public static final int COMMON_ENERGY_MAX_PER_ENERGY = 2;       // design choice: +2 Common Energy max/ponto
    public static final float COMMON_ENERGY_REGEN_PER_ENERGY = 0.3F; // spec: +0.3 regen/ponto/seg (bonus por ponto no atributo Energy)
    public static final float COMMON_ENERGY_PASSIVE_REGEN_BASE = 1.0F; // regen base/seg, independente do atributo Energy

    // ===== Corruption =====
    public static final int CORRUPTION_BASE_MAX = 50; // "quantidade total de corrupcao no nascimento" - confirmado, era 100
    public static final int ABYSS_SHARD_RESTORE_AMOUNT = 1;         // quanto um Abyss Shard restaura

    // ===== Conversao Abyssal -> Common =====
    // O botao "Converter" agora aumenta permanentemente o MAXIMO de Common
    // Energy, em vez de encher a energia atual. Custa CORRUPTION_TO_COMMON_COST
    // de Corruption (Corruption) e concede CORRUPTION_TO_COMMON_MAX_GAIN
    // pontos de Common Energy Max.
    public static final int CORRUPTION_TO_COMMON_COST = 30;
    public static final int CORRUPTION_TO_COMMON_MAX_GAIN = 1;
}
