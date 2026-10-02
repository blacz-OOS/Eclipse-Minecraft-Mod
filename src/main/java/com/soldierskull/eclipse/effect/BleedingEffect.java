package com.soldierskull.eclipse.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

/**
 * Sangramento (Fase 14, item 59-60).
 *
 * Efeito próprio do Eclipse.
 *
 * Dano:
 * - 0.5 HP por segundo
 * - 0.25 coração por segundo
 *
 * Níveis:
 * - Sangramento I   = 3 segundos
 * - Sangramento II  = 5 segundos
 * - Sangramento III = 8 segundos
 * - Sangramento IV  = 10 segundos
 *
 * O amplifier NÃO altera o dano.
 * Ele serve apenas para diferenciar os níveis do efeito.
 *
 * A duração é definida pelo EffectInstance no momento em que
 * o efeito é aplicado.
 */
public class BleedingEffect extends Effect {

    public BleedingEffect() {
        super(
                EffectType.HARMFUL,
                0x8B0000
        );
    }

    /**
     * Define quando o efeito deve executar seu dano.
     *
     * Minecraft possui 20 ticks por segundo.
     *
     * Portanto:
     *
     * duration % 20 == 0
     *
     * faz o efeito executar aproximadamente uma vez por segundo.
     */
    @Override
    public boolean isDurationEffectTick(
            int duration,
            int amplifier
    ) {
        return duration % 20 == 0;
    }

    /**
     * Executa o dano do Sangramento.
     */
    @Override
    public void applyEffectTick(
            LivingEntity entity,
            int amplifier
    ) {
        /*
         * O dano deve ser aplicado apenas no servidor.
         * Caso contrário, poderíamos causar inconsistências
         * entre cliente e servidor.
         */
        if (!entity.level.isClientSide) {

            entity.hurt(
                    DamageSource.GENERIC,
                    0.5F
            );
        }
    }
}