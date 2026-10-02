package com.soldierskull.eclipse.event;

import java.util.UUID;

import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.skills.EclipseEventManager;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Regras de combate e pressão de spawn exclusivas do Eclipse. */
@Mod.EventBusSubscriber(modid = "eclipse")
public final class EclipseCombatEvents {

    private static final UUID MOB_HEALTH = UUID.fromString("3d680cea-7d2f-4b56-87ce-7dd4f2220001");
    private static final UUID MOB_DAMAGE = UUID.fromString("3d680cea-7d2f-4b56-87ce-7dd4f2220002");
    private static final UUID MOB_SPEED = UUID.fromString("3d680cea-7d2f-4b56-87ce-7dd4f2220003");
    private static final UUID PLAYER_HEALTH = UUID.fromString("3d680cea-7d2f-4b56-87ce-7dd4f2220004");
    private static final UUID PLAYER_DAMAGE = UUID.fromString("3d680cea-7d2f-4b56-87ce-7dd4f2220005");
    private static final UUID PLAYER_ARMOR = UUID.fromString("3d680cea-7d2f-4b56-87ce-7dd4f2220006");
    private static final UUID PLAYER_SPEED = UUID.fromString("3d680cea-7d2f-4b56-87ce-7dd4f2220007");
    private static final String DUPLICATE_TAG = "eclipse_spawn_duplicate";

    private EclipseCombatEvents() { }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!(entity.level instanceof ServerWorld)) return;
        boolean eclipse = EclipseEventManager.isActive((ServerWorld) entity.level);
        if (entity instanceof IMob) {
            setMultiplier(entity, Attributes.MAX_HEALTH, MOB_HEALTH, eclipse ? 2.0D : 1.0D);
            setMultiplier(entity, Attributes.ATTACK_DAMAGE, MOB_DAMAGE, eclipse ? 2.0D : 1.0D);
            setMultiplier(entity, Attributes.MOVEMENT_SPEED, MOB_SPEED, eclipse ? 2.0D : 1.0D);
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            boolean supernatural = player.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP)
                    .map(stats -> stats.getRace() != RaceType.HUMAN).orElse(false);
            double multiplier = eclipse && supernatural ? 1.5D : 1.0D;
            setMultiplier(player, Attributes.MAX_HEALTH, PLAYER_HEALTH, multiplier);
            setMultiplier(player, Attributes.ATTACK_DAMAGE, PLAYER_DAMAGE, multiplier);
            setMultiplier(player, Attributes.ARMOR, PLAYER_ARMOR, multiplier);
            setMultiplier(player, Attributes.MOVEMENT_SPEED, PLAYER_SPEED, multiplier);
        }
    }

    /** Cada spawn hostil natural ganha um segundo indivíduo durante o Eclipse. */
    @SubscribeEvent
    public static void onHostileJoin(EntityJoinWorldEvent event) {
        if (!(event.getWorld() instanceof ServerWorld) || !(event.getEntity() instanceof MobEntity)
                || !(event.getEntity() instanceof IMob) || event.getEntity().getPersistentData().getBoolean(DUPLICATE_TAG)
                || event.getEntity().tickCount > 1 || !EclipseEventManager.isActive((ServerWorld) event.getWorld())) return;
        MobEntity original = (MobEntity) event.getEntity();
        net.minecraft.entity.Entity created = original.getType().create((ServerWorld) event.getWorld());
        if (!(created instanceof MobEntity)) return;
        MobEntity duplicate = (MobEntity) created;
        duplicate.getPersistentData().putBoolean(DUPLICATE_TAG, true);
        duplicate.moveTo(original.getX() + original.getRandom().nextInt(5) - 2, original.getY(),
                original.getZ() + original.getRandom().nextInt(5) - 2, original.yRot, original.xRot);
        ((ServerWorld) event.getWorld()).addFreshEntity(duplicate);
    }

    private static void setMultiplier(LivingEntity entity, Attribute attribute, UUID id, double multiplier) {
        ModifiableAttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        AttributeModifier current = instance.getModifier(id);
        if (multiplier == 1.0D) {
            if (current != null) instance.removeModifier(current);
        } else if (current == null) {
            instance.addTransientModifier(new AttributeModifier(id, "eclipse.event", multiplier - 1.0D,
                    AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }
}
