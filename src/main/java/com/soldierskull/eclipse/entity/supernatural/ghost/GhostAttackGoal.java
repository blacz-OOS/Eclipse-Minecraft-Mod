package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class GhostAttackGoal extends Goal {
    private final GhostEntity ghost;
    private int attackTimer;

    public GhostAttackGoal(GhostEntity ghost) {
        this.ghost = ghost;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.ghost.getTarget();
        if (target != null && target.isAlive()) {
            return this.ghost.distanceToSqr(target) <= 16.0D;
        }
        return false;
    }

    @Override
    public void start() {
        this.attackTimer = 0;
        this.ghost.setChasing(true);
    }

    @Override
    public void stop() {
        this.ghost.setChasing(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.ghost.getTarget();
        if (target != null) {
            this.ghost.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double distanceSqr = this.ghost.distanceToSqr(target);

            this.attackTimer = Math.max(this.attackTimer - 1, 0);

            if (distanceSqr <= 4.0D && this.attackTimer <= 0) {
                this.attackTimer = 20; // Ataca a cada 1 segundo (20 ticks)
                this.ghost.swing(Hand.MAIN_HAND);
                this.ghost.doHurtTarget(target);
            }
        }
    }
}