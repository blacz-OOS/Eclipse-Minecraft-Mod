package com.soldierskull.eclipse.entity.supernatural.ghost;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, Eclipse.MOD_ID);

    public static final RegistryObject<EntityType<GhostEntity>> GHOST =
            ENTITY_TYPES.register("ghost",
                    () -> EntityType.Builder.of(GhostEntity::new, EntityClassification.MONSTER)
                            .sized(0.6F, 1.95F) // Tamanho da caixa de colisão (largura, altura)
                            .build(new ResourceLocation(Eclipse.MOD_ID, "ghost").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}