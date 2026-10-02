package com.soldierskull.eclipse.stats;

import java.util.UUID;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;

public final class StatAttributeHandler {

    private static final UUID STRENGTH_MODIFIER_ID = UUID.fromString("b12e2f1a-1f0a-4b1a-8f3a-a1c111100001");
    private static final UUID CONSTITUTION_MODIFIER_ID = UUID.fromString("b12e2f1a-1f0a-4b1a-8f3a-a1c111100002");
    private static final UUID DEFENSE_MODIFIER_ID = UUID.fromString("b12e2f1a-1f0a-4b1a-8f3a-a1c111100003");
    private static final UUID SPEED_MODIFIER_ID = UUID.fromString("b12e2f1a-1f0a-4b1a-8f3a-a1c111100004");

    private StatAttributeHandler() {
    }

    public static void apply(PlayerEntity player, PlayerStats stats) {
        setModifier(player, Attributes.ATTACK_DAMAGE, STRENGTH_MODIFIER_ID, "eclipse.strength",
                stats.getAttribute(AttributeType.STRENGTH) * StatBalance.ATTACK_DAMAGE_PER_STRENGTH);

        setModifier(player, Attributes.MAX_HEALTH, CONSTITUTION_MODIFIER_ID, "eclipse.constitution",
                stats.getAttribute(AttributeType.CONSTITUTION) * StatBalance.HEALTH_PER_CONSTITUTION);

        setModifier(player, Attributes.ARMOR, DEFENSE_MODIFIER_ID, "eclipse.defense",
                stats.getAttribute(AttributeType.DEFENSE) * StatBalance.ARMOR_PER_DEFENSE);

        setModifier(player, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_ID, "eclipse.speed",
                stats.getAttribute(AttributeType.SPEED) * StatBalance.MOVE_SPEED_PER_SPEED);
    }

    private static void setModifier(PlayerEntity player, Attribute attribute, UUID id, String name, double amount) {
        ModifiableAttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier existing = instance.getModifier(id);
        if (existing != null) {
            instance.removeModifier(existing);
        }
        if (amount != 0D) {
            instance.addPermanentModifier(new AttributeModifier(id, name, amount, AttributeModifier.Operation.ADDITION));
        }
    }
}