package com.soldierskull.eclipse.ritual.altar;

import com.soldierskull.eclipse.block.ModBlocks;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/** Primeiro TileEntityType do mod - o projeto ainda não tinha nenhum TileEntity antes do Altar. */
public final class ModTileEntities {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, "eclipse");

    public static final RegistryObject<TileEntityType<AltarTileEntity>> ALTAR = TILE_ENTITIES.register("altar",
            () -> TileEntityType.Builder.of(AltarTileEntity::new, ModBlocks.ALTAR.get()).build(null));

    public static final RegistryObject<TileEntityType<BloodAltarTileEntity>> BLOOD_ALTAR = TILE_ENTITIES.register("blood_altar",
            () -> TileEntityType.Builder.of(BloodAltarTileEntity::new, ModBlocks.BLOOD_ALTAR.get()).build(null));

    public static final RegistryObject<TileEntityType<MoonAltarTileEntity>> MOON_ALTAR = TILE_ENTITIES.register("moon_altar",
            () -> TileEntityType.Builder.of(MoonAltarTileEntity::new, ModBlocks.MOON_ALTAR.get()).build(null));

    public static final RegistryObject<TileEntityType<AbyssAltarCoreTileEntity>> ABYSS_ALTAR_CORE = TILE_ENTITIES.register("abyss_altar_core",
            () -> TileEntityType.Builder.of(AbyssAltarCoreTileEntity::new, ModBlocks.ABYSS_ALTAR_CORE.get()).build(null));

    private ModTileEntities() {
    }

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
