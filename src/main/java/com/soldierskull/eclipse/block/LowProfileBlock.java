package com.soldierskull.eclipse.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

/**
 * Bloco de altura reduzida. O modelo ainda é definido pelo arquivo JSON;
 * esta classe só faz a seleção e a colisão seguirem exatamente sua altura.
 */
public class LowProfileBlock extends Block {

    private final VoxelShape shape;

    public LowProfileBlock(Properties properties, double height) {
        super(properties);
        this.shape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, height, 16.0D);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return this.shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return this.shape;
    }
}
