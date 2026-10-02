package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Random;

public class RandomFlyGoal extends Goal {
    private final GhostEntity ghost;

    public RandomFlyGoal(GhostEntity ghost) {
        this.ghost = ghost;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        MovementController moveControl = this.ghost.getMoveControl();
        if (!moveControl.hasWanted()) {
            return true;
        } else {
            double dx = moveControl.getWantedX() - this.ghost.getX();
            double dy = moveControl.getWantedY() - this.ghost.getY();
            double dz = moveControl.getWantedZ() - this.ghost.getZ();
            double distanceSqr = dx * dx + dy * dy + dz * dz;
            return distanceSqr < 1.0D || distanceSqr > 3600.0D;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        Random random = this.ghost.getRandom();
        double targetX = this.ghost.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
        double targetY = this.ghost.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 6.0F);
        double targetZ = this.ghost.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);

        this.ghost.getMoveControl().setWantedPosition(targetX, targetY, targetZ, 1.0D);
    }
}