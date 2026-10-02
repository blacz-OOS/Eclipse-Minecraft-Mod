package com.soldierskull.eclipse.particle;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registro dos ParticleType do mod Eclipse. Por enquanto so o laser do
 * Void Observer - mesmo padrao de DeferredRegister usado em ModEffects,
 * ModItems etc.
 */
public class ModParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Eclipse.MOD_ID);


    public static final RegistryObject<BasicParticleType> LASER =
            PARTICLE_TYPES.register("laser", () -> new BasicParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}