package com.soldierskull.eclipse.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

/**
 * Representação física da "Armadilha de Caçador/Prata" (ver
 * {@code TrapRegistry}) - antes era só partículas de redstone no chão,
 * agora existe de verdade como bloco (modelo enviado pelo usuário,
 * 21 elementos, ~4/16 de altura). Toda a lógica de jogo (atrair,
 * aplicar Lentidão, expirar) continua em {@code TrapRegistry.tick()} -
 * este bloco é colocado/removido por ele, não tem lógica própria.
 */
public class TrapBlock extends Block {

    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 1.0D, 14.0D, 4.0D, 15.0D);

    public TrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, net.minecraft.util.math.BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, net.minecraft.util.math.BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
