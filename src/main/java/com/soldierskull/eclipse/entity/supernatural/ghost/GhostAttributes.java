package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;

public class GhostAttributes {
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)            // Zombie vanilla 20.0 * 1.5
                .add(Attributes.MOVEMENT_SPEED, 0.345D)        // Zombie vanilla 0.23 * 1.5
                .add(Attributes.ATTACK_DAMAGE, 3.0D)           // Zombie vanilla damage (3.0 Normal)
                .add(Attributes.ARMOR, 3.0D)                   // Zombie vanilla armor 2.0 * 1.5
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.15D)   // +15% Knockback Resistance
                .add(Attributes.FOLLOW_RANGE, 52.5D);          // Zombie vanilla 35.0 * 1.5
    }
}
