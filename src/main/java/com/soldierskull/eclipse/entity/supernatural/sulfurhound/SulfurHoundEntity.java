package com.soldierskull.eclipse.entity.supernatural.sulfurhound;

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
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/**
 * Cao de Chofre (item 16.4). Matilha - ao atacar, cães aliados em 20
 * blocos entram na perseguicao. 5% de chance de incendiar (7s). Imune
 * a fogo.
 *
 * ALERT_PULSE sincronizado (spec 10.2: "orelhas levantam; cabeça baixa;
 * postura agressiva" ao detectar - momento perceptivel, precisa de sync
 * ja que setTarget so muda estado server-side).
 */
public class SulfurHoundEntity extends MonsterEntity implements ISupernaturalMob {

    private static final double PACK_RADIUS = 20.0D;
    private static final int ALERT_PULSE_DURATION = 8;

    private static final DataParameter<Integer> ALERT_PULSE =
            EntityDataManager.defineId(SulfurHoundEntity.class, DataSerializers.INT);

    public SulfurHoundEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        this.xpReward = 6;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ALERT_PULSE, 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.entityData.get(ALERT_PULSE) > 0) {
            this.entityData.set(ALERT_PULSE, this.entityData.get(ALERT_PULSE) - 1);
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void setTarget(LivingEntity target) {
        boolean isNewAggro = target != null && this.getTarget() == null;
        super.setTarget(target);
        if (isNewAggro && !this.level.isClientSide) {
            alertPack(target);
            this.entityData.set(ALERT_PULSE, ALERT_PULSE_DURATION);
        }
    }

    private void alertPack(LivingEntity target) {
        for (SulfurHoundEntity ally : this.level.getEntitiesOfClass(SulfurHoundEntity.class,
                this.getBoundingBox().inflate(PACK_RADIUS))) {
            if (ally != this && ally.getTarget() == null) {
                ally.setTarget(target);
            }
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && this.random.nextFloat() < 0.05F) {
            target.setSecondsOnFire(7);
        }
        return result;
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.OVERWORLD_SUPERNATURAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.COMMON;
    }

    // ---------- Consultado pelo SulfurHoundModel (cliente) ----------

    public boolean isAlertPulseActive() {
        return this.entityData.get(ALERT_PULSE) > 0;
    }

    public int getDeathTime() {
        return this.deathTime;
    }
}
