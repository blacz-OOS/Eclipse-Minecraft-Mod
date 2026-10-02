package com.soldierskull.eclipse.entity.npc.dialogue;

import com.soldierskull.eclipse.stats.PlayerStats;

import net.minecraft.entity.player.ServerPlayerEntity;

/** Ação executada quando o jogador clica numa opção de diálogo (Etapa 3). */
@FunctionalInterface
public interface DialogueAction {

    void run(ServerPlayerEntity player, PlayerStats stats);
}
