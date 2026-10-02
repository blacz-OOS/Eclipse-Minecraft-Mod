package com.soldierskull.eclipse.item;

import java.util.HashSet;
import java.util.Set;

import com.soldierskull.eclipse.block.ModBlocks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Define o que conta como "item de caçador" pra Hunter Badge (só esses
 * podem ser guardados nela). Lista baseada no que já existe no mod -
 * tudo ligado a prata, água benta e equipamento de caçador.
 */
public final class HunterItemFilter {

    private static Set<Item> hunterItems;

    private HunterItemFilter() {
    }

    public static boolean isHunterItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return items().contains(stack.getItem());
    }

    private static Set<Item> items() {
        if (hunterItems == null) {
            hunterItems = new HashSet<>();
            hunterItems.add(ModItems.RAW_SILVER.get());
            hunterItems.add(ModItems.SILVER.get());
            hunterItems.add(ModItems.PURE_SILVER.get());
            hunterItems.add(ModItems.SILVER_SWORD.get());
            hunterItems.add(ModItems.SILVER_DAGGER.get());
            hunterItems.add(ModItems.HOLY_WATER.get());
            hunterItems.add(ModItems.HUNTER_SPECIAL_BOW.get());
            hunterItems.add(ModItems.HUNTER_TRAP.get());
            // HUNTER_JOURNAL foi removido (unificado no ECLIPSE_GRIMOIRE) - o
            // Badge agora aceita o grimório único em vez do diário exclusivo.
            hunterItems.add(ModItems.ECLIPSE_GRIMOIRE.get());
            hunterItems.add(ModBlocks.SILVER_ORE_ITEM.get());
        }
        return hunterItems;
    }
}
