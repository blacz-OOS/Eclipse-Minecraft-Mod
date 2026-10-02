package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.ITag;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "eclipse")
public class GhostSilverWeaknessEvent {

    private static final ITag.INamedTag<net.minecraft.item.Item> SILVER_WEAPONS = ItemTags.createOptional(new ResourceLocation("eclipse", "silver_weapons"));

    @SubscribeEvent
    public static void onGhostHurt(LivingHurtEvent event) {
        if (event.getEntityLiving() instanceof GhostEntity) {
            if (event.getSource().getEntity() instanceof net.minecraft.entity.LivingEntity) {
                net.minecraft.entity.LivingEntity attacker = (net.minecraft.entity.LivingEntity) event.getSource().getEntity();
                ItemStack heldItem = attacker.getMainHandItem();

                if (!heldItem.isEmpty() && (heldItem.getItem().is(SILVER_WEAPONS) || heldItem.getItem().getRegistryName().getPath().contains("silver"))) {
                    event.setAmount(event.getAmount() + 1.0F);
                }
            }
        }
    }
}
