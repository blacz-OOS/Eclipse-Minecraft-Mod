package com.soldierskull.eclipse.entity.npc.dialogue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.soldierskull.eclipse.stats.PlayerStats;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;

/**
 * Motor mínimo de diálogo (Etapa 3 - "Right Click -> Dialogue -> Options
 * -> Actions", "a menor arquitetura extensível possível"). Não é uma
 * GUI - as opções aparecem como linhas clicáveis no chat, cada uma
 * rodando um comando escondido ({@code /eclipsenpc <npcId> <indice>})
 * quando clicada. Reaproveita um mecanismo padrão do próprio Minecraft
 * em vez de criar mais um Container/Screen.
 *
 * Cada "npcId" (ex.: "hunter", "cultist") tem um {@link MenuBuilder}
 * próprio, registrado uma vez no setup do mod. O builder roda de novo
 * toda vez - ao abrir o diálogo E a cada clique - nunca há um "menu
 * salvo": ele é sempre reconstruído a partir do estado atual do
 * jogador (quests ativas, reputação etc.). Isso evita qualquer
 * sincronização de estado entre "ver o menu" e "clicar numa opção" -
 * ao custo de que, se o jogador demorar muito entre as duas coisas, a
 * opção pode ter mudado de figura (aceitável nesta primeira versão).
 */
public final class NPCDialogue {

    @FunctionalInterface
    public interface MenuBuilder {
        List<DialogueOption> build(ServerPlayerEntity player, PlayerStats stats);
    }

    private static final Map<String, MenuBuilder> BUILDERS = new HashMap<>();
    private static final Map<String, String> TITLES = new HashMap<>();

    private NPCDialogue() {
    }

    public static void register(String npcId, String title, MenuBuilder builder) {
        BUILDERS.put(npcId, builder);
        TITLES.put(npcId, title);
    }

    /** Chamado pelo {@link NPCInteractionHandler} quando o jogador clica com botão direito no NPC. */
    public static void open(ServerPlayerEntity player, String npcId) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> sendMenu(player, npcId, stats));
    }

    /** Chamado pelo comando escondido quando o jogador clica numa opção do chat. */
    public static void handleOption(ServerPlayerEntity player, String npcId, int index) {
        player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
            MenuBuilder builder = BUILDERS.get(npcId);
            if (builder == null) {
                return;
            }
            List<DialogueOption> options = builder.build(player, stats);
            if (index < 0 || index >= options.size()) {
                return;
            }
            options.get(index).getAction().run(player, stats);
            // Reabre o menu depois da ação, pra próxima escolha não exigir clicar de novo no NPC.
            sendMenu(player, npcId, stats);
        });
    }

    private static void sendMenu(ServerPlayerEntity player, String npcId, PlayerStats stats) {
        MenuBuilder builder = BUILDERS.get(npcId);
        if (builder == null) {
            return;
        }
        List<DialogueOption> options = builder.build(player, stats);
        String title = TITLES.getOrDefault(npcId, npcId);

        player.displayClientMessage(new StringTextComponent("§6=== " + title + " ==="), false);
        for (int i = 0; i < options.size(); i++) {
            StringTextComponent line = new StringTextComponent("§b" + (i + 1) + ") " + options.get(i).getLabel());
            line.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/eclipsenpc " + npcId + " " + i)));
            player.displayClientMessage(line, false);
        }
    }
}
