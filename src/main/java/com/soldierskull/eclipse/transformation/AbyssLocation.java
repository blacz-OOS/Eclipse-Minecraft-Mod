package com.soldierskull.eclipse.transformation;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.soldierskull.eclipse.dimension.ModDimensions;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * "Entrar no Abismo" é uma das duas condições da transformação (regra
 * 14 do documento original: "50% de afinidade + entrada no Abismo").
 *
 * ATUALIZADO NA FASE 13: a dimensão do Abismo agora existe de verdade
 * (data/eclipse/dimension/abyss.json + dimension_type + bioma próprio),
 * referenciada em código por ModDimensions.ABYSS. isInAbyss() faz a
 * checagem real comparando a dimensão atual do jogador.
 *
 * O override manual de debug (/eclipse transform forceabyss <target>
 * <true|false>) continua funcionando - útil para testar o fluxo de
 * transformação sem precisar viajar até a dimensão real.
 */
public final class AbyssLocation {

    private static final Set<UUID> DEBUG_FORCED_IN_ABYSS = new HashSet<>();

    private AbyssLocation() {
    }

    public static boolean isInAbyss(ServerPlayerEntity player) {
        if (DEBUG_FORCED_IN_ABYSS.contains(player.getUUID())) {
            return true;
        }
        return player.level.dimension().equals(ModDimensions.ABYSS);
    }

    public static void setDebugForced(UUID playerId, boolean forced) {
        if (forced) {
            DEBUG_FORCED_IN_ABYSS.add(playerId);
        } else {
            DEBUG_FORCED_IN_ABYSS.remove(playerId);
        }
    }
}
