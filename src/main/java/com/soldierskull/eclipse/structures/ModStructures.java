package com.soldierskull.eclipse.structures;

import com.soldierskull.eclipse.Eclipse;

import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registro das estruturas do Eclipse.
 *
 * A Abyssal Ruins é registrada como uma Structure customizada
 * porque utiliza geração baseada em Jigsaw.
 */
public final class ModStructures {

    /**
     * Registro das Structures do mod.
     */
    public static final DeferredRegister<Structure<?>> STRUCTURES =
            DeferredRegister.create(
                    ForgeRegistries.STRUCTURE_FEATURES,
                    Eclipse.MOD_ID
            );

    /**
     * Abyssal Ruins.
     */
    public static final RegistryObject<Structure<NoFeatureConfig>> ABYSSAL_RUINS =
            STRUCTURES.register(
                    "abyssal_ruins",
                    () -> new AbyssalRuinsStructure(
                            NoFeatureConfig.CODEC
                    )
            );

    /**
     * Hunter Camp (Etapa 2 - antes só existia como .nbt parado, nunca
     * gerava no mundo de verdade).
     */
    public static final RegistryObject<Structure<NoFeatureConfig>> HUNTER_CAMP =
            STRUCTURES.register(
                    "hunter_camp",
                    () -> new HunterCampStructure(
                            NoFeatureConfig.CODEC
                    )
            );

    private ModStructures() {
        // Classe utilitária.
    }

    /**
     * Registra as estruturas no Event Bus do mod.
     *
     * Deve ser chamado no construtor principal do Eclipse.
     */
    public static void register(IEventBus modEventBus) {
        STRUCTURES.register(modEventBus);
    }
}