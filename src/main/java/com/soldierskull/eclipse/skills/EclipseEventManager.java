package com.soldierskull.eclipse.skills;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

/** Evento mundial persistente do Eclipse, com noite e tempestade temporárias. */
public final class EclipseEventManager {

    private static final int NIGHT_START = 13000;
    private static final int NIGHT_END = 23000;
    private static final int NATURAL_ECLIPSE_CHANCE = 28;
    private static final long NATURAL_ECLIPSE_DURATION_MILLIS = 6L * 60L * 1000L;

    private EclipseEventManager() { }

    public static void start(ServerWorld world, long durationMillis) {
        long durationTicks = Math.max(20L, (durationMillis + 49L) / 50L);
        long endsAt = world.getGameTime() + durationTicks;
        EclipseEventData data = getData(world);
        if (data.isActive(world.getGameTime())) {
            data.extendTo(endsAt);
            enforceEclipseWeather(world, data.getEndsAtGameTime() - world.getGameTime());
            return;
        }
        data.begin(endsAt, world.isRaining(), world.isThundering(), 0, 0);
        moveToNight(world);
        enforceEclipseWeather(world, durationTicks);
        broadcast(world, "§5§lO ECLIPSE COMEÇOU§r §7— a noite caiu sobre este mundo.");
    }

    public static boolean isActive(ServerWorld world) {
        return getData(world).isActive(world.getGameTime());
    }

    /** Chamado uma vez por tick de servidor, para cada dimensão carregada. */
    public static void tick(ServerWorld world) {
        EclipseEventData data = getData(world);
        tryStartNaturalEclipse(world, data);
        if (!data.isActive(world.getGameTime())) {
            if (data.getEndsAtGameTime() != 0L) {
                world.setWeatherParameters(0, data.getPreviousRainTime(), data.wasRaining(), data.wasThundering());
                data.finish();
                broadcast(world, "§6O Eclipse terminou. §7A luz volta a este mundo.");
            }
            return;
        }
        moveToNight(world);
        enforceEclipseWeather(world, data.getEndsAtGameTime() - world.getGameTime());
    }

    /** Uma vez por noite há 1/28 de chance de um Eclipse natural, por dimensão. */
    private static void tryStartNaturalEclipse(ServerWorld world, EclipseEventData data) {
        if (data.isActive(world.getGameTime())) return;
        long timeOfDay = world.getDayTime() % 24000L;
        long day = world.getDayTime() / 24000L;
        if (timeOfDay >= NIGHT_START && data.getLastNaturalCheckDay() != day) {
            data.setLastNaturalCheckDay(day);
            if (world.random.nextInt(NATURAL_ECLIPSE_CHANCE) == 0) start(world, NATURAL_ECLIPSE_DURATION_MILLIS);
        }
    }

    private static EclipseEventData getData(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(EclipseEventData::new, EclipseEventData.DATA_NAME);
    }

    private static void moveToNight(ServerWorld world) {
        long dayTime = world.getDayTime();
        long timeOfDay = dayTime % 24000L;
        if (timeOfDay < NIGHT_START || timeOfDay > NIGHT_END) {
            world.setDayTime(dayTime - timeOfDay + NIGHT_START);
        }
    }

    private static void enforceEclipseWeather(ServerWorld world, long remainingTicks) {
        int weatherTicks = (int) Math.min(Integer.MAX_VALUE, Math.max(1L, remainingTicks + 20L));
        world.setWeatherParameters(0, weatherTicks, true, true);
    }

    private static void broadcast(ServerWorld world, String message) {
        for (ServerPlayerEntity player : world.players()) {
            player.sendMessage(new StringTextComponent(message), player.getUUID());
        }
    }

    /** O estado agora pertence ao próprio mundo e é descartado com ele. */
    public static void forgetWorld(ServerWorld world) { }

    /** Não há cache estático a limpar. */
    public static void clearAll() { }
}
