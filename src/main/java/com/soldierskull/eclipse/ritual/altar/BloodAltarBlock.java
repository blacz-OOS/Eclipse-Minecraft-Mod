package com.soldierskull.eclipse.ritual.altar;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

/** Mesma interação de {@link AltarBlock} (sem GUI) - só troca o TileEntity concreto. */
public class BloodAltarBlock extends AltarBlock {

    public BloodAltarBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BloodAltarTileEntity();
    }
}
