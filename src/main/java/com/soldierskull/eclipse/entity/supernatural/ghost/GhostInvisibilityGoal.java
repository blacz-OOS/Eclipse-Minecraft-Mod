package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.entity.ai.goal.Goal;

public class GhostInvisibilityGoal extends Goal {
    private final GhostEntity ghost;
    private int timer;
    private int duration;

    public GhostInvisibilityGoal(GhostEntity ghost) {
        this.ghost = ghost;
    }

    @Override
    public boolean canUse() {
        return !this.ghost.isGhostInvisible() && this.ghost.getRandom().nextInt(100) < 5;
    }

    @Override
    public void start() {
        this.duration = 100 + this.ghost.getRandom().nextInt(101);
        this.timer = 0;
        this.ghost.setGhostInvisible(true);
    }

    @Override
    public void tick() {
        this.timer++;
        if (this.timer >= this.duration) {
            this.ghost.setGhostInvisible(false);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.ghost.isGhostInvisible() && this.timer < this.duration;
    }

    @Override
    public void stop() {
        this.ghost.setGhostInvisible(false);
    }
}
