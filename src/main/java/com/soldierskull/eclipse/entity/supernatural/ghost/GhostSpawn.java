package com.soldierskull.eclipse.entity.supernatural.ghost;

import com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.Heightmap;

import java.util.Random;

public class GhostSpawn {

    @SuppressWarnings("unchecked")
    public static boolean checkGhostSpawnRules(EntityType<GhostEntity> type, IServerWorld world, SpawnReason reason, BlockPos pos, Random random) {
        boolean isNightOrStorm = world.getLevel().isNight() || world.getLevel().isThundering();

        // Faz o cast do EntityType para permitir o uso em checkMonsterSpawnRules
        EntityType<MonsterEntity> monsterType = (EntityType<MonsterEntity>) (EntityType<?>) type;

        return isNightOrStorm && MonsterEntity.checkMonsterSpawnRules(monsterType, world, reason, pos, random);
    }

    public static void registerSpawns() {
        EntitySpawnPlacementRegistry.register(
                ModSupernaturalEntities.GHOST.get(),
                EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                GhostSpawn::checkGhostSpawnRules
        );
    }
}