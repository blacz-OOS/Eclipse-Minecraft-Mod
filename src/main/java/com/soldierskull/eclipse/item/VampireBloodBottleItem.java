package com.soldierskull.eclipse.item;

import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * "Sangue Engarrafado" - a fonte de cura da Regeneração Sanguínea/
 * Regeneração Superior do Vampiro. Você disse que ia adicionar depois,
 * mas pediu pra eu já adicionar agora - implementado como um item
 * simples de beber (mesma UseAction de uma poção).
 *
 * Cura base: 2 HP.
 * Com Regeneração Sanguínea desbloqueada: 2x (4 HP) - "enquanto faz isso
 * se cura duas vezes mais", conforme especificado.
 * Com Regeneração Superior também desbloqueada: mais 2x em cima disso
 * (8 HP) - SIMPLIFICAÇÃO: o documento original condicionava a Superior a
 * "estar com a fome cheia", conceito que não existe (sem sistema de fome
 * vampírica ainda) - por ora ela sempre dobra o valor da Sanguínea, sem
 * essa condição. Ajuste quando o sistema de fome existir.
 */
public class VampireBloodBottleItem extends Item {

    private static final float BASE_HEAL = 2.0F;

    public VampireBloodBottleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.startUsingItem(hand);
        return ActionResult.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                float heal = BASE_HEAL;
                if (stats.hasUnlockedSkill("vampiro_regeneracao_sanguinea")) {
                    heal *= 2.0F;
                }
                if (stats.hasUnlockedSkill("vampiro_regeneracao_superior")) {
                    heal *= 2.0F;
                }
                player.heal(heal);
            });
            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }
}
