package com.soldierskull.eclipse.item;

import java.util.UUID;

import com.soldierskull.eclipse.skills.BuffManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * "Lunar Essence" da lista de itens do documento original (seção 24) -
 * dei função real ligada à Fúria Lunar (Fase 8): ao beber, aplica o
 * mesmo multiplicador de fase lunar em dano/velocidade, mas MENOR (60%
 * do valor da skill) e MAIS CURTO (15s) - e funciona a QUALQUER hora do
 * dia, ao contrário da skill (que só funciona à noite). Pensada como uma
 * versão "de bolso", mais fraca, utilizável por qualquer jogador (não só
 * quem já tem a skill). Valores placeholder, sem número exato definido.
 */
public class LunarEssenceItem extends Item {

    private static final long DURATION = 15_000L;
    private static final double PORTION_OF_SKILL = 0.6D;

    private static final double[] MOON_MULTIPLIER = {
            2.0D, 1.5D, 1.0D, 0.75D, 0.5D, 0.75D, 1.0D, 1.5D
    };

    public LunarEssenceItem(Item.Properties properties) {
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
            int moonPhase = player.level.getMoonPhase();
            double multiplier = 1.0D + (MOON_MULTIPLIER[moonPhase] - 1.0D) * PORTION_OF_SKILL;

            BuffManager.applyTimedMultiplier(player, Attributes.ATTACK_DAMAGE, uuid("lunar_essence_dano"), "eclipse.lunar_essence.dano", multiplier, DURATION);
            BuffManager.applyTimedMultiplier(player, Attributes.MOVEMENT_SPEED, uuid("lunar_essence_vel"), "eclipse.lunar_essence.vel", multiplier, DURATION);

            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }

    private static UUID uuid(String seed) {
        return UUID.nameUUIDFromBytes(("eclipse.item." + seed).getBytes());
    }
}
