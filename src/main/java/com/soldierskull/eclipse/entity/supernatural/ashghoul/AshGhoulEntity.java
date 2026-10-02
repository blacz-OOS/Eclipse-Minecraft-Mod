package com.soldierskull.eclipse.entity.supernatural.ashghoul;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Carniçal das Cinzas (item 13.4 + secao 15). Sem visao - deteccao 100%
 * sonora, com alcances diferentes por tipo de som. Perde o alvo se o
 * jogador ficar parado 3s. Regenera Regeneracao II a cada 12s enquanto
 * ferido (item explicito: obriga o jogador a manter pressao).
 *
 * NOTA DE PERFORMANCE (secao 52): a "audicao" nao roda todo tick - so
 * verifica a cada 10 ticks (0.5s), suficiente pra nao pesar e ainda
 * detectar o jogador rapido o bastante.
 *
 * ANIMACAO: a transicao "detectou som" (item 4.2 - "deve ser muito
 * perceptivel") precisa de um pulso sincronizado, ja que setTarget()
 * so muda estado server-side. Regeneracao NAO precisa de sync propria -
 * efeitos de pocao (EffectInstance) ja sao replicados automaticamente
 * pelo vanilla, entao o Model consulta hasEffect(REGENERATION) direto.
 * Ataque usa getAttackAnim() (padrao vanilla), tambem ja sincronizado.
 */
public class AshGhoulEntity extends MonsterEntity implements ISupernaturalMob {

    private static final int HEARING_CHECK_INTERVAL = 10;
    private static final int LOSE_TARGET_STILL_TICKS = 3 * 20;
    private static final int REGEN_INTERVAL_TICKS = 12 * 20;
    private static final int DETECTION_PULSE_DURATION = 8; // ~0.4s de "parou e virou a cabeca"

    private static final DataParameter<Integer> DETECTION_PULSE =
            EntityDataManager.defineId(AshGhoulEntity.class, DataSerializers.INT);

    private int stillTicks = 0;
    private double lastX, lastY, lastZ;
    private int regenTimer = 0;

    public AshGhoulEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        this.xpReward = 6;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DETECTION_PULSE, 0);
    }

    @Override
    public void setTarget(LivingEntity target) {
        boolean acquiring = target != null && this.getTarget() == null;
        super.setTarget(target);
        if (acquiring && !this.level.isClientSide) {
            this.entityData.set(DETECTION_PULSE, DETECTION_PULSE_DURATION);
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(2, new net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new net.minecraft.entity.ai.goal.LookAtGoal(this, PlayerEntity.class, 8.0F));
        // Sem alvo automatico visual - o alvo e definido manualmente via audicao (aiStep)
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) return;

        if (this.entityData.get(DETECTION_PULSE) > 0) {
            this.entityData.set(DETECTION_PULSE, this.entityData.get(DETECTION_PULSE) - 1);
        }

        // regeneracao a cada 12s enquanto ferido
        regenTimer++;
        if (regenTimer >= REGEN_INTERVAL_TICKS) {
            regenTimer = 0;
            if (this.getHealth() < this.getMaxHealth()) {
                this.addEffect(new EffectInstance(Effects.REGENERATION, 60, 1)); // Regen II por 3s
            }
        }

        if (this.tickCount % HEARING_CHECK_INTERVAL == 0) {
            updateHearing();
        }

        // "jogador parado 3s despista" - so conta enquanto e o alvo atual
        if (this.getTarget() instanceof PlayerEntity) {
            PlayerEntity target = (PlayerEntity) this.getTarget();
            double dx = target.getX() - lastX, dy = target.getY() - lastY, dz = target.getZ() - lastZ;
            if (dx * dx + dy * dy + dz * dz < 0.01D) {
                stillTicks++;
                if (stillTicks >= LOSE_TARGET_STILL_TICKS) {
                    this.setTarget(null);
                    stillTicks = 0;
                }
            } else {
                stillTicks = 0;
            }
            lastX = target.getX();
            lastY = target.getY();
            lastZ = target.getZ();
        }
    }

    private void updateHearing() {
        if (this.getTarget() != null) return; // ja tem alvo, nao precisa procurar
        for (PlayerEntity player : this.level.getEntitiesOfClass(PlayerEntity.class,
                this.getBoundingBox().inflate(20))) {
            if (player.isSpectator() || player.isCreative()) continue;
            double dist = this.distanceTo(player);
            double range = estimateNoiseRange(player);
            if (dist <= range) {
                this.setTarget(player);
                break;
            }
        }
    }

    /** Alcances de audicao (secao 15) - aproximado por estado do jogador. */
    private double estimateNoiseRange(PlayerEntity player) {
        if (player.swinging || player.getLastHurtMob() != null) return 20; // combate
        if (player.isSprinting()) return 12; // correr
        if (player.walkDist != player.walkDistO) return 6; // andar
        return 6;
        // quebrar bloco (16) e explosao (24) sao eventos pontuais tratados
        // separadamente por quem dispara o evento (fora do escopo desta
        // classe - ver observacao no README do patch).
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.ABYSSAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.COMMON;
    }

    // ---------- Consultado pelo AshGhoulModel (cliente) ----------

    /** 0..1 - momento exato em que percebeu o som (item 4.2, "muito perceptivel"). */
    public float getDetectionPulseProgress() {
        return this.entityData.get(DETECTION_PULSE) / (float) DETECTION_PULSE_DURATION;
    }

    public boolean isDetectionPulseActive() {
        return this.entityData.get(DETECTION_PULSE) > 0;
    }

    public int getDeathTime() {
        return this.deathTime;
    }
}
