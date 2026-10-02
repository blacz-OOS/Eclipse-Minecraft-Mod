package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class GhostTeleportGoal extends Goal {
    private final GhostEntity ghost;
    private int cooldown;

    public GhostTeleportGoal(GhostEntity ghost) {
        this.ghost = ghost;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.ghost.getTarget();
        if (target == null || !target.isAlive()) return false;
        
        int baseCooldown = this.ghost.isEclipseActive() ? 60 : 120;
        return ++this.cooldown >= baseCooldown && this.ghost.distanceToSqr(target) > 16.0D;
    }

    @Override
    public void start() {
        this.cooldown = 0;
        LivingEntity target = this.ghost.getTarget();
        if (target != null) {
            double posX = target.getX() + (this.ghost.getRandom().nextDouble() - 0.5D) * 4.0D;
            double posY = target.getY() + 1.0D;
            double posZ = target.getZ() + (this.ghost.getRandom().nextDouble() - 0.5D) * 4.0D;
            
            this.ghost.teleportTo(posX, posY, posZ);
            this.ghost.level.broadcastEntityEvent(this.ghost, (byte) 46);
        }
    }
}
