package com.soldierskull.eclipse.structures;

import com.mojang.serialization.Codec;
import com.soldierskull.eclipse.Eclipse;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

/**
 * Estrutura Jigsaw do Hunter Camp - ao contrário da Abyssal Ruins (várias
 * peças conectadas, subterrânea), esta é UMA peça só, na superfície
 * (Etapa 2: "Hunter Camp existe só como .nbt parado, nunca gerava no
 * mundo" - corrigido aqui). Mesmo padrão de {@link AbyssalRuinsStructure}
 * pra reaproveitar os bugfixes já resolvidos lá (STEP/step(),
 * STRUCTURES_REGISTRY, DimensionStructuresSettings via reflection - ver
 * {@link StructureRegistryHandler}).
 */
public class HunterCampStructure extends Structure<NoFeatureConfig> {

    public HunterCampStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    /** Mesmo bugfix do step() documentado em AbyssalRuinsStructure - obrigatório pra não dar NPE na hora de construir QUALQUER bioma. */
    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Start::new;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {

        /** Peça única - não precisa de profundidade além de 1. */
        private static final int MAX_DEPTH = 1;

        public Start(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ,
                    MutableBoundingBox box, int references, long seed) {
            super(structure, chunkX, chunkZ, box, references, seed);
        }

        @Override
        public void generatePieces(DynamicRegistries dynamicRegistries, ChunkGenerator generator,
                                   TemplateManager templateManager, int chunkX, int chunkZ,
                                   Biome biome, NoFeatureConfig config) {

            int x = (chunkX << 4) + 8;
            int z = (chunkZ << 4) + 8;

            // OCEAN_FLOOR_WG ignora agua/folhas e acha o CHAO solido de
            // verdade - com WORLD_SURFACE_WG o Hunter Camp podia nascer
            // flutuando na superficie da agua em vez de no fundo/na costa.
            int y = generator.getFirstOccupiedHeight(x, z, Heightmap.Type.OCEAN_FLOOR_WG);
            BlockPos startPos = new BlockPos(x, y, z);

            VillageConfig jigsawConfig = new VillageConfig(
                    () -> dynamicRegistries
                            .registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
                            .get(new ResourceLocation(Eclipse.MOD_ID, "hunter_camp")),
                    MAX_DEPTH
            );

            JigsawManager.addPieces(
                    dynamicRegistries,
                    jigsawConfig,
                    AbstractVillagePiece::new,
                    generator,
                    templateManager,
                    startPos,
                    this.pieces,
                    this.random,
                    false,
                    false
            );

            this.calculateBoundingBox();
        }
    }
}
