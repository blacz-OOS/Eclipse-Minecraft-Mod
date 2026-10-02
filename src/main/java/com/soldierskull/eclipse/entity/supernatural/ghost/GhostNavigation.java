package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GhostNavigation extends FlyingPathNavigator {
    public GhostNavigation(MobEntity mob, World world) {
        super(mob, world);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return true;
    }
}
