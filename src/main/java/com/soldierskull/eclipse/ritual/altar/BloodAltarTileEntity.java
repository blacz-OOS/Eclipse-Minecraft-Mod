package com.soldierskull.eclipse.ritual.altar;

import java.util.EnumSet;
import java.util.Set;

import com.soldierskull.eclipse.ritual.RitualCategory;

/** Blood Altar (Bloco A/#2) - GENERAL+BLOOD+VAMPIRE, 250 de limite (Bloco B/#13). */
public class BloodAltarTileEntity extends AltarTileEntity {

    public BloodAltarTileEntity() {
        super(ModTileEntities.BLOOD_ALTAR.get());
    }

    @Override
    protected Set<RitualCategory> allowedCategories() {
        return EnumSet.of(RitualCategory.GENERAL, RitualCategory.BLOOD, RitualCategory.VAMPIRE);
    }

    @Override
    protected int ritualPowerMax() {
        return 250;
    }
}
