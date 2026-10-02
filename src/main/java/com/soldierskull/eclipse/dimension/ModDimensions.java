package com.soldierskull.eclipse.dimension;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Chave de registro (ResourceKey) da dimensao do Abismo.
 *
 * A dimensao em si (bioma + dimension_type + dimension) e definida via
 * datapack padrao em:
 *   data/eclipse/worldgen/biome/abyss.json
 *   data/eclipse/dimension_type/abyss_type.json
 *   data/eclipse/dimension/abyss.json
 *
 * Isso e o suficiente para o Forge 1.16.5 carregar a dimensao automaticamente
 * (nao precisa de registro adicional em codigo para a dimensao existir).
 *
 * Esta classe serve apenas para o CODIGO do mod poder referenciar a
 * dimensao (ex.: checar se um jogador esta nela, ou teleportar), sem
 * espalhar a ResourceLocation crua "eclipse:abyss" por varios arquivos.
 */
public final class ModDimensions {

    public static final RegistryKey<World> ABYSS = RegistryKey.create(
            net.minecraft.util.registry.Registry.DIMENSION_REGISTRY,
            new ResourceLocation(Eclipse.MOD_ID, "abyss")
    );

    private ModDimensions() {
    }
}
