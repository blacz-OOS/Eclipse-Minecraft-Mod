package com.soldierskull.eclipse.entity.supernatural.ghost;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class GhostEntity extends FlyingEntity implements ISupernaturalMob {

    private static final DataParameter<Boolean> GHOST_INVISIBLE = EntityDataManager.defineId(GhostEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CHASING = EntityDataManager.defineId(GhostEntity.class, DataSerializers.BOOLEAN);

    private int targetLostTimer = 0;

    public GhostEntity(EntityType<? extends FlyingEntity> type, World world) {
        super(type, world);
        this.moveControl = new GhostMoveController(this);
        this.noPhysics = true;
    }

    /** Fase 14 - Ghost é uma criatura sobrenatural do Overworld (lenda pré-existente, não do Abismo). */
    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.OVERWORLD_SUPERNATURAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.COMMON;
    }


    // Registra os atributos base (vida, velocidade, dano) exigidos pelo Forge no Minecraft 1.16.5
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)       // 20 de Vida (10 corações)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)   // Velocidade de movimento
                .add(Attributes.ATTACK_DAMAGE, 5.0D)     // 5 de Dano por ataque
                .add(Attributes.FOLLOW_RANGE, 16.0D);    // Distância para detectar e perseguir
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GHOST_INVISIBLE, false);
        this.entityData.define(CHASING, false);
    }

    @Override
    protected PathNavigator createNavigation(World world) {
        return new GhostNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        GhostGoals.registerGoals(this);
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);

        if (!this.level.isClientSide) {
            if (this.getTarget() != null && !this.getTarget().isAlive()) {
                this.setTarget(null);
            }
            if (this.getTarget() == null) {
                targetLostTimer++;
                if (targetLostTimer >= 100) {
                    this.remove();
                }
            } else {
                targetLostTimer = 0;
            }
        }

        if (this.level.isClientSide && this.getRandom().nextInt(3) == 0) {
            this.level.addParticle(net.minecraft.particles.ParticleTypes.SOUL_FIRE_FLAME,
                    this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.02D, 0.0D);
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.entity.Entity entity) {
        boolean hurt = super.doHurtTarget(entity);
        if (hurt && entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 80, 0));
        }
        return hurt;
    }

    public boolean isEclipseActive() {
        return this.level.isNight() && this.level.isThundering();
    }

    public boolean isGhostInvisible() {
        return this.entityData.get(GHOST_INVISIBLE);
    }

    public void setGhostInvisible(boolean value) {
        this.entityData.set(GHOST_INVISIBLE, value);
        this.setInvisible(value);
    }

    public boolean isChasing() {
        return this.entityData.get(CHASING);
    }

    public void setChasing(boolean value) {
        this.entityData.set(CHASING, value);
    }

    @Override
    public boolean causeFallDamage(float dist, float multiplier) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return GhostSounds.GHOST_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource src) {
        return GhostSounds.GHOST_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return GhostSounds.GHOST_DEATH.get();
    }
}