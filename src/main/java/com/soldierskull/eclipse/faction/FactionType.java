package com.soldierskull.eclipse.faction;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;


public enum FactionType {

    NONE,
    HUNTERS,
    CULTISTS;

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("faction.eclipse." + this.name().toLowerCase());
    }
}