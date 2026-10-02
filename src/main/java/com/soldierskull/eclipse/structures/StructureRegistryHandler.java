package com.soldierskull.eclipse.structures;

import com.google.common.collect.ImmutableMap;
import com.soldierskull.eclipse.Eclipse;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Integra a Abyssal Ruins e o Hunter Camp ao sistema de geração de estruturas
 * do Minecraft Forge 1.16.5.
 *
 * Responsabilidades:
 * 1. Criar as StructureFeature configuradas.
 * 2. Registrar as StructureFeature no WorldGenRegistries.
 * 3. Configurar o espaçamento das estruturas.
 * 4. Adicionar as estruturas aos biomas apropriados.
 */
public final class StructureRegistryHandler {

    private static final Logger LOGGER =
            LogManager.getLogger(Eclipse.MOD_ID);

    /* ============================================================
     * CONFIGURAÇÃO DE GERAÇÃO
     * ============================================================ */

    /** Espaçamento médio da Abyssal Ruins em chunks. */
    private static final int SPACING = 24;

    /** Separação mínima da Abyssal Ruins em chunks. */
    private static final int SEPARATION = 8;

    /** Salt exclusivo da Abyssal Ruins. */
    private static final int SALT = 984712369;

    /** Hunter Camp: estrutura de apoio relativamente rara. */
    private static final int SPACING_HUNTER_CAMP = 20;
    private static final int SEPARATION_HUNTER_CAMP = 8;
    private static final int SALT_HUNTER_CAMP = 984712370;

    /** StructureFeature configurada da Abyssal Ruins. */
    public static StructureFeature<
            NoFeatureConfig,
            ? extends Structure<NoFeatureConfig>
            > CONFIGURED_ABYSSAL_RUINS;

    /** StructureFeature configurada do Hunter Camp. */
    public static StructureFeature<
            NoFeatureConfig,
            ? extends Structure<NoFeatureConfig>
            > CONFIGURED_HUNTER_CAMP;

    private static final Set<ResourceLocation> HUNTER_CAMP_BIOMES =
            new HashSet<>(Arrays.asList(
                    new ResourceLocation("minecraft", "plains"),
                    new ResourceLocation("minecraft", "sunflower_plains"),
                    new ResourceLocation("minecraft", "forest"),
                    new ResourceLocation("minecraft", "flower_forest"),
                    new ResourceLocation("minecraft", "savanna"),
                    new ResourceLocation("minecraft", "taiga")
            ));

    private StructureRegistryHandler() {
    }

    /**
     * Configura e registra as duas estruturas.
     * Deve ser executado durante o Common Setup.
     */
    public static void setup() {

        /* ========================================================
         * ABYSSAL RUINS
         * ======================================================== */

        Structure<NoFeatureConfig> structure =
                ModStructures.ABYSSAL_RUINS.get();

        Structure.STRUCTURES_REGISTRY.put(
                "abyssal_ruins",
                structure
        );

        CONFIGURED_ABYSSAL_RUINS =
                structure.configured(
                        NoFeatureConfig.INSTANCE
                );

        Registry.register(
                WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE,
                new ResourceLocation(
                        Eclipse.MOD_ID,
                        "abyssal_ruins"
                ),
                CONFIGURED_ABYSSAL_RUINS
        );

        addDimensionalSpacing(
                structure,
                new StructureSeparationSettings(
                        SPACING,
                        SEPARATION,
                        SALT
                )
        );

        LOGGER.info(
                "[Eclipse] Abyssal Ruins configurada e registrada."
        );

        /* ========================================================
         * HUNTER CAMP
         * ======================================================== */

        Structure<NoFeatureConfig> hunterCampStructure =
                ModStructures.HUNTER_CAMP.get();

        Structure.STRUCTURES_REGISTRY.put(
                "hunter_camp",
                hunterCampStructure
        );

        CONFIGURED_HUNTER_CAMP =
                hunterCampStructure.configured(
                        NoFeatureConfig.INSTANCE
                );

        Registry.register(
                WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE,
                new ResourceLocation(
                        Eclipse.MOD_ID,
                        "hunter_camp"
                ),
                CONFIGURED_HUNTER_CAMP
        );

        addDimensionalSpacing(
                hunterCampStructure,
                new StructureSeparationSettings(
                        SPACING_HUNTER_CAMP,
                        SEPARATION_HUNTER_CAMP,
                        SALT_HUNTER_CAMP
                )
        );

        LOGGER.info(
                "[Eclipse] Hunter Camp configurado e registrado."
        );
    }

    /**
     * Adiciona a estrutura às configurações padrão de espaçamento.
     *
     * Os campos privados de DimensionStructuresSettings são expostos
     * pelo Access Transformer do mod; não usamos reflection aqui.
     */
    private static void addDimensionalSpacing(
            Structure<?> structure,
            StructureSeparationSettings settings
    ) {
        DimensionStructuresSettings.DEFAULTS =
                ImmutableMap.<
                                Structure<?>,
                                StructureSeparationSettings
                                >builder()
                        .putAll(DimensionStructuresSettings.DEFAULTS)
                        .put(structure, settings)
                        .build();

        LOGGER.info(
                "[Eclipse] Espaçamento da estrutura {} registrado.",
                structure.getRegistryName()
        );
    }

    /* ============================================================
     * COMMON SETUP
     * ============================================================ */

    /**
     * Este método deve ser registrado no MOD EVENT BUS.
     */
    @SubscribeEvent
    public static void onCommonSetup(
            FMLCommonSetupEvent event
    ) {
        event.enqueueWork(
                StructureRegistryHandler::setup
        );

        event.enqueueWork(
                com.soldierskull.eclipse.entity.supernatural.spawn
                        .OverworldSpawnPredicates::setup
        );
    }

    /* ============================================================
     * BIOME LOADING
     * ============================================================ */

    @SubscribeEvent
    public static void onBiomeLoad(
            BiomeLoadingEvent event
    ) {
        ResourceLocation biomeName = event.getName();

        if (biomeName == null) {
            return;
        }

        ResourceLocation abyssBiome =
                new ResourceLocation(
                        Eclipse.MOD_ID,
                        "abyss"
                );

        if (!biomeName.equals(abyssBiome)) {
            addHunterCampIfSuitable(event, biomeName);
            return;
        }

        event.getGeneration()
                .getStructures()
                .add(() -> CONFIGURED_ABYSSAL_RUINS);

        LOGGER.debug(
                "[Eclipse] Abyssal Ruins adicionada ao bioma {}.",
                biomeName
        );
    }

    private static void addHunterCampIfSuitable(
            BiomeLoadingEvent event,
            ResourceLocation biomeName
    ) {
        if (!HUNTER_CAMP_BIOMES.contains(biomeName)) {
            return;
        }

        event.getGeneration()
                .getStructures()
                .add(() -> CONFIGURED_HUNTER_CAMP);

        LOGGER.debug(
                "[Eclipse] Hunter Camp adicionado ao bioma {}.",
                biomeName
        );
    }

    /* ============================================================
     * WORLD LOAD
     * ============================================================ */

    /**
     * Garante que cada ChunkGenerator de mundo carregado possua o
     * espaçamento das estruturas customizadas.
     *
     * O campo structureConfig é exposto pelo Access Transformer;
     * não usamos reflection.
     */
    @SubscribeEvent
    public static void onWorldLoad(
            WorldEvent.Load event
    ) {
        if (!(event.getWorld() instanceof ServerWorld)) {
            return;
        }

        ServerWorld serverWorld = (ServerWorld) event.getWorld();

        try {
            ChunkGenerator generator =
                    serverWorld.getChunkSource().getGenerator();

            DimensionStructuresSettings settings =
                    generator.getSettings();

            Map<
                    Structure<?>,
                    StructureSeparationSettings
                    > current =
                    new HashMap<>(settings.structureConfig());

            current.putIfAbsent(
                    ModStructures.ABYSSAL_RUINS.get(),
                    new StructureSeparationSettings(
                            SPACING,
                            SEPARATION,
                            SALT
                    )
            );

            current.putIfAbsent(
                    ModStructures.HUNTER_CAMP.get(),
                    new StructureSeparationSettings(
                            SPACING_HUNTER_CAMP,
                            SEPARATION_HUNTER_CAMP,
                            SALT_HUNTER_CAMP
                    )
            );

            settings.structureConfig = current;

            LOGGER.info(
                    "[Eclipse] Espaçamento da Abyssal Ruins e do Hunter Camp " +
                            "corrigido no mundo {}.",
                    serverWorld.dimension().location()
            );

        } catch (Exception e) {
            LOGGER.error(
                    "[Eclipse] Falha ao corrigir o espaçamento das " +
                            "estruturas no mundo.",
                    e
            );
        }
    }
}
