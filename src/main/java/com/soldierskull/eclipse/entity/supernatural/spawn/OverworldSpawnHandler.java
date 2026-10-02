package com.soldierskull.eclipse.entity.supernatural.spawn;

import com.soldierskull.eclipse.Eclipse;
import com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.world.BiomeLoadingEvent;

/**
 * Spawn dos 7 mobs do Overworld (Fase 14, lote 2, secao 34) via
 * BiomeLoadingEvent - adiciona aos biomas vanilla certos.
 *
 * CORRIGIDO: a versao anterior usava "Biome.SpawnListEntry", que NAO
 * existe mais na 1.16.5 - o sistema de spawn de bioma foi refatorado
 * pra "MobSpawnInfo.Spawners" (acessado via event.getSpawns(), que
 * retorna um MobSpawnInfoBuilder). Isso nao tem relacao com mapping
 * MCP/oficial - era uma API desatualizada minha mesmo.
 *
 * IMPORTANTE: este handler NAO usa @Mod.EventBusSubscriber, porque
 * BiomeLoadingEvent roda no FORGE EVENT BUS, nao no MOD bus (mesmo
 * problema ja corrigido em StructureRegistryHandler). O registro correto
 * e feito manualmente em Eclipse.java via
 * MinecraftForge.EVENT_BUS.addListener(OverworldSpawnHandler::onBiomeLoad).
 */
public final class OverworldSpawnHandler {

    private OverworldSpawnHandler() {
    }

    public static void onBiomeLoad(BiomeLoadingEvent event) {
        Biome.Category category = event.getCategory();
        ResourceLocation name = event.getName();

        // Corpo-Seco: secas, desertos, savanas
        if (category == Biome.Category.DESERT || category == Biome.Category.SAVANNA) {
            addSpawn(event, ModSupernaturalEntities.DRY_BODY.get(), 8, 1, 2);
        }

        // Ghoulin da Nevoa + Espectro da Floresta: florestas
        if (category == Biome.Category.FOREST || category == Biome.Category.TAIGA) {
            addSpawn(event, ModSupernaturalEntities.MIST_GHOULIN.get(), 6, 1, 1);
            addSpawn(event, ModSupernaturalEntities.FOREST_SPECTER.get(), 5, 1, 1);
        }

        // Saci Corrompido e Cao de Chofre: qualquer bioma com spawn hostil normal
        if (isValidHostileBiome(category)) {
            addSpawn(event, ModSupernaturalEntities.CORRUPTED_SACI.get(), 4, 1, 1);
            addSpawn(event, ModSupernaturalEntities.SULFUR_HOUND.get(), 5, 2, 4);
        }

        // Wendigo: SOMENTE Taiga/Snowy Taiga/Snowy Tundra/Old Growth Taiga (item 24) - raro
        if (isWendigoBiome(name, category)) {
            addSpawn(event, ModSupernaturalEntities.WENDIGO.get(), 2, 1, 1);
        }

        // Ghost: integrado a categoria sobrenatural. Peso baixo (raro) e a
        // restricao de noite/tempestade ja vem de GhostSpawn.checkGhostSpawnRules.
        // Antes desta mudanca o Ghost nao tinha NENHUMA entrada de spawn de
        // bioma - so regra de posicionamento -, entao nunca nascia sozinho.
        if (isValidHostileBiome(category)) {
            addSpawn(event, ModSupernaturalEntities.GHOST.get(), 3, 1, 1);
        }

        // Mapinguari: sem spawn natural (item 17) - so via MapinguariSpawnHandler
    }

    private static boolean isValidHostileBiome(Biome.Category category) {
        return category != Biome.Category.OCEAN && category != Biome.Category.RIVER
                && category != Biome.Category.NONE && category != Biome.Category.THEEND
                && category != Biome.Category.NETHER;
    }

    private static boolean isWendigoBiome(ResourceLocation name, Biome.Category category) {
        if (category != Biome.Category.TAIGA && category != Biome.Category.ICY) return false;
        if (name == null) return false;
        String p = name.getPath();
        return p.equals("taiga") || p.equals("snowy_taiga") || p.equals("snowy_tundra")
                || p.equals("old_growth_pine_taiga") || p.equals("old_growth_spruce_taiga")
                || p.equals("giant_tree_taiga") || p.equals("giant_spruce_taiga");
    }

    private static void addSpawn(BiomeLoadingEvent event, EntityType<?> type,
                                  int weight, int minCount, int maxCount) {
        event.getSpawns().getSpawner(EntityClassification.MONSTER)
                .add(new MobSpawnInfo.Spawners(type, weight, minCount, maxCount));
    }
}
