package com.soldierskull.eclipse.entity.supernatural.wendigo;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import com.soldierskull.eclipse.stats.ReputationFaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Wendigo (item 22-30). Elite, extremamente agressivo, nunca foge.
 * 5 estagios de dano/velocidade calculados a partir do %HP ATUAL do
 * alvo (recalcula a cada tick de ataque - cura do jogador reduz o
 * estagio imediatamente, sem acumulo permanente). Regeneracao 1 HP/2s.
 *
 * ANIMACAO: STAGE (1-5) sincronizado pro WendigoModel refletir os
 * "Rage Stages" visuais (spec 13.5) - o calculo ja existia so nos
 * atributos server-side, sem forma do cliente saber qual estagio
 * mostrar. REGEN_PULSE tambem sincronizado (13.6).
 */
public class WendigoEntity extends MonsterEntity implements ISupernaturalMob {

    private static final double BASE_DAMAGE = 9.0D;
    private static final double BASE_SPEED = 0.34D;
    private static final int REGEN_PULSE_DURATION = 6;

    private static final DataParameter<Byte> STAGE =
            EntityDataManager.defineId(WendigoEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> REGEN_PULSE =
            EntityDataManager.defineId(WendigoEntity.class, DataSerializers.INT);

    private int regenTimer = 0;

    public WendigoEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        this.xpReward = 35;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STAGE, (byte) 1);
        this.entityData.define(REGEN_PULSE, 0);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.ATTACK_DAMAGE, BASE_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) return;

        if (this.entityData.get(REGEN_PULSE) > 0) {
            this.entityData.set(REGEN_PULSE, this.entityData.get(REGEN_PULSE) - 1);
        }

        regenTimer++;
        if (regenTimer >= 40) { // 2s
            regenTimer = 0;
            if (this.getHealth() < this.getMaxHealth()) {
                this.heal(1.0F);
                this.entityData.set(REGEN_PULSE, REGEN_PULSE_DURATION);
            }
        }

        // recalcula estagio continuamente com base no alvo atual, pra "cura reduz estagio" ser imediato
        if (this.getTarget() instanceof PlayerEntity) {
            applyStage((PlayerEntity) this.getTarget());
        }
    }

    /** Estagios (item 27) - bonus calculados sobre os atributos BASE, nao acumulativos. */
    private void applyStage(PlayerEntity target) {
        float hpFraction = target.getHealth() / target.getMaxHealth();
        double damageMult;
        double speedMult;
        byte stage;

        if (hpFraction > 0.75F) {
            damageMult = 1.00D; speedMult = 1.00D; stage = 1;
        } else if (hpFraction > 0.50F) {
            damageMult = 1.10D; speedMult = 1.10D; stage = 2;
        } else if (hpFraction > 0.25F) {
            damageMult = 1.25D; speedMult = 1.20D; stage = 3;
        } else if (hpFraction > 0.10F) {
            damageMult = 1.40D; speedMult = 1.35D; stage = 4;
        } else {
            damageMult = 1.60D; speedMult = 1.50D; stage = 5;
        }

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * damageMult);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED * speedMult);
        this.entityData.set(STAGE, stage);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return super.doHurtTarget(target);
    }

    /** Elite nunca foge (secao 26) - sobrescreve qualquer chance de fuga por dano/HP baixo. */
    @Override
    public boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level.isClientSide && source.getEntity() instanceof PlayerEntity) {
            PlayerEntity killer = (PlayerEntity) source.getEntity();
            killer.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats ->
                    stats.addReputation(ReputationFaction.HUNTERS, 10));
        }
        super.die(source);
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.OVERWORLD_SUPERNATURAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.ELITE;
    }

    // ---------- Consultado pelo WendigoModel (cliente) ----------

    public int getStage() {
        return this.entityData.get(STAGE);
    }

    public boolean isRegenPulseActive() {
        return this.entityData.get(REGEN_PULSE) > 0;
    }

    public int getDeathTime() {
        return this.deathTime;
    }
}
