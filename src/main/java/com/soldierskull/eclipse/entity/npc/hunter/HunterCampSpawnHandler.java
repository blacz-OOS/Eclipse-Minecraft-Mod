package com.soldierskull.eclipse.entity.npc.hunter;

import java.util.HashSet;
import java.util.Set;

import com.soldierskull.eclipse.entity.npc.ModNPCEntities;
import com.soldierskull.eclipse.structures.ModStructures;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Etapa 2 (Ecossistema Hunter) - o Hunter Camp agora gera de verdade no
 * mundo ({@link com.soldierskull.eclipse.structures.HunterCampStructure}),
 * mas a peça .nbt sozinha não coloca nenhum HunterNPCEntity lá dentro
 * (o sistema de piece placement do Forge 1.16.5 não tem um hook simples
 * pra "spawnar entidade X ao colocar esta peça" sem escrever uma
 * StructurePiece inteira do zero). Em vez disso, verificamos
 * periodicamente se o jogador está dentro de um Hunter Camp gerado e,
 * se ainda não tem NPC lá, spawnamos um - mesmo padrão de
 * {@code MapinguariSpawnHandler} (checagem por tick de jogador, throttled).
 *
 * Rastreamento "já spawnado aqui" é só em memória (não persiste a um
 * restart do servidor) - na pior das hipóteses, um Hunter Camp que já
 * teve seu NPC morto pode ganhar outro depois de reiniciar o servidor,
 * o que é aceitável (até desejável, como "reforço chegou") pra esta
 * primeira versão.
 */
@Mod.EventBusSubscriber(modid = "eclipse")
public final class HunterCampSpawnHandler {

    private static final int CHECK_INTERVAL_TICKS = 100; // 5s
    private static final Set<BlockPos> CAMPS_WITH_NPC = new HashSet<>();

    private HunterCampSpawnHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.player.level.isClientSide) {
            return;
        }
        if (event.player.tickCount % CHECK_INTERVAL_TICKS != 0) {
            return;
        }

        ServerWorld world = (ServerWorld) event.player.level;
        BlockPos playerPos = event.player.blockPosition();

        StructureStart<?> start = world.structureFeatureManager()
                .getStructureAt(playerPos, false, ModStructures.HUNTER_CAMP.get());
        if (start == null || !start.isValid()) {
            return;
        }

        MutableBoundingBox box = start.getBoundingBox();
        BlockPos campKey = new BlockPos(box.x0, box.y0, box.z0);
        if (CAMPS_WITH_NPC.contains(campKey)) {
            return;
        }

        AxisAlignedBB campArea = new AxisAlignedBB(box.x0, box.y0, box.z0, box.x1, box.y1, box.z1);
        boolean alreadyHasNpc = !world.getEntitiesOfClass(HunterNPCEntity.class, campArea).isEmpty();
        if (alreadyHasNpc) {
            CAMPS_WITH_NPC.add(campKey);
            return;
        }

        EntityType<HunterNPCEntity> type = ModNPCEntities.HUNTER_NPC.get();
        HunterNPCEntity npc = type.create(world);
        if (npc == null) {
            return;
        }

        int centerX = (box.x0 + box.x1) / 2;
        int centerZ = (box.z0 + box.z1) / 2;
        int surfaceY = world.getHeight(net.minecraft.world.gen.Heightmap.Type.WORLD_SURFACE_WG, centerX, centerZ);
        npc.moveTo(centerX + 0.5D, surfaceY, centerZ + 0.5D, 0.0F, 0.0F);
        npc.finalizeSpawn(world, world.getCurrentDifficultyAt(npc.blockPosition()),
                net.minecraft.entity.SpawnReason.STRUCTURE, null, null);
        world.addFreshEntity(npc);

        CAMPS_WITH_NPC.add(campKey);
    }
}
