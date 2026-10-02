package com.soldierskull.eclipse.entity.supernatural.drybody;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import com.soldierskull.eclipse.skills.EclipseEventManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * Corpo-Seco (item 16.1). Combatente direto, chance de aplicar Fraqueza I
 * (7s). Durante o Eclipse, atributos melhoram (35 HP / 6 dano / +10%
 * velocidade) - lido em tempo real a cada tick via isEclipseActive, sem
 * precisar recriar a entidade.
 */
public class DryBodyEntity extends MonsterEntity implements ISupernaturalMob {

    private static final double BASE_SPEED = 0.30D;
    private boolean eclipseBoosted = false;

    public DryBodyEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        this.xpReward = 7;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide || !(this.level instanceof ServerWorld)) return;

        boolean active = EclipseEventManager.isActive((ServerWorld) this.level);
        if (active != eclipseBoosted) {
            eclipseBoosted = active;
            if (active) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(35.0D);
                this.setHealth(this.getHealth() + 7.0F); // ajusta o HP atual proporcionalmente ao novo maximo
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0D);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * 1.10D);
            } else {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(28.0D);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.0D);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof net.minecraft.entity.LivingEntity && this.random.nextFloat() < 0.35F) {
            ((net.minecraft.entity.LivingEntity) target).addEffect(new EffectInstance(Effects.WEAKNESS, 7 * 20, 0));
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
