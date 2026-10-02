package com.soldierskull.eclipse.item;

import javax.annotation.Nullable;

import com.soldierskull.eclipse.container.HunterBadgeContainer;
import com.soldierskull.eclipse.inventory.HunterBadgeItemHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Hunter Badge - "basicamente uma mochila", mas só com 7 slots e só
 * aceita item de caçador (ver {@link HunterItemFilter}). Antes era um
 * item sem nenhuma função; agora abre um container real ao usar,
 * mantendo o conteúdo salvo no próprio item (NBT), igual qualquer
 * backpack - sobrevive a troca de mão, morte com keepInventory, etc.
 */
public class HunterBadgeItem extends Item {

    public HunterBadgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide && player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new StringTextComponent("Hunter Badge");
                }

                @Override
                public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity p) {
                    return new HunterBadgeContainer(windowId, inv, hand);
                }
            }, buf -> buf.writeEnum(hand));
        }
        return ActionResult.sidedSuccess(stack, world.isClientSide);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new BadgeCapabilityProvider(nbt);
    }

    private static class BadgeCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

        private final HunterBadgeItemHandler handler = new HunterBadgeItemHandler();
        private final LazyOptional<IItemHandler> handlerOptional = LazyOptional.of(() -> this.handler);

        BadgeCapabilityProvider(@Nullable CompoundNBT nbt) {
            if (nbt != null) {
                this.handler.deserializeNBT(nbt);
            }
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, this.handlerOptional);
        }

        @Override
        public CompoundNBT serializeNBT() {
            return this.handler.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            this.handler.deserializeNBT(nbt);
        }
    }
}
