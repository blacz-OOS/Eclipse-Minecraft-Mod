package com.soldierskull.eclipse.entity.supernatural.emberimp;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/**
 * Diabrete de Brasas (item 13.6). Rapido, persegue direto, 20% de
 * chance de incendiar (5s).
 *
 * IGNITE_PULSE sincronizado (spec 6.4: "quando a chance de 5%(sic, é
 * 20% no balanceamento) é ativada, brasas aumentam momentaneamente e o
 * ataque recebe pequena intensificacao") - sem isso o cliente nao
 * saberia diferenciar um ataque normal de um que incendiou.
 */
public class EmberImpEntity extends MonsterEntity implements ISupernaturalMob {

    private static final int IGNITE_PULSE_DURATION = 8; // ~0.4s

    private static final DataParameter<Integer> IGNITE_PULSE =
            EntityDataManager.defineId(EmberImpEntity.class, DataSerializers.INT);

    public EmberImpEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IGNITE_PULSE, 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.entityData.get(IGNITE_PULSE) > 0) {
            this.entityData.set(IGNITE_PULSE, this.entityData.get(IGNITE_PULSE) - 1);
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3D, true));
        this.goalSelector.addGoal(2, new net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && this.random.nextFloat() < 0.20F) {
            target.setSecondsOnFire(5);
            this.entityData.set(IGNITE_PULSE, IGNITE_PULSE_DURATION);
        }
        return result;
    }

    // ---------- Consultado pelo EmberImpModel (cliente) ----------

    public boolean isIgnitePulseActive() {
        return this.entityData.get(IGNITE_PULSE) > 0;
    }

    public int getDeathTime() {
        return this.deathTime;
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.ABYSSAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.COMMON;
    }
}
