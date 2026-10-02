package com.soldierskull.eclipse.entity.supernatural;

import net.minecraft.entity.Entity;

/**
 * Utilitario pra qualquer sistema (ex.: QuestObjectiveType do sistema de
 * missoes, item 9/55 da Fase 14) consultar a categoria SUPERNATURAL sem
 * precisar de uma lista manual de entidades. Quando o sistema de missoes
 * Hunter adicionar um objetivo do tipo "matar N criaturas sobrenaturais",
 * ele so precisa chamar SupernaturalUtil.isSupernatural(deadEntity).
 */
public final class SupernaturalUtil {

    private SupernaturalUtil() {
    }

    public static boolean isSupernatural(Entity entity) {
        return entity instanceof ISupernaturalMob;
    }

    public static boolean isOrigin(Entity entity, SupernaturalOrigin origin) {
        return entity instanceof ISupernaturalMob
                && ((ISupernaturalMob) entity).getSupernaturalOrigin() == origin;
    }

    public static boolean isRank(Entity entity, SupernaturalRank rank) {
        return entity instanceof ISupernaturalMob
                && ((ISupernaturalMob) entity).getSupernaturalRank() == rank;
    }
}
