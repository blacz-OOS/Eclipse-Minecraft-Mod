package com.soldierskull.eclipse.ritual.altar;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class MoonAltarBlock extends AltarBlock {

    public MoonAltarBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MoonAltarTileEntity();
    }
}
