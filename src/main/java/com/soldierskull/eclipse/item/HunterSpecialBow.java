package com.soldierskull.eclipse.item;

import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;

/**
 * "Munição Especial" (Caçadores, nível 9): arco com 5 de durabilidade
 * que atira flechas marcadas - CombatEvents (ver
 * skills/ProjectileEvents.java) dá +3 de dano quando essa flecha acerta
 * uma criatura sobrenatural. Concedido ao inventário do jogador quando
 * a habilidade é desbloqueada (ver HunterSkillEffects.MUNICAO_ESPECIAL);
 * uma futura GUI de habilidades pode reconceder via o mesmo método caso
 * o item se perca.
 *
 * Simplificação: usa a flecha vanilla (AbstractArrowEntity) marcada via
 * NBT persistente em vez de um novo tipo de flecha/entidade - evita
 * precisar registrar um EntityType novo só pra isso.
 */
public class HunterSpecialBow extends BowItem {

    public HunterSpecialBow(Item.Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrowEntity customArrow(AbstractArrowEntity arrow) {
        arrow.getPersistentData().putBoolean("eclipse_hunter_special_arrow", true);
        return arrow;
    }
}
