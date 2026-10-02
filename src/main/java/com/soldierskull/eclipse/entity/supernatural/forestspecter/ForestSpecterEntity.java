package com.soldierskull.eclipse.entity.supernatural.forestspecter;

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
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

/**
 * Espectro da Floresta (item 16.5). Flutua lentamente, ataque de impulso
 * sonoro em area (raio 3, dano 4, knockback ~5 blocos, cooldown 7s) -
 * sem ataque corpo a corpo convencional.
 *
 * ATUALIZADO (animacao): o pulso era instantaneo ao chegar perto. A
 * spec (11.4 SCREAM PREPARATION) pede uma preparacao visivel antes do
 * grito - adicionei essa janela, sincronizada via SynchedEntityData.
 */
public class ForestSpecterEntity extends MonsterEntity implements ISupernaturalMob {

    private static final int WINDUP_DURATION = 10; // ~0.5s de preparacao (corpo para, braços abrem)

    private static final DataParameter<Integer> WINDUP_TICKS =
            EntityDataManager.defineId(ForestSpecterEntity.class, DataSerializers.INT);

    public ForestSpecterEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.moveControl = new FlyingMovementController(this, 5, true);
        this.xpReward = 5;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WINDUP_TICKS, 0);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.FLYING_SPEED, 0.25D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SonicPulseGoal(this));
        this.targetSelector.addGoal(1, new net.minecraft.entity.ai.goal.NearestAttackableTargetGoal<>(
                this, PlayerEntity.class, true));
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.OVERWORLD_SUPERNATURAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.COMMON;
    }

    // ---------- Consultado pelo ForestSpecterModel (cliente) ----------

    public boolean isWindingUp() {
        return this.entityData.get(WINDUP_TICKS) > 0;
    }

    public float getWindupProgress() {
        return 1.0F - (this.entityData.get(WINDUP_TICKS) / (float) WINDUP_DURATION);
    }

    public int getDeathTime() {
        return this.deathTime;
    }

    static class SonicPulseGoal extends Goal {
        private final ForestSpecterEntity mob;
        private int cooldown = 0;
        private int windup = 0;

        SonicPulseGoal(ForestSpecterEntity mob) {
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

            if (windup <= 0) {
                mob.getMoveControl().setWantedPosition(target.getX(), target.getY() + 1, target.getZ(), 0.6D);
            } // durante o windup - "corpo para" (spec 11.4), nao se move

            if (cooldown > 0) {
                cooldown--;
            }

            if (windup > 0) {
                windup--;
                mob.entityData.set(WINDUP_TICKS, windup);
                if (windup <= 0) {
                    pulse();
                    cooldown = 7 * 20;
                }
            } else if (cooldown <= 0 && mob.distanceTo(target) <= 3.5D) {
                windup = WINDUP_DURATION;
                mob.entityData.set(WINDUP_TICKS, windup);
            }
        }

        private void pulse() {
            mob.playSound(SoundEvents.GENERIC_EXPLODE, 0.6F, 1.5F);
            List<LivingEntity> hit = mob.level.getEntitiesOfClass(LivingEntity.class,
                    mob.getBoundingBox().inflate(3.0D));
            for (LivingEntity entity : hit) {
                if (entity == mob) continue;
                entity.hurt(DamageSource.mobAttack(mob), 4.0F);
                double dx = entity.getX() - mob.getX();
                double dz = entity.getZ() - mob.getZ();
                entity.knockback(1.6F, -dx, -dz); // ~5 blocos em condicoes normais
            }
        }
    }
}
