package com.soldierskull.eclipse.item;

import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;

public class SilverSwordItem extends SwordItem implements SilverWeapon {

    public SilverSwordItem(Item.Properties properties) {
        super(EclipseItemTiers.SILVER, 3, -2.4F, properties);
    }
}
