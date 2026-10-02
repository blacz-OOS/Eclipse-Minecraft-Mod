package com.soldierskull.eclipse.item;

import java.util.function.Supplier;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

/**
 * Tier de material "Prata" pras armas dos Caçadores (Espada de Prata,
 * Adaga de Prata - Fase 10). Sem valores definidos no documento de
 * design pra durabilidade/dano/encantabilidade - usei algo entre Ferro e
 * Diamante como PLACEHOLDER de balanceamento, já que prata narrativamente
 * é um material "especial" mas não necessariamente o mais forte do jogo.
 * Ajuste os números se quiser algo diferente.
 */
public enum EclipseItemTiers implements IItemTier {

    SILVER(2, 300, 7.0F, 2.5F, 14, () -> Ingredient.of(com.soldierskull.eclipse.item.ModItems.PURE_SILVER.get()));

    private final int level;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    EclipseItemTiers(int level, int uses, float speed, float attackDamageBonus, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.attackDamageBonus;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
