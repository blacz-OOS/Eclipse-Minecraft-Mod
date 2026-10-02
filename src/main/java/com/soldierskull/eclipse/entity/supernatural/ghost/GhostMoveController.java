package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class GhostMoveController extends MovementController {
    private final GhostEntity ghost;
    private int courseChangeCooldown;

    public GhostMoveController(GhostEntity ghost) {
        super(ghost);
        this.ghost = ghost;
    }

    @Override
    public void tick() {
        if (this.operation == Action.MOVE_TO) {
            if (this.courseChangeCooldown-- <= 0) {
                this.courseChangeCooldown += this.ghost.getRandom().nextInt(5) + 2;
                Vector3d targetVec = new Vector3d(this.wantedX - this.ghost.getX(), this.wantedY - this.ghost.getY(), this.wantedZ - this.ghost.getZ());
                double distance = targetVec.length();
                targetVec = targetVec.normalize();

                if (this.canReach(targetVec, MathHelper.ceil(distance))) {
                    double speed = this.speedModifier * this.ghost.getAttributeValue(net.minecraft.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
                    this.ghost.setDeltaMovement(this.ghost.getDeltaMovement().add(targetVec.scale(speed * 0.1D)));
                } else {
                    this.operation = Action.WAIT;
                }
            }
        }
    }

    private boolean canReach(Vector3d pos, int steps) {
        AxisAlignedBB bb = this.ghost.getBoundingBox();
        for (int i = 1; i < steps; ++i) {
            bb = bb.move(pos);
            if (!this.ghost.level.noCollision(this.ghost, bb)) {
                return true;
            }
        }
        return true;
    }
}
