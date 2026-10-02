package com.soldierskull.eclipse.entity.supernatural.abyssalhand;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Mao Abissal (item 13.1). Imovel, controle/armadilha via agarrao.
 * - alcance 2 blocos, agarra por 5s a 0.5 HP/s (2.5 HP total)
 * - atacar a mao NAO reduz a duracao do agarrao (regra explicita)
 * - 40% menos dano recebido (resistencia media)
 * - cooldown de 5s entre agarroes
 * Sem IA de movimento nenhuma - fica presa ao ponto de spawn.
 *
 * grabTicksRemaining/detectionTicks sao sincronizados via SynchedEntityData
 * (DataParameter) porque so existiam como campos server-side antes - o
 * AbyssalHandModel (cliente) precisa desses valores pra animar o
 * fechamento dos dedos, e sem sync o cliente sempre veria 0.
 */
public class AbyssalHandEntity extends MonsterEntity implements ISupernaturalMob {

    private static final DataParameter<Integer> GRAB_TICKS_REMAINING =
            EntityDataManager.defineId(AbyssalHandEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> DETECTION_TICKS =
            EntityDataManager.defineId(AbyssalHandEntity.class, DataSerializers.INT);

    private static final double GRAB_RANGE = 2.0D;
    private static final int GRAB_DURATION_TICKS = 5 * 20;
    private static final int GRAB_COOLDOWN_TICKS = 5 * 20;
    // Pausa de antecipacao antes do agarrao (spec de animacao, item 1.2):
    // "pequena pausa de antecipacao de aproximadamente 0.2-0.3 segundos"
    private static final int DETECTION_DURATION_TICKS = 5; // 0.25s a 20 ticks/s

    private int grabCooldown = 0;
    private PlayerEntity grabbedPlayer = null;
    private int grabTicksRemaining = 0;
    private int detectionTicks = 0;
    private PlayerEntity detectingPlayer = null;

    public AbyssalHandEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GRAB_TICKS_REMAINING, 0);
        this.entityData.define(DETECTION_TICKS, 0);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 8.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // trava a entidade no lugar - sem gravidade nem empurroes
        this.setDeltaMovement(0, 0, 0);

        if (this.level.isClientSide) return;

        if (grabCooldown > 0) grabCooldown--;

        if (grabbedPlayer != null) {
            if (grabbedPlayer.removed || grabbedPlayer.isDeadOrDying()
                    || this.distanceToSqr(grabbedPlayer) > (GRAB_RANGE + 1) * (GRAB_RANGE + 1)) {
                // solta se o jogador morreu/saiu do alcance por outro meio (ex: teleporte)
                releaseGrab();
                return;
            }
            grabTicksRemaining--;
            this.entityData.set(GRAB_TICKS_REMAINING, grabTicksRemaining);
            if (grabTicksRemaining % 20 == 0) {
                grabbedPlayer.hurt(DamageSource.mobAttack(this), 0.5F);
            }
            // trava o jogador perto da mao
            grabbedPlayer.setDeltaMovement(0, grabbedPlayer.getDeltaMovement().y, 0);
            if (grabTicksRemaining <= 0) {
                releaseGrab();
                grabCooldown = GRAB_COOLDOWN_TICKS;
            }
            return;
        }

        if (grabCooldown <= 0) {
            PlayerEntity nearby = null;
            for (PlayerEntity player : this.level.getEntitiesOfClass(PlayerEntity.class,
                    this.getBoundingBox().inflate(GRAB_RANGE))) {
                if (!player.isSpectator() && !player.isCreative()) {
                    nearby = player;
                    break;
                }
            }
            if (nearby != null) {
                if (detectingPlayer != nearby) {
                    detectingPlayer = nearby;
                    detectionTicks = 0;
                }
                detectionTicks++;
                this.entityData.set(DETECTION_TICKS, detectionTicks);
                if (detectionTicks >= DETECTION_DURATION_TICKS) {
                    startGrab(nearby);
                    detectingPlayer = null;
                    detectionTicks = 0;
                    this.entityData.set(DETECTION_TICKS, 0);
                }
            } else if (detectingPlayer != null) {
                detectingPlayer = null;
                detectionTicks = 0;
                this.entityData.set(DETECTION_TICKS, 0);
            }
        }
    }

    private void startGrab(PlayerEntity player) {
        this.grabbedPlayer = player;
        this.grabTicksRemaining = GRAB_DURATION_TICKS;
        this.entityData.set(GRAB_TICKS_REMAINING, GRAB_DURATION_TICKS);
        this.playSound(net.minecraft.util.SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 1.0F, 0.6F);
        if (this.level instanceof net.minecraft.world.server.ServerWorld) {
            ((net.minecraft.world.server.ServerWorld) this.level).sendParticles(
                    net.minecraft.particles.ParticleTypes.SOUL,
                    player.getX(), player.getY() + 0.5, player.getZ(), 8, 0.3, 0.3, 0.3, 0.01);
        }
    }

    private void releaseGrab() {
        this.grabbedPlayer = null;
        this.grabTicksRemaining = 0;
        this.entityData.set(GRAB_TICKS_REMAINING, 0);
    }

    // ---------- Consultado pelo AbyssalHandModel (cliente) para animar ----------

    public boolean isGrabbing() {
        return this.entityData.get(GRAB_TICKS_REMAINING) > 0;
    }

    public boolean isDetecting() {
        return this.entityData.get(DETECTION_TICKS) > 0;
    }

    /** 0 = acabou de agarrar, 1 = prestes a soltar. */
    public float getGrabProgress() {
        return 1.0F - (this.entityData.get(GRAB_TICKS_REMAINING) / (float) GRAB_DURATION_TICKS);
    }

    /** 0 = acabou de perceber o jogador, 1 = prestes a agarrar. */
    public float getDetectionProgress() {
        return this.entityData.get(DETECTION_TICKS) / (float) DETECTION_DURATION_TICKS;
    }

    /** Ticks desde a morte começou (0 se viva) - usado pra animar a mão afundando no chão. */
    public int getDeathTime() {
        return this.deathTime;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 40% menos dano recebido - resistencia media, nao HP excessivo.
        // Atacar a mao durante o agarrao NAO reduz a duracao (nao ha logica
        // ligando dano recebido a grabTicksRemaining - e proposital).
        return super.hurt(source, amount * 0.6F);
    }

    @Override
    public boolean canBeAffected(net.minecraft.potion.EffectInstance effect) {
        return false; // mao nao anda, nao tem sentido receber a maioria dos efeitos
    }

    @Override
    protected void registerGoals() {
        // sem goals de movimento/perseguicao - a mao e completamente imovel
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void knockback(float strength, double x, double z) {
        // ignora knockback - permanece presa ao local
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.ABYSSAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.COMMON;
    }
}
