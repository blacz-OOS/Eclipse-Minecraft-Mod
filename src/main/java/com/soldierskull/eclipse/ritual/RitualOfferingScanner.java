package com.soldierskull.eclipse.ritual;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

/**
 * Procura, dentro da área do círculo, os itens que o jogador jogou (Q)
 * como "offerings" de um ritual (Bloco C/#18, decisão confirmada -
 * ingrediente principal é clicado no altar, offerings são físicos no
 * chão). Só olha {@link ItemEntity} dentro do raio - nenhum inventário
 * é usado.
 */
public final class RitualOfferingScanner {

    private RitualOfferingScanner() {
    }

    /** Confere se cada offering da lista tem uma entidade correspondente disponível (sem consumir nada ainda). */
    public static boolean allOfferingsPresent(ServerWorld world, BlockPos center, int radius, List<RitualIngredient> offerings) {
        List<ItemEntity> pool = findItemEntities(world, center, radius);
        for (RitualIngredient offering : offerings) {
            if (findMatch(pool, offering) == null) {
                return false;
            }
        }
        return true;
    }

    /** Remove (ou reduz) as entidades que satisfazem cada offering. Só chamar depois de confirmar allOfferingsPresent. */
    public static void consumeOfferings(ServerWorld world, BlockPos center, int radius, List<RitualIngredient> offerings) {
        List<ItemEntity> pool = findItemEntities(world, center, radius);
        for (RitualIngredient offering : offerings) {
            ItemEntity match = findMatch(pool, offering);
            if (match == null) {
                continue; // já validado antes por allOfferingsPresent; defensivo
            }
            ItemStack stack = match.getItem();
            stack.shrink(offering.getCount());
            if (stack.isEmpty()) {
                match.remove();
            }
            pool.remove(match);
        }
    }

    private static ItemEntity findMatch(List<ItemEntity> pool, RitualIngredient offering) {
        for (ItemEntity entity : pool) {
            if (offering.matches(entity.getItem())) {
                return entity;
            }
        }
        return null;
    }

    private static List<ItemEntity> findItemEntities(ServerWorld world, BlockPos center, int radius) {
        AxisAlignedBB area = new AxisAlignedBB(center).inflate(radius, 2, radius);
        return new ArrayList<>(world.getEntitiesOfClass(ItemEntity.class, area));
    }
}
