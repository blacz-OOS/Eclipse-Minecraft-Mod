package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.server.ServerWorld;

public class GhostSummonGoal extends Goal {
    private final GhostEntity ghost;
    private int cooldown;

    public GhostSummonGoal(GhostEntity ghost) {
        this.ghost = ghost;
    }

    @Override
    public boolean canUse() {
        if (this.ghost.getTarget() == null) return false;
        int interval = this.ghost.isEclipseActive() ? 200 : 400;
        return ++this.cooldown >= interval && this.ghost.getRandom().nextFloat() < 0.25F;
    }

    @Override
    public void start() {
        this.cooldown = 0;
        if (this.ghost.level instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) this.ghost.level;
            GhostEntity minion = (GhostEntity) this.ghost.getType().create(world);
            if (minion != null) {
                minion.moveTo(this.ghost.getX() + 1, this.ghost.getY(), this.ghost.getZ() + 1, this.ghost.yRot, 0.0F);
                minion.finalizeSpawn(world, world.getCurrentDifficultyAt(minion.blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
                minion.setTarget(this.ghost.getTarget());
                world.addFreshEntity(minion);
            }
        }
    }
}
