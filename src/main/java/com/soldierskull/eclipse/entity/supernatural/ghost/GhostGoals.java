package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;

public class GhostGoals {

    public static void registerGoals(GhostEntity ghost) {
        ghost.goalSelector.addGoal(1, new GhostAttackGoal(ghost));
        ghost.goalSelector.addGoal(2, new GhostTeleportGoal(ghost));
        ghost.goalSelector.addGoal(3, new GhostSummonGoal(ghost));
        ghost.goalSelector.addGoal(4, new GhostInvisibilityGoal(ghost));
        ghost.goalSelector.addGoal(5, new RandomFlyGoal(ghost));
        ghost.goalSelector.addGoal(6, new LookAtGoal(ghost, PlayerEntity.class, 8.0F));
        ghost.goalSelector.addGoal(7, new LookRandomlyGoal(ghost));

        ghost.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(ghost, LivingEntity.class, 10, true, false,
                entity -> (entity instanceof PlayerEntity || isHostileFaction(entity)) && !isCultist(entity)));
    }

    private static boolean isHostileFaction(LivingEntity entity) {
        // Lógica de facção inimiga (se houver)
        return false;
    }

    private static boolean isCultist(LivingEntity entity) {
        // Lógica para ignorar cultistas (se houver)
        return false;
    }
}