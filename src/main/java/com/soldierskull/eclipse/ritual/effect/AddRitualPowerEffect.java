package com.soldierskull.eclipse.ritual.effect;

import com.soldierskull.eclipse.ritual.RitualContext;
import com.soldierskull.eclipse.ritual.RitualEffect;

/** Adiciona Ritual Power ao próprio altar que executou o ritual (respeitando o limite - ver AltarTileEntity#addRitualPower). */
public class AddRitualPowerEffect implements RitualEffect {

    private final int amount;

    public AddRitualPowerEffect(int amount) {
        this.amount = amount;
    }

    @Override
    public void apply(RitualContext context) {
        context.getAltar().addRitualPower(this.amount);
    }
}
