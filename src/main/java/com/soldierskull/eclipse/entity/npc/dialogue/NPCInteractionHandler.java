package com.soldierskull.eclipse.entity.npc.dialogue;

import com.soldierskull.eclipse.entity.npc.cultist.CultistDialogue;
import com.soldierskull.eclipse.entity.npc.cultist.CultistNPCEntity;
import com.soldierskull.eclipse.entity.npc.hunter.HunterDialogue;
import com.soldierskull.eclipse.entity.npc.hunter.HunterNPCEntity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Etapa 3 - abre o diálogo do NPC no clique direito. Usa o evento do
 * Forge ({@code PlayerInteractEvent.EntityInteract}) em vez de
 * sobrescrever {@code mobInteract()} nas entidades - reportado que o
 * override não disparava (motivo não diagnosticado sem conseguir
 * compilar/testar), então troquei pelo caminho mais usado/testado do
 * Forge pra interação com entidade, que é independente de qualquer
 * lógica de IA/goals da entidade.
 *
 * BUGFIX (diálogo duplicado): mesmo com o filtro de MAIN_HAND abaixo
 * (que já evita o disparo óbvio "uma vez por mão"), o jogo reportou o
 * diálogo abrindo 2x pra um único clique. PlayerInteractEvent.EntityInteract
 * é um evento com histórico conhecido de disparar mais de uma vez pro
 * mesmo clique em certas circunstâncias (nem sempre reproduzível fora
 * do jogo rodando de verdade). Em vez de depender de identificar a
 * causa exata do disparo duplo, adicionamos uma trava de debounce: se
 * o MESMO jogador interagir com o MESMO NPC de novo dentro de uma
 * janela curta (10 ticks = 0.5s), a segunda chamada é ignorada. Isso
 * resolve o sintoma garantidamente, seja qual for a causa raiz do
 * disparo duplo, sem impedir cliques legítimos e intencionais (que
 * naturalmente ficam bem mais espaçados que 0.5s).
 */
@Mod.EventBusSubscriber(modid = "eclipse")
public final class NPCInteractionHandler {

    private NPCInteractionHandler() {
    }

    private static final long DEBOUNCE_TICKS = 10L; // 0.5s a 20 ticks/s
    private static final java.util.Map<String, Long> LAST_INTERACT_TICK = new java.util.HashMap<>();

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != Hand.MAIN_HAND) {
            return; // evita abrir o dialogo 2x (o evento dispara pra cada mao)
        }
        if (event.getWorld().isClientSide) {
            return;
        }
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) {
            return;
        }
        if (!(event.getTarget() instanceof HunterNPCEntity) && !(event.getTarget() instanceof CultistNPCEntity)) {
            return;
        }

        // Chave = jogador + entidade especifica (nao so o tipo), pra nao
        // bloquear por engano interagir com dois NPCs diferentes em
        // sequencia rapida - so protege contra o MESMO par se repetindo.
        String key = event.getPlayer().getUUID() + ":" + event.getTarget().getId();
        long now = event.getWorld().getGameTime();
        Long last = LAST_INTERACT_TICK.get(key);
        if (last != null && now - last < DEBOUNCE_TICKS) {
            event.setCanceled(true);
            return;
        }
        LAST_INTERACT_TICK.put(key, now);

        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

        if (event.getTarget() instanceof HunterNPCEntity) {
            NPCDialogue.open(player, HunterDialogue.NPC_ID);
            event.setCanceled(true);
        } else if (event.getTarget() instanceof CultistNPCEntity) {
            NPCDialogue.open(player, CultistDialogue.NPC_ID);
            event.setCanceled(true);
        }
    }
}