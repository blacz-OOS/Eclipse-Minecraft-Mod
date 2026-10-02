package com.soldierskull.eclipse.ritual.altar;

import java.util.EnumSet;
import java.util.Set;

import com.soldierskull.eclipse.block.ModBlocks;
import com.soldierskull.eclipse.ritual.Ritual;
import com.soldierskull.eclipse.ritual.RitualCategory;
import com.soldierskull.eclipse.ritual.RitualCircleManager;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Moon Altar (Bloco A/#3) - GENERAL+LUNAR+WEREWOLF, 250 de limite. Não
 * usa chalk (Bloco A/C/#17, decisão confirmada) - o círculo é o
 * próprio anel de pedra da estrutura (bloco {@code moon_altar_stone}).
 */
public class MoonAltarTileEntity extends AltarTileEntity {

    public MoonAltarTileEntity() {
        super(ModTileEntities.MOON_ALTAR.get());
    }

    @Override
    protected Set<RitualCategory> allowedCategories() {
        return EnumSet.of(RitualCategory.GENERAL, RitualCategory.LUNAR, RitualCategory.WEREWOLF);
    }

    @Override
    protected int ritualPowerMax() {
        return 250;
    }

    @Override
    public boolean isCircleValid(World world, BlockPos pos, Ritual ritual) {
        return RitualCircleManager.isNaturalRingValid(world, pos, ritual.getCircleSize(), ModBlocks.MOON_ALTAR_STONE.get());
    }
}
