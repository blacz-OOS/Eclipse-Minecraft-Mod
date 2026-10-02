package com.soldierskull.eclipse.stats;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;


public enum AffinityType {

    VAMPIRIC,
    LUPINE;

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("affinity.eclipse." + this.name().toLowerCase());
    }
}