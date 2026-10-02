package com.soldierskull.eclipse.item;

/**
 * Marca uma arma como "de prata" pra fins de gameplay (Treinamento com
 * Prata, Fase 8) - independe de qual subclasse concreta de Item ela seja
 * (espada, adaga, futura lança, etc.). CombatEvents faz `item instanceof
 * SilverWeapon` pra checar isso, em vez de comparar classes específicas.
 */
public interface SilverWeapon {
}
