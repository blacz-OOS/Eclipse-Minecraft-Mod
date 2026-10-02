package com.soldierskull.eclipse.stats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerStatsProvider implements ICapabilitySerializable<CompoundNBT> {

    @CapabilityInject(PlayerStats.class)
    public static Capability<PlayerStats> PLAYER_STATS_CAP = null;

    private final PlayerStats stats = new PlayerStats();
    private final LazyOptional<PlayerStats> instance = LazyOptional.of(() -> this.stats);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == PLAYER_STATS_CAP ? this.instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        this.stats.writeToNbt(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.stats.loadFromNbt(nbt);
    }
}
