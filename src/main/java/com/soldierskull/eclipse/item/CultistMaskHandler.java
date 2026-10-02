package com.soldierskull.eclipse.item;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;

import com.soldierskull.eclipse.stats.PlayerStats;

/**
 * A Máscara do Culto some/aparece livremente (o jogador tira e põe a
 * qualquer momento), diferente de uma skill desbloqueada permanentemente
 * - por isso o bônus de +15 no máximo de Corrupção precisa ser
 * checado continuamente (1x/segundo, mesmo tick do resto do sistema),
 * não só uma vez ao craftar/pegar o item.
 *
 * `stats.hasCultistMaskBonus()` guarda se o bônus JÁ FOI aplicado, pra
 * saber quando precisa remover (se não guardasse isso, tirar a máscara
 * duas vezes seguidas removeria 15 de novo por engano).
 */
public final class CultistMaskHandler {

    private CultistMaskHandler() {
    }

    /**
     * Chamado 1x/segundo por jogador (ver ServerEvents.onPlayerTick).
     *
     * BUGFIX: antes retornava void, então quando o bônus era
     * aplicado/removido aqui, o servidor atualizava o valor correto
     * internamente, mas ServerEvents.onPlayerTick só chama syncTo()
     * quando a variável local "changed" é true - e "changed" nunca
     * considerava essa mudança, só HP regen e Common Energy regen.
     * Resultado: o cliente só via o novo total de Corrupção quando
     * ALGUM OUTRO sistema (regen de vida/energia) coincidentemente
     * disparava um sync por perto - dando a falsa impressão de que
     * abrir a tela de status era a "condição" pra atualizar (na
     * verdade a tela só mostra o que já está em cache no cliente,
     * sem pedir sync nenhum ao abrir).
     *
     * Agora retorna true quando o bônus muda de estado, pra
     * ServerEvents.java poder incluir isso no "changed" e sincronizar
     * o cliente na hora certa.
     */
    public static boolean tick(ServerPlayerEntity player, PlayerStats stats) {
        boolean wearing = player.getItemBySlot(EquipmentSlotType.HEAD).getItem() instanceof CultistMaskItem;

        if (wearing && !stats.hasCultistMaskBonus()) {
            stats.addCorruptionMax(15);
            stats.setCultistMaskBonus(true);
            return true;
        } else if (!wearing && stats.hasCultistMaskBonus()) {
            stats.addCorruptionMax(-15);
            stats.setCultistMaskBonus(false);
            return true;
        }
        return false;
    }
}
