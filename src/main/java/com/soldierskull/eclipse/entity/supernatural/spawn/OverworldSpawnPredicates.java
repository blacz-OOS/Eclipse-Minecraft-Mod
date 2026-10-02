package com.soldierskull.eclipse.entity.supernatural.spawn;

import com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities;
import com.soldierskull.eclipse.skills.EclipseEventManager;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Condicoes de spawn (secao 31-34). Estrategia: registra os placement
 * types padrao (ON_GROUND, altura Heightmap.MOTION_BLOCKING_NO_LEAVES,
 * regra de spawn de monstro comum) e usa CheckSpawn (LivingSpawnEvent) pra
 * aplicar as condicoes especificas de horario/Eclipse/lua cheia de cada
 * mob, ja que sao muito diferentes entre si pra caber num unico predicado
 * generico do EntitySpawnPlacementRegistry.
 */
public class OverworldSpawnPredicates {

    public static void setup() {
        register(ModSupernaturalEntities.DRY_BODY.get());
        register(ModSupernaturalEntities.MIST_GHOULIN.get());
        register(ModSupernaturalEntities.CORRUPTED_SACI.get());
        register(ModSupernaturalEntities.SULFUR_HOUND.get());
        register(ModSupernaturalEntities.FOREST_SPECTER.get());
        register(ModSupernaturalEntities.WENDIGO.get());
    }

    private static void register(EntityType<? extends net.minecraft.entity.MobEntity> type) {
        EntitySpawnPlacementRegistry.register(type,
                EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
                net.minecraft.world.gen.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                OverworldSpawnPredicates::canSpawn);
    }

    private static boolean canSpawn(EntityType<? extends net.minecraft.entity.MobEntity> type,
                                     net.minecraft.world.IServerWorld world,
                                     net.minecraft.entity.SpawnReason reason,
                                     net.minecraft.util.math.BlockPos pos,
                                     java.util.Random random) {
        if (!net.minecraft.entity.monster.MonsterEntity.isDarkEnoughToSpawn(world, pos, random)) {
            // luz baixa e a regra padrao vanilla; abaixo tratamos as excecoes por mob
        }
        boolean eclipseActive = world instanceof ServerWorld && EclipseEventManager.isActive((ServerWorld) world);
        long dayTime = world.getLevel().getDayTime() % 24000L;
        boolean isNight = dayTime >= 13000L && dayTime <= 23000L;

        if (type == ModSupernaturalEntities.DRY_BODY.get()) {
            // sem restricao de horario (so bioma, ja filtrado no BiomeLoadingEvent);
            // o +50% de frequencia do Eclipse (secao 31) e aplicado como chance extra,
            // ja que nao ha uma condicao generica (como "noite") pra simplesmente ignorar aqui.
            float chance = eclipseActive ? 0.9F : 0.6F; // 0.9 / 0.6 = 1.5x
            return random.nextFloat() < chance;
        }
        if (type == ModSupernaturalEntities.CORRUPTED_SACI.get()) {
            float chance = eclipseActive ? 0.9F : 0.6F;
            return random.nextFloat() < chance;
        }
        if (type == ModSupernaturalEntities.MIST_GHOULIN.get() || type == ModSupernaturalEntities.SULFUR_HOUND.get()
                || type == ModSupernaturalEntities.FOREST_SPECTER.get()) {
            // normalmente noite; Eclipse ignora essa condicao generica (secao 31/32)
            return isNight || eclipseActive;
        }
        if (type == ModSupernaturalEntities.WENDIGO.get()) {
            // bioma ja filtrado (obrigatorio, item 25/26). Horario normal = noite;
            // Eclipse ignora o horario (condicao generica). Lua cheia aumenta a CHANCE,
            // nao e um requisito - tratada como bonus probabilistico abaixo.
            if (!isNight && !eclipseActive) return false;
            float chance = isFullMoon(world) ? 0.20F : 0.08F; // raro; lua cheia aumenta a chance
            if (eclipseActive) chance *= 1.5F; // +50% de frequencia durante o Eclipse (secao 24/31)
            return random.nextFloat() < chance;
        }
        return true;
    }

    private static boolean isFullMoon(net.minecraft.world.IServerWorld world) {
        return world.getLevel().getMoonPhase() == 0; // fase 0 = lua cheia no Minecraft
    }
}
