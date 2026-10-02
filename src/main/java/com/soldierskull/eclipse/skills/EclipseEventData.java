package com.soldierskull.eclipse.skills;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

/** Estado persistente do Eclipse para uma dimensão. */
public class EclipseEventData extends WorldSavedData {

    public static final String DATA_NAME = "eclipse_event";
    private long endsAtGameTime;
    private boolean previousRaining;
    private boolean previousThundering;
    private int previousRainTime;
    private int previousThunderTime;
    private long lastNaturalCheckDay = -1L;

    public EclipseEventData() { super(DATA_NAME); }

    @Override
    public void load(CompoundNBT tag) {
        this.endsAtGameTime = tag.getLong("EndsAtGameTime");
        this.previousRaining = tag.getBoolean("PreviousRaining");
        this.previousThundering = tag.getBoolean("PreviousThundering");
        this.previousRainTime = tag.getInt("PreviousRainTime");
        this.previousThunderTime = tag.getInt("PreviousThunderTime");
        this.lastNaturalCheckDay = tag.getLong("LastNaturalCheckDay");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putLong("EndsAtGameTime", this.endsAtGameTime);
        tag.putBoolean("PreviousRaining", this.previousRaining);
        tag.putBoolean("PreviousThundering", this.previousThundering);
        tag.putInt("PreviousRainTime", this.previousRainTime);
        tag.putInt("PreviousThunderTime", this.previousThunderTime);
        tag.putLong("LastNaturalCheckDay", this.lastNaturalCheckDay);
        return tag;
    }

    public boolean isActive(long gameTime) { return this.endsAtGameTime > gameTime; }
    public long getEndsAtGameTime() { return this.endsAtGameTime; }

    public void begin(long endsAt, boolean raining, boolean thundering, int rainTime, int thunderTime) {
        this.endsAtGameTime = endsAt;
        this.previousRaining = raining;
        this.previousThundering = thundering;
        this.previousRainTime = rainTime;
        this.previousThunderTime = thunderTime;
        this.setDirty();
    }

    public void extendTo(long endsAt) {
        if (endsAt > this.endsAtGameTime) {
            this.endsAtGameTime = endsAt;
            this.setDirty();
        }
    }

    public void finish() { this.endsAtGameTime = 0L; this.setDirty(); }
    public boolean wasRaining() { return this.previousRaining; }
    public boolean wasThundering() { return this.previousThundering; }
    public int getPreviousRainTime() { return this.previousRainTime; }
    public int getPreviousThunderTime() { return this.previousThunderTime; }
    public long getLastNaturalCheckDay() { return this.lastNaturalCheckDay; }
    public void setLastNaturalCheckDay(long day) { this.lastNaturalCheckDay = day; this.setDirty(); }
}
