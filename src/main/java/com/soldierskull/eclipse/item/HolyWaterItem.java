package com.soldierskull.eclipse.item;

import java.util.List;

import com.soldierskull.eclipse.skills.SupernaturalEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * "Holy Water" da lista de itens do documento original (seção 24) - sem
 * valores exatos definidos, então implementei como um consumível simples
 * (mesmo padrão de uso da Garrafa de Sangue): ao beber, causa dano e
 * Lentidão em criaturas sobrenaturais num raio pequeno ao redor do
 * jogador. PLACEHOLDER de valores: 4 de dano, Lentidão II por 5s, raio
 * de 4 blocos - ajuste se quiser diferente.
 *
 * Concedida em quantidade inicial pela habilidade Preparação Alquímica
 * ao desbloquear (ver HunterSkillEffects) - a receita de crafting fica
 * em data/eclipse/recipes/holy_water.json (Garrafa de Água + Prata Pura).
 */
public class HolyWaterItem extends Item {

    private static final float DAMAGE = 4.0F;
    private static final double RADIUS = 4.0D;

    public HolyWaterItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20;
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

            List<LivingEntity> nearby = world.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(RADIUS), e -> e.isAlive() && SupernaturalEntities.isSupernatural(e));
            for (LivingEntity target : nearby) {
                target.hurt(net.minecraft.util.DamageSource.playerAttack(player), DAMAGE);
                target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 100, 1));
            }

            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }
}
