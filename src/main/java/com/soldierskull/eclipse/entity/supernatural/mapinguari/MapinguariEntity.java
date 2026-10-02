package com.soldierskull.eclipse.entity.supernatural.mapinguari;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import com.soldierskull.eclipse.stats.ReputationFaction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

/**
 * Mapinguari (item 16.6-21). Mini-boss. Nao possui spawn natural comum -
 * so nasce via MapinguariSpawnHandler (contador de 50 kills + 45%).
 * Sem despawn natural (item 50). Pisada (AoE) e Grito (Veneno, atravessa
 * paredes). Ao morrer: +15 reputacao Hunter direta, 85% de dropar
 * mapinguari_tongue (item ja registrado, sem receita nesta fase).
 *
 * ANIMACAO: STOMP_WINDUP e ROAR_TICKS sincronizados via
 * SynchedEntityData pro MapinguariModel animar a pisada (12.5) e o
 * grito (12.6) - antes eram so timers internos da Goal, invisiveis
 * pro cliente.
 */
public class MapinguariEntity extends MonsterEntity implements ISupernaturalMob {

    private static final int ROAR_DURATION = 16; // ~0.8s de animacao do grito

    private static final DataParameter<Integer> STOMP_WINDUP =
            EntityDataManager.defineId(MapinguariEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> ROAR_TICKS =
            EntityDataManager.defineId(MapinguariEntity.class, DataSerializers.INT);

    public MapinguariEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        this.xpReward = 60;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STOMP_WINDUP, 0);
        this.entityData.define(ROAR_TICKS, 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.entityData.get(ROAR_TICKS) > 0) {
            this.entityData.set(ROAR_TICKS, this.entityData.get(ROAR_TICKS) - 1);
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StompGoal(this));
        this.goalSelector.addGoal(2, new RoarGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.targetSelector.addGoal(1, new net.minecraft.entity.ai.goal.NearestAttackableTargetGoal<>(
                this, PlayerEntity.class, true));
    }

    /** Mini-bosses nao devem possuir despawn natural (item 50). */
    @Override
    public boolean removeWhenFarAway(double distanceSq) {
        return false;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level.isClientSide && source.getEntity() instanceof PlayerEntity) {
            PlayerEntity killer = (PlayerEntity) source.getEntity();
            killer.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats ->
                    stats.addReputation(ReputationFaction.HUNTERS, 15));
        }
        super.die(source);
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.OVERWORLD_SUPERNATURAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.MINIBOSS;
    }

    // ---------- Consultado pelo MapinguariModel (cliente) ----------

    /** 0 = idle, sobe ate 14 (windup) depois volta a 0 no exato tick do impacto. */
    public int getStompWindupTicks() {
        return this.entityData.get(STOMP_WINDUP);
    }

    public boolean isRoaring() {
        return this.entityData.get(ROAR_TICKS) > 0;
    }

    public float getRoarProgress() {
        return 1.0F - (this.entityData.get(ROAR_TICKS) / (float) ROAR_DURATION);
    }

    public int getDeathTime() {
        return this.deathTime;
    }

    /** Pisada: levanta o pe, aviso sonoro, ~0.7s, pisada em area. Cooldown 5-8s. */
    static class StompGoal extends Goal {
        private final MapinguariEntity mob;
        private int cooldown = 0;
        private int windupTicks = -1;

        StompGoal(MapinguariEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return mob.getTarget() != null && mob.distanceTo(mob.getTarget()) <= 4.0D;
        }

        @Override
        public void start() {
            cooldown = 0;
            windupTicks = 14; // ~0.7s
            mob.entityData.set(STOMP_WINDUP, windupTicks);
            mob.playSound(net.minecraft.util.SoundEvents.RAVAGER_ROAR, 1.0F, 0.6F);
            mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (windupTicks > 0) {
                windupTicks--;
                mob.entityData.set(STOMP_WINDUP, windupTicks);
                if (windupTicks == 0) {
                    stomp();
                    cooldown = (5 + mob.random.nextInt(4)) * 20;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return windupTicks > 0;
        }

        private void stomp() {
            List<LivingEntity> hit = mob.level.getEntitiesOfClass(LivingEntity.class,
                    mob.getBoundingBox().inflate(4.0D));
            for (LivingEntity entity : hit) {
                if (entity == mob) continue;
                entity.hurt(DamageSource.mobAttack(mob), 10.0F);
                double dx = entity.getX() - mob.getX();
                double dz = entity.getZ() - mob.getZ();
                entity.knockback(1.2F, -dx, -dz);
            }
            mob.playSound(net.minecraft.util.SoundEvents.GENERIC_BIG_FALL, 1.2F, 0.7F);
        }
    }

    /** Grito: raio 12, atravessa paredes, aplica Veneno. Cooldown ~10s. */
    static class RoarGoal extends Goal {
        private final MapinguariEntity mob;
        private int cooldown = 0;

        RoarGoal(MapinguariEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (cooldown > 0) {
                cooldown--;
                return false;
            }
            return mob.getTarget() != null;
        }

        @Override
        public void start() {
            mob.playSound(net.minecraft.util.SoundEvents.RAVAGER_ROAR, 1.5F, 0.4F);
            mob.entityData.set(ROAR_TICKS, ROAR_DURATION);
            List<LivingEntity> hit = mob.level.getEntitiesOfClass(LivingEntity.class,
                    mob.getBoundingBox().inflate(12.0D));
            for (LivingEntity entity : hit) {
                if (entity == mob) continue;
                entity.addEffect(new EffectInstance(Effects.POISON, 5 * 20, 0));
            }
            cooldown = 10 * 20;
        }

        @Override
        public boolean canContinueToUse() {
            return false; // acao instantanea
        }
    }
}
