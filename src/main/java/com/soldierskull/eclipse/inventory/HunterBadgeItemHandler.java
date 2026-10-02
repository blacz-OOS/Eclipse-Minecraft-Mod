package com.soldierskull.eclipse.inventory;

import com.soldierskull.eclipse.item.HunterItemFilter;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/** Inventário de 7 slots da Hunter Badge - só aceita "item de caçador" (ver {@link HunterItemFilter}). */
public class HunterBadgeItemHandler extends ItemStackHandler {

    public static final int SLOTS = 7;

    public HunterBadgeItemHandler() {
        super(SLOTS);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return HunterItemFilter.isHunterItem(stack);
    }
}
