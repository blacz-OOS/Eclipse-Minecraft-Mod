package com.soldierskull.eclipse.entity.supernatural.voidobserver;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * Vazio Observador (item 13.5). Voa, mantem distancia 8-12 blocos do
 * jogador (recua se < 8, aproxima se > 12), precisa de linha de visao
 * pra atacar, dispara VoidBoltEntity a cada ~3s.
 *
 * ATUALIZADO (animacao): o disparo era instantaneo assim que o cooldown
 * zerava - a spec (5.5) pede uma preparacao visivel (pupila contrai,
 * brilho intensifica, tentaculos rigidos, pequena pausa) ANTES do tiro.
 * Adicionei essa janela de windup, mais o pulso de "travou no alvo"
 * (5.3) e um breve relaxamento pos-tiro (5.6) - tudo sincronizado via
 * SynchedEntityData pro VoidObserverModel animar no cliente.
 */
public class VoidObserverEntity extends MonsterEntity implements ISupernaturalMob {

    private static final int WINDUP_DURATION = 6;   // ~0.3s de preparacao antes do tiro
    private static final int POST_FIRE_DURATION = 6; // ~0.3s de relaxamento apos o tiro
    private static final int LOCK_PULSE_DURATION = 6;

    private static final DataParameter<Boolean> TARGET_LOCKED =
            EntityDataManager.defineId(VoidObserverEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> LOCK_PULSE_TICKS =
            EntityDataManager.defineId(VoidObserverEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> WINDUP_TICKS =
            EntityDataManager.defineId(VoidObserverEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> POST_FIRE_TICKS =
            EntityDataManager.defineId(VoidObserverEntity.class, DataSerializers.INT);

    public VoidObserverEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.moveControl = new FlyingMovementController(this, 10, true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_LOCKED, false);
        this.entityData.define(LOCK_PULSE_TICKS, 0);
        this.entityData.define(WINDUP_TICKS, 0);
        this.entityData.define(POST_FIRE_TICKS, 0);
    }

    @Override
    public void setTarget(LivingEntity target) {
        boolean acquiring = target != null && this.getTarget() == null;
        super.setTarget(target);
        if (!this.level.isClientSide) {
            this.entityData.set(TARGET_LOCKED, target != null);
            if (acquiring) {
                this.entityData.set(LOCK_PULSE_TICKS, LOCK_PULSE_DURATION);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) return;
        if (this.entityData.get(LOCK_PULSE_TICKS) > 0) {
            this.entityData.set(LOCK_PULSE_TICKS, this.entityData.get(LOCK_PULSE_TICKS) - 1);
        }
        if (this.entityData.get(POST_FIRE_TICKS) > 0) {
            this.entityData.set(POST_FIRE_TICKS, this.entityData.get(POST_FIRE_TICKS) - 1);
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 18.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FLYING_SPEED, 0.22D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new KeepDistanceAndShootGoal(this));
        this.targetSelector.addGoal(1, new net.minecraft.entity.ai.goal.NearestAttackableTargetGoal<>(
                this, PlayerEntity.class, true));
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.ABYSSAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.COMMON;
    }

    // ---------- Consultado pelo VoidObserverModel (cliente) ----------

    public boolean isTargetLocked() {
        return this.entityData.get(TARGET_LOCKED);
    }

    public boolean isLockPulseActive() {
        return this.entityData.get(LOCK_PULSE_TICKS) > 0;
    }

    public boolean isWindingUp() {
        return this.entityData.get(WINDUP_TICKS) > 0;
    }

    public float getWindupProgress() {
        return this.entityData.get(WINDUP_TICKS) / (float) WINDUP_DURATION;
    }

    public boolean isPostFire() {
        return this.entityData.get(POST_FIRE_TICKS) > 0;
    }

    public int getDeathTime() {
        return this.deathTime;
    }

    static class KeepDistanceAndShootGoal extends Goal {
        private final VoidObserverEntity mob;
        private int shootCooldown = 0;
        private int windup = 0;

        KeepDistanceAndShootGoal(VoidObserverEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null && mob.getTarget().isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = mob.getTarget();
            if (target == null) return;

            mob.getLookControl().setLookAt(target, 30F, 30F);
            double dist = mob.distanceTo(target);

            // durante a preparacao do tiro, para de se mover (spec: "pequena pausa")
            if (windup <= 0) {
                if (dist < 8.0D) {
                    Vector3d away = mob.position().subtract(target.position()).normalize();
                    Vector3d dest = mob.position().add(away.scale(3));
                    mob.getMoveControl().setWantedPosition(dest.x, dest.y + 1, dest.z, 1.0D);
                } else if (dist > 12.0D) {
                    mob.getMoveControl().setWantedPosition(target.getX(), target.getY() + 1, target.getZ(), 1.0D);
                } else {
                    mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0.0D);
                }
            }

            if (shootCooldown > 0) shootCooldown--;
            boolean hasLineOfSight = mob.getSensing().canSee(target);

            if (windup > 0) {
                windup--;
                mob.entityData.set(WINDUP_TICKS, windup);
                if (windup <= 0) {
                    shoot(target);
                    shootCooldown = 3 * 20;
                    mob.entityData.set(POST_FIRE_TICKS, POST_FIRE_DURATION);
                }
            } else if (hasLineOfSight && shootCooldown <= 0 && dist <= 16.0D) {
                windup = WINDUP_DURATION;
                mob.entityData.set(WINDUP_TICKS, windup);
            }
        }

        private void shoot(LivingEntity target) {
            VoidBoltEntity bolt = new VoidBoltEntity(mob.level, mob);
            double dx = target.getX() - mob.getX();
            double dy = target.getY(0.5) - bolt.getY();
            double dz = target.getZ() - mob.getZ();
            bolt.shoot(dx, dy, dz, 1.2F, 4.0F);
            mob.level.addFreshEntity(bolt);
            mob.playSound(net.minecraft.util.SoundEvents.ILLUSIONER_CAST_SPELL, 1.0F, 1.0F);
        }
    }
}
