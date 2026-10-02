package com.soldierskull.eclipse.item;

import com.soldierskull.eclipse.skills.TrapRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * "Hunter Trap" da lista de itens do documento original (seção 24) -
 * versão craftável/consumível do mesmo mecanismo da habilidade Armadilha
 * de Caçador/Prata (ver TrapRegistry). Diferente da habilidade, não
 * exige a skill desbloqueada - é um item que qualquer jogador pode
 * craftar e usar, dando uma segunda via de acesso ao mesmo efeito
 * (marca vermelha, atrai sobrenaturais em 10 blocos, Lentidão extrema
 * ao se aproximar).
 *
 * CORRIGIDO: antes só existia use(), que é o clique genérico - a
 * armadilha sempre nascia em player.blockPosition(), ou seja, nos pés
 * do jogador, independente de onde ele estivesse mirando. Agora
 * useOn() trata o clique EM UM BLOCO e planta a armadilha na face
 * clicada (em cima do bloco, se clicou no topo). O use() continua
 * existindo só como fallback pra quando o jogador clica no ar, sem
 * nenhum bloco na mira.
 *
 * Dá feedback explícito ao jogador (colocada / falhou) - sem isso, era
 * fácil confundir "o gatilho disparou rápido porque tinha um zumbi por
 * perto" (zumbi conta como alvo sobrenatural, ver SupernaturalEntities)
 * com "o item simplesmente não fez nada".
 */
public class HunterTrapItem extends Item {

    private static final long TRAP_DURATION_MILLIS = 60_000L;

    public HunterTrapItem(Item.Properties properties) {
        super(properties);
    }

    /**
     * Clique em um bloco: planta a armadilha na posição adjacente à face
     * clicada. É esse o caminho normal de uso.
     */
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();

        if (world.isClientSide || !(player instanceof ServerPlayerEntity)) {
            return ActionResultType.SUCCESS;
        }

        // posição livre adjacente à face clicada (clicou no topo -> em cima do bloco)
        BlockPos target = context.getClickedPos().relative(context.getClickedFace());

        if (!tryPlace((ServerWorld) world, player, target)) {
            return ActionResultType.FAIL;
        }

        consume(player, context.getItemInHand());
        return ActionResultType.CONSUME;
    }

    /**
     * Fallback: clique no ar (sem bloco na mira) continua plantando aos
     * pés do jogador, como era antes.
     */
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide || !(player instanceof ServerPlayerEntity)) {
            return ActionResult.pass(stack);
        }

        if (!tryPlace((ServerWorld) world, player, player.blockPosition())) {
            return ActionResult.fail(stack);
        }

        consume(player, stack);
        return ActionResult.success(stack);
    }

    private boolean tryPlace(ServerWorld world, PlayerEntity player, BlockPos pos) {
        boolean placed = TrapRegistry.place(world, pos, TRAP_DURATION_MILLIS);
        if (!placed) {
            player.displayClientMessage(
                    new StringTextComponent("Não há espaço livre aqui para a armadilha."), true);
            return false;
        }
        player.displayClientMessage(
                new StringTextComponent("Armadilha colocada - atrai sobrenaturais por 60s."), true);
        return true;
    }

    private void consume(PlayerEntity player, ItemStack stack) {
        if (!player.abilities.instabuild) {
            stack.shrink(1);
        }
    }
}
