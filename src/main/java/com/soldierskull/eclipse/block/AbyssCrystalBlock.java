package com.soldierskull.eclipse.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

/**
 * Cristal do Abismo - "de forma semelhante ao cristal de ametista"
 * (confirmado), pensado pra rituais (Fase 13 futuramente). O Minecraft
 * 1.16.5 é anterior à Caves & Cliffs (ametista só existe a partir da
 * 1.17), então não há uma classe vanilla pra herdar - implementei do
 * zero com o mesmo comportamento essencial: gruda na face que você
 * clicar (FACING), não é um cubo sólido (hitbox menor, forma de
 * cristal saindo da superfície) e quebra sozinho se o bloco de suporte
 * for removido.
 *
 * Reaproveitada por dois blocos: Abyss Crystal (sem função especial -
 * função de restaurar corrupção foi removida do item, por instrução
 * sua) e Charged Abyss Crystal (mesma forma, só muda Properties -
 * brilha, ver ModBlocks).
 */
public class AbyssCrystalBlock extends DirectionalBlock {

    protected static final VoxelShape UP_SHAPE = Block.box(4, 0, 4, 12, 12, 12);
    protected static final VoxelShape DOWN_SHAPE = Block.box(4, 4, 4, 12, 16, 12);
    protected static final VoxelShape NORTH_SHAPE = Block.box(4, 4, 4, 12, 12, 16);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(4, 4, 0, 12, 12, 12);
    protected static final VoxelShape EAST_SHAPE = Block.box(0, 4, 4, 12, 12, 12);
    protected static final VoxelShape WEST_SHAPE = Block.box(4, 4, 4, 16, 12, 12);

    public AbyssCrystalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)) {
            case DOWN:
                return DOWN_SHAPE;
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return UP_SHAPE;
        }
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos attachedPos = pos.relative(facing.getOpposite());
        return world.getBlockState(attachedPos).isFaceSturdy(world, attachedPos, facing);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (dir.getOpposite() == state.getValue(FACING) && !state.canSurvive(world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, net.minecraft.pathfinding.PathType type) {
        return false;
    }
}
