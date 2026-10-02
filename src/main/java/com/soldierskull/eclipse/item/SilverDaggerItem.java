package com.soldierskull.eclipse.item;

import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;

/**
 * Variante mais rápida e mais fraca da Espada de Prata (menos dano base,
 * ataque mais veloz) - sem valores exatos definidos no documento;
 * PLACEHOLDER: -1 de dano contra a espada, +0.8 de velocidade de ataque.
 */
public class SilverDaggerItem extends SwordItem implements SilverWeapon {

    public SilverDaggerItem(Item.Properties properties) {
        super(EclipseItemTiers.SILVER, 2, -1.6F, properties);
    }
}
