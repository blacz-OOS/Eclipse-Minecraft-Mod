package com.soldierskull.eclipse.entity.supernatural.mistghoulin;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/** Ghoulin da Nevoa (item 16.2). Zumbi sobrenatural, ataque com chance de Veneno I (5s). */
public class MistGhoulinEntity extends net.minecraft.entity.monster.MonsterEntity implements ISupernaturalMob {

    public MistGhoulinEntity(EntityType<? extends net.minecraft.entity.monster.MonsterEntity> type, World world) {
        super(type, world);
        this.xpReward = 8;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return net.minecraft.entity.monster.MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof LivingEntity && this.random.nextFloat() < 0.30F) {
            ((LivingEntity) target).addEffect(new EffectInstance(Effects.POISON, 5 * 20, 0));
        }
        return result;
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.OVERWORLD_SUPERNATURAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.DANGEROUS;
    }

    public int getDeathTime() {
        return this.deathTime;
    }
}
