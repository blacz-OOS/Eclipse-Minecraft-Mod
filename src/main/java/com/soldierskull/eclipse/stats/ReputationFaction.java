package com.soldierskull.eclipse.stats; // (ou o package correto da sua classe)

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum ReputationFaction {

    HUNTERS,
    CULTISTS,
    VAMPIRES,
    WEREWOLFS;

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("reputation.eclipse." + this.name().toLowerCase());
    }
}