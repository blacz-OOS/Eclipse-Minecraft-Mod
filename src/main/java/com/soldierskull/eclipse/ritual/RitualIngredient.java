package com.soldierskull.eclipse.ritual;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

/**
 * Um ingrediente exigido por um {@link Ritual} - tanto o "ingrediente
 * principal" (clicado no altar) quanto cada "offering" (jogado dentro
 * do círculo). Usa {@link Ingredient} do próprio Minecraft/Forge, não
 * uma lista fixa de itens - decisão confirmada (Bloco C/#18), permite
 * tanto item específico (ex.: Abyss Crystal x1) quanto, futuramente,
 * qualquer item de uma tag (ex.: qualquer Blood Essence).
 */
public class RitualIngredient {

    private final Ingredient ingredient;
    private final int count;

    public RitualIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public static RitualIngredient of(ItemStack stack) {
        return new RitualIngredient(Ingredient.of(stack.getItem()), stack.getCount());
    }

    public static RitualIngredient of(Ingredient ingredient, int count) {
        return new RitualIngredient(ingredient, count);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public int getCount() {
        return this.count;
    }

    /** Testa se o stack dado satisfaz este ingrediente em quantidade suficiente. */
    public boolean matches(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.count;
    }
}
