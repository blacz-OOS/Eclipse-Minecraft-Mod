package com.soldierskull.eclipse.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;

/**
 * Remove entidades invocadas por rituais (Ritual de Invocação, Invocação
 * Superior) depois do tempo de vida dito na especificação. Checado no
 * mesmo tick de 1s do BuffManager (ver ServerEvents.onPlayerTick /
 * SkillTickEvents) - não precisa de precisão de tick a tick.
 */
public final class SummonRegistry {

    private static final WeakHashMap<Entity, Long> SCHEDULED = new WeakHashMap<>();

    private SummonRegistry() {
    }

    public static void schedule(Entity entity, long removeAtEpochMillis) {
        SCHEDULED.put(entity, removeAtEpochMillis);
    }

    /** Chamado periodicamente (uma vez por segundo é suficiente) para remover invocações vencidas. */
    public static void tick() {
        if (SCHEDULED.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        List<Entity> expired = new ArrayList<>();
        for (java.util.Map.Entry<Entity, Long> entry : SCHEDULED.entrySet()) {
            if (now >= entry.getValue()) {
                expired.add(entry.getKey());
            }
        }
        for (Entity entity : expired) {
            if (entity.isAlive()) {
                entity.remove(false);
            }
            SCHEDULED.remove(entity);
        }
    }
}
