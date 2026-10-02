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
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;


/**
 * Estrutura Jigsaw das Abyssal Ruins.
 *
 * Compatível com Forge 1.16.5 utilizando Mojang Official Mappings.
 */
public class AbyssalRuinsStructure extends Structure<NoFeatureConfig> {

    public AbyssalRuinsStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    /**
     * BUGFIX: a classe base Structure só preenche seu mapa privado
     * estático "STEP" (o que step() consulta) para as estruturas
     * vanilla, através de um método register() privado chamado no
     * carregamento da própria classe Structure. Nossa estrutura é
     * registrada pelo DeferredRegister do Forge, um caminho
     * totalmente diferente que nunca passa por aquele register()
     * privado - então o "STEP" nunca ganha uma entrada pra essa
     * estrutura, e o step() padrão (STEP.get(this)) sempre
     * retornava null aqui, causando NullPointerException em
     * Biome.lambda$new$17 na hora de construir QUALQUER bioma
     * (esse campo do Biome itera todas as estruturas registradas
     * globalmente, não só as do bioma específico).
     *
     * Como step() é público e não é final, sobrescrever aqui
     * resolve sem precisar de reflection.
     */
    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Start::new;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {

        /**
         * Profundidade máxima da geração Jigsaw.
         */
        private static final int MAX_DEPTH = 15;

        public Start(
                Structure<NoFeatureConfig> structure,
                int chunkX,
                int chunkZ,
                MutableBoundingBox box,
                int references,
                long seed
        ) {
            super(
                    structure,
                    chunkX,
                    chunkZ,
                    box,
                    references,
                    seed
            );
        }

        @Override
        public void generatePieces(
                DynamicRegistries dynamicRegistries,
                ChunkGenerator generator,
                TemplateManager templateManager,
                int chunkX,
                int chunkZ,
                Biome biome,
                NoFeatureConfig config
        ) {

            /*
             * Converte as coordenadas do chunk para coordenadas do mundo.
             *
             * O centro aproximado do chunk é utilizado como ponto inicial
             * da estrutura.
             */
            int x = (chunkX << 4) + 8;
            int z = (chunkZ << 4) + 8;

            /*
             * Altura inicial da Abyssal Ruins.
             *
             * Como a estrutura é subterrânea, usamos Y=40.
             */
            BlockPos startPos = new BlockPos(x, 40, z);

            /*
             * Configuração do sistema Jigsaw.
             *
             * TEMPLATE_POOL_REGISTRY é o nome utilizado pelas
             * Official Mappings de Minecraft 1.16.5.
             */
            VillageConfig jigsawConfig = new VillageConfig(
                    () -> dynamicRegistries
                            .registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
                            .get(
                                    new ResourceLocation(
                                            Eclipse.MOD_ID,
                                            "abyssal_ruins/entrance"
                                    )
                            ),
                    MAX_DEPTH
            );

            /*
             * Gera todas as peças conectadas pelo sistema Jigsaw.
             *
             * func_242837_a é o nome disponível nessa API/build
             * de Minecraft 1.16.5.
             */
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

            /*
             * Calcula a bounding box final da estrutura depois
             * que todas as peças foram adicionadas.
             */
            this.calculateBoundingBox();
        }
    }
}