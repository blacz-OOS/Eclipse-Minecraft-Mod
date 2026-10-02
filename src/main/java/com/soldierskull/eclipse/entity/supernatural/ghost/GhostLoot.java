package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = "eclipse")
public class GhostLoot {

    @SubscribeEvent
    public static void onGhostDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof GhostEntity) {
            GhostEntity ghost = (GhostEntity) event.getEntityLiving();
            
            if (ghost.getRandom().nextFloat() < 0.50F) {
                ItemStack corruptedBone = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("eclipse", "corrupted_bone")));
                ghost.spawnAtLocation(corruptedBone);
            }
        }
    }

    @SubscribeEvent
    public static void onXpDrop(LivingExperienceDropEvent event) {
        if (event.getEntityLiving() instanceof GhostEntity) {
            event.setDroppedExperience(5);
        }
    }
}
