package com.soldierskull.eclipse.entity.supernatural.bloodworm;

import com.soldierskull.eclipse.effect.ModEffects;
import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * Vorme de Sangue (item 13.2 + secao 14). Fica enterrado (invisivel),
 * emerge e investe quando o jogador se aproxima, aplica Sangramento I,
 * pode voltar a se enterrar quando perde o jogador.
 *
 * ATUALIZADO para suportar a especificacao de animacao: emergir/enterrar
 * agora sao TRANSICOES com duracao (nao um on/off instantaneo de
 * isInvisible), e a investida tem uma pausa de preparacao antes do
 * bote, tudo sincronizado ao cliente via SynchedEntityData pro
 * BloodWormModel poder animar corretamente.
 */
public class BloodWormEntity extends MonsterEntity implements ISupernaturalMob {

    /** Estados possiveis, sincronizados como byte simples. */
    public static final byte STATE_BURIED = 0;
    public static final byte STATE_EMERGING = 1;
    public static final byte STATE_SURFACED = 2;
    public static final byte STATE_WINDUP = 3;
    public static final byte STATE_DASH = 4;
    public static final byte STATE_SUBMERGING = 5;

    private static final int EMERGE_DURATION = 8;   // ~0.4s
    private static final int SUBMERGE_DURATION = 8; // ~0.4s
    private static final int WINDUP_DURATION = 5;    // ~0.25s (spec 2.5)
    private static final int DASH_DURATION = 4;      // ~0.2s

    private static final double DETECT_RANGE = 3.5D;
    private static final int CHARGE_COOLDOWN_TICKS = 5 * 20;

    private static final DataParameter<Byte> STATE =
            EntityDataManager.defineId(BloodWormEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> STATE_TICKS =
            EntityDataManager.defineId(BloodWormEntity.class, DataSerializers.INT);

    private byte state = STATE_BURIED;
    private int stateTicks = 0;
    private int chargeCooldown = 0;
    private PlayerEntity pendingChargeTarget = null;

    public BloodWormEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, STATE_BURIED);
        this.entityData.define(STATE_TICKS, 0);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new EmergeAndChargeGoal(this));
    }

    /**
     * Invisivel apenas quando totalmente enterrado - durante emergir/submergir o modelo ja fica visivel (sobe/desce).
     *
     * BUGFIX: tinha que ler getState() (o valor sincronizado via entityData),
     * nao o campo local "state" - esse campo so e atualizado dentro de
     * setState(), que roda exclusivamente no servidor (dentro da Goal). No
     * cliente o campo local ficava para sempre travado em STATE_BURIED,
     * entao isInvisible() sempre retornava true e o modelo nunca era
     * desenhado, mesmo com a entidade emergida e atacando normalmente no
     * servidor.
     */
    @Override
    public boolean isInvisible() {
        return getState() == STATE_BURIED || super.isInvisible();
    }

    @Override
    public boolean isPushable() {
        return state != STATE_BURIED && super.isPushable();
    }

    public boolean isBurrowed() {
        return state == STATE_BURIED || state == STATE_SUBMERGING;
    }

    private void setState(byte newState) {
        this.state = newState;
        this.stateTicks = 0;
        this.entityData.set(STATE, newState);
        this.entityData.set(STATE_TICKS, 0);
        this.noPhysics = (newState == STATE_BURIED);
        this.setNoGravity(newState == STATE_BURIED || newState == STATE_EMERGING || newState == STATE_SUBMERGING);
    }

    private void tickState() {
        stateTicks++;
        this.entityData.set(STATE_TICKS, stateTicks);
    }

    void tickChargeCooldown() {
        if (chargeCooldown > 0) chargeCooldown--;
    }

    boolean canCharge() {
        return chargeCooldown <= 0;
    }

    void startChargeCooldown() {
        chargeCooldown = CHARGE_COOLDOWN_TICKS;
    }

    // ---------- Consultado pelo BloodWormModel (cliente) ----------

    public byte getState() {
        return this.entityData.get(STATE);
    }

    /** Progresso 0..1 dentro do estado atual (usa a duracao certa por estado). */
    public float getStateProgress() {
        int ticks = this.entityData.get(STATE_TICKS);
        int duration;
        switch (getState()) {
            case STATE_EMERGING: duration = EMERGE_DURATION; break;
            case STATE_SUBMERGING: duration = SUBMERGE_DURATION; break;
            case STATE_WINDUP: duration = WINDUP_DURATION; break;
            case STATE_DASH: duration = DASH_DURATION; break;
            default: duration = 1;
        }
        return Math.min(1.0F, ticks / (float) duration);
    }

    public int getDeathTime() {
        return this.deathTime;
    }

    @Override
    public boolean doHurtTarget(net.minecraft.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof LivingEntity) {
            ModEffects.applyBleeding((LivingEntity) target, 1);
        }
        return result;
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.ABYSSAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.TRIVIAL;
    }

    /**
     * Goal customizada (item 49). Maquina de estados:
     * BURIED -> (detecta jogador) -> EMERGING -> SURFACED
     *   -> (perto o suficiente e sem cooldown) -> WINDUP -> DASH (aplica dano/bleeding)
     *   -> (perde alvo) -> SUBMERGING -> BURIED
     */
    static class EmergeAndChargeGoal extends Goal {
        private final BloodWormEntity worm;
        private PlayerEntity target;

        EmergeAndChargeGoal(BloodWormEntity worm) {
            this.worm = worm;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            worm.tickChargeCooldown();
            if (worm.state != BloodWormEntity.STATE_BURIED) return true; // ja em transicao/superficie, continua
            PlayerEntity nearest = worm.level.getNearestPlayer(worm, DETECT_RANGE);
            if (nearest != null && !nearest.isSpectator() && !nearest.isCreative()) {
                this.target = nearest;
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            if (worm.state == BloodWormEntity.STATE_BURIED) {
                worm.setState(BloodWormEntity.STATE_EMERGING);
                worm.playSound(net.minecraft.util.SoundEvents.ZOMBIE_INFECT, 1.0F, 0.7F);
                if (worm.level instanceof net.minecraft.world.server.ServerWorld) {
                    ((net.minecraft.world.server.ServerWorld) worm.level).sendParticles(
                            net.minecraft.particles.ParticleTypes.CRIMSON_SPORE,
                            worm.getX(), worm.getY() + 0.2, worm.getZ(), 12, 0.4, 0.2, 0.4, 0.02);
                }
            }
        }

        @Override
        public void tick() {
            worm.tickState();

            switch (worm.state) {
                case BloodWormEntity.STATE_EMERGING:
                    if (worm.getStateProgress() >= 1.0F) {
                        worm.setState(BloodWormEntity.STATE_SURFACED);
                    }
                    return;

                case BloodWormEntity.STATE_SUBMERGING:
                    if (worm.getStateProgress() >= 1.0F) {
                        worm.setState(BloodWormEntity.STATE_BURIED);
                    }
                    return;

                case BloodWormEntity.STATE_WINDUP:
                    worm.getNavigation().stop();
                    if (worm.getStateProgress() >= 1.0F) {
                        worm.setState(BloodWormEntity.STATE_DASH);
                        if (worm.pendingChargeTarget != null && !worm.pendingChargeTarget.isDeadOrDying()) {
                            worm.doHurtTarget(worm.pendingChargeTarget);
                            double dx = worm.pendingChargeTarget.getX() - worm.getX();
                            double dz = worm.pendingChargeTarget.getZ() - worm.getZ();
                            worm.pendingChargeTarget.knockback(0.6F, -dx, -dz);
                        }
                        worm.startChargeCooldown();
                    }
                    return;

                case BloodWormEntity.STATE_DASH:
                    if (worm.getStateProgress() >= 1.0F) {
                        worm.setState(BloodWormEntity.STATE_SURFACED);
                    }
                    return;

                default:
                    break; // SURFACED cai pra logica normal abaixo
            }

            if (target == null || target.isSpectator() || target.isDeadOrDying()
                    || worm.distanceToSqr(target) > 20 * 20) {
                target = worm.level.getNearestPlayer(worm, 20);
            }
            if (target == null) {
                worm.setState(BloodWormEntity.STATE_SUBMERGING); // perdeu o jogador -> volta a se enterrar
                return;
            }
            worm.getLookControl().setLookAt(target, 30F, 30F);
            worm.getNavigation().moveTo(target, 1.1D);

            double distSq = worm.distanceToSqr(target);
            if (distSq < 2.2D * 2.2D && worm.canCharge()) {
                worm.pendingChargeTarget = target;
                worm.setState(BloodWormEntity.STATE_WINDUP);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return worm.state != BloodWormEntity.STATE_BURIED;
        }
    }
}