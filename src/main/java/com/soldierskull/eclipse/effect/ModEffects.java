package com.soldierskull.eclipse.effect;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.entity.LivingEntity;


import net.minecraft.potion.Effect;

import net.minecraft.potion.EffectInstance;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects {

    public static final DeferredRegister<Effect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.POTIONS, Eclipse.MOD_ID);

    public static final RegistryObject<Effect> BLEEDING =
            EFFECTS.register("bleeding", BleedingEffect::new);

    // Duracao em ticks por nivel (item 59-60 da Fase 14) - I=3s II=5s III=8s IV=10s
    private static final int[] LEVEL_DURATION_TICKS = {3 * 20, 5 * 20, 8 * 20, 10 * 20};

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }

    /**
     * Aplica Sangramento no nivel dado (1..4 = I..IV). Reaplicar o mesmo
     * nivel enquanto o efeito ja esta ativo reinicia a duracao (padrao do
     * EffectInstance ao receber uma nova instancia com duracao igual/maior).
     */
    public static void applyBleeding(LivingEntity target, int level) {
        int lvl = Math.max(1, Math.min(4, level));
        int duration = LEVEL_DURATION_TICKS[lvl - 1];
        target.addEffect(new EffectInstance(BLEEDING.get(), duration, lvl - 1, false, true));
    }
}
