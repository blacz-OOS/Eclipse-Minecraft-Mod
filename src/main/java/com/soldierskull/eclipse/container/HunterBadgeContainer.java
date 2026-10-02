package com.soldierskull.eclipse.container;

import com.soldierskull.eclipse.inventory.HunterBadgeItemHandler;
import com.soldierskull.eclipse.item.ModItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * GUI da Hunter Badge - 7 slots (restritos a item de caçador via
 * {@link HunterBadgeItemHandler#isItemValid}) + inventário do jogador
 * embaixo, igual qualquer container de backpack.
 */
public class HunterBadgeContainer extends Container {

    public static final int BADGE_SLOTS = HunterBadgeItemHandler.SLOTS;

    private final IItemHandler badgeInventory;

    /** Construtor usado no lado do cliente, reconstruído a partir do pacote de abertura (qual mão segurava a badge). */
    public HunterBadgeContainer(int windowId, PlayerInventory playerInventory, Hand hand) {
        this(windowId, playerInventory, resolveHandler(playerInventory.player, hand));
    }

    public HunterBadgeContainer(int windowId, PlayerInventory playerInventory, IItemHandler badgeInventory) {
        super(ModContainers.HUNTER_BADGE.get(), windowId);
        this.badgeInventory = badgeInventory;

        for (int i = 0; i < BADGE_SLOTS; i++) {
            this.addSlot(new SlotItemHandler(badgeInventory, i, 8 + i * 18, 20));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 109));
        }
    }

    private static IItemHandler resolveHandler(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElseGet(() -> new HunterBadgeItemHandler());
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.getMainHandItem().getItem() == ModItems.HUNTER_BADGE.get()
                || player.getOffhandItem().getItem() == ModItems.HUNTER_BADGE.get();
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            if (index < BADGE_SLOTS) {
                if (!this.moveItemStackTo(stackInSlot, BADGE_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, BADGE_SLOTS, false)) {
                    return ItemStack.EMPTY; // não é item de caçador (isItemValid recusa) ou não coube
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }
}
