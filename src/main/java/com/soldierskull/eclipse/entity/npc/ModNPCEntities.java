package com.soldierskull.eclipse.entity.npc;

import com.soldierskull.eclipse.Eclipse;
import com.soldierskull.eclipse.entity.npc.cultist.CultistNPCEntity;
import com.soldierskull.eclipse.entity.npc.hunter.HunterNPCEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/** Registro dos NPCs atmosféricos das estruturas (Fase 13 - Hunter Camp e Abyssal Shrine). */
@Mod.EventBusSubscriber(modid = Eclipse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModNPCEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, Eclipse.MOD_ID);

    public static final RegistryObject<EntityType<HunterNPCEntity>> HUNTER_NPC =
            ENTITY_TYPES.register("hunter_npc", () -> EntityType.Builder
                    .of(HunterNPCEntity::new, EntityClassification.CREATURE)
                    .sized(0.6F, 1.95F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "hunter_npc").toString()));

    public static final RegistryObject<EntityType<CultistNPCEntity>> CULTIST_NPC =
            ENTITY_TYPES.register("cultist_npc", () -> EntityType.Builder
                    .of(CultistNPCEntity::new, EntityClassification.CREATURE)
                    .sized(0.6F, 1.95F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "cultist_npc").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(HUNTER_NPC.get(), HunterNPCEntity.createAttributes().build());
        event.put(CULTIST_NPC.get(), CultistNPCEntity.createAttributes().build());
    }
}
