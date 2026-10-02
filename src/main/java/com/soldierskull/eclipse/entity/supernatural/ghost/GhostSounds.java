package com.soldierskull.eclipse.entity.supernatural.ghost;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class GhostSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Eclipse.MOD_ID);

    public static final RegistryObject<SoundEvent> GHOST_AMBIENT = SOUNDS.register("entity.ghost.ambient",
            () -> new SoundEvent(new ResourceLocation(Eclipse.MOD_ID, "entity.ghost.ambient")));

    public static final RegistryObject<SoundEvent> GHOST_HURT = SOUNDS.register("entity.ghost.hurt",
            () -> new SoundEvent(new ResourceLocation(Eclipse.MOD_ID, "entity.ghost.hurt")));

    public static final RegistryObject<SoundEvent> GHOST_DEATH = SOUNDS.register("entity.ghost.death",
            () -> new SoundEvent(new ResourceLocation(Eclipse.MOD_ID, "entity.ghost.death")));

    // Método necessário para conectar o registro ao barramento de eventos do Forge na classe principal (Eclipse.java)
    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}