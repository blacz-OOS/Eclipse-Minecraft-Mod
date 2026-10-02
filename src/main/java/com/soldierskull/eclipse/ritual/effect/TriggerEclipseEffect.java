package com.soldierskull.eclipse.ritual.effect;

import com.soldierskull.eclipse.ritual.RitualContext;
import com.soldierskull.eclipse.ritual.RitualEffect;
import com.soldierskull.eclipse.skills.EclipseEventManager;

/**
 * Dispara o evento Eclipse manualmente (Bloco D/#31 - ritual exclusivo
 * prioritário do Abyss Altar). Reaproveita o {@code EclipseEventManager}
 * já existente - não duplica lógica de evento.
 */
public class TriggerEclipseEffect implements RitualEffect {

    private final long durationMillis;

    public TriggerEclipseEffect(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    @Override
    public void apply(RitualContext context) {
        EclipseEventManager.start(context.getWorld(), this.durationMillis);
    }
}
