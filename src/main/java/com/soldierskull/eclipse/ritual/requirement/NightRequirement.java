package com.soldierskull.eclipse.ritual.requirement;

import com.soldierskull.eclipse.ritual.RitualContext;
import com.soldierskull.eclipse.ritual.RitualRequirement;

/**
 * Exige que esteja de noite. Usa a mesma janela (13000-23000) já
 * definida em {@code EclipseEventManager}, pra "noite" significar a
 * mesma coisa em todo o mod.
 */
public class NightRequirement implements RitualRequirement {

    private static final int NIGHT_START = 13000;
    private static final int NIGHT_END = 23000;

    @Override
    public boolean test(RitualContext context) {
        long timeOfDay = context.getWorld().getDayTime() % 24000L;
        return timeOfDay >= NIGHT_START && timeOfDay <= NIGHT_END;
    }

    @Override
    public String describe() {
        return "Precisa ser noite";
    }
}
