package com.soldierskull.eclipse.ritual.altar;

import javax.annotation.Nullable;

import com.soldierskull.eclipse.ritual.RitualManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * O Altar comum (Bloco A/#1) - um bloco só, sem GUI (Bloco B/#9-10,
 * decisão confirmada). Segurando um item = tenta usá-lo como
 * ingrediente principal. Mão vazia = tenta iniciar o ritual pronto
 * (READY). O status detalhado (círculo, power, condições) é mostrado
 * pelo {@link AltarOverlayHandler} quando o jogador mira no bloco -
 * não existe aqui uma mensagem de status completa a cada clique, só o
 * resultado da ação.
 */
public class AltarBlock extends Block {

    public AltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new AltarTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide || !(world instanceof ServerWorld)) {
            return ActionResultType.SUCCESS;
        }
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (!(tileEntity instanceof AltarTileEntity)) {
            return ActionResultType.PASS;
        }
        AltarTileEntity altar = (AltarTileEntity) tileEntity;
        ItemStack held = player.getItemInHand(hand);

        if (held.isEmpty()) {
            boolean started = RitualManager.tryStart(altar, (ServerWorld) world, (ServerPlayerEntity) player);
            player.displayClientMessage(started
                    ? new net.minecraft.util.text.StringTextComponent("Ritual iniciado.")
                    : new net.minecraft.util.text.StringTextComponent("O altar ainda nao esta pronto."), true);
            return ActionResultType.CONSUME;
        }

        RitualManager.offerPrimaryIngredient(altar, player, hand);
        player.displayClientMessage(new net.minecraft.util.text.StringTextComponent("Ingrediente colocado no altar."), true);
        return ActionResultType.CONSUME;
    }
}
