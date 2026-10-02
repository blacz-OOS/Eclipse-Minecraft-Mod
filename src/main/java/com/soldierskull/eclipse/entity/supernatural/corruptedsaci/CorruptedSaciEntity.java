package com.soldierskull.eclipse.entity.supernatural.corruptedsaci;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Saci Corrompido (item 16.3). A cada 10s tenta roubar a off-hand do
 * jogador (nao rouba livros/tomos), foge 10s atacando por 2 de dano
 * enquanto foge, dropa o item roubado ao morrer.
 *
 * "Tomos" no Eclipse sao itens dedicados do mod (ver quests/QuestBookItem)
 * - a checagem abaixo cobre tanto livros vanilla quanto qualquer item cujo
 * registry name contenha "tome"/"book", pra nao depender de importar cada
 * classe de tomo individualmente.
 *
 * ANIMACAO: FLEEING/HOLDING_ITEM/STEAL_PULSE sincronizados pro
 * CorruptedSaciModel diferenciar idle/roubo/fuga (fleeTicksRemaining e
 * stolenItem so existiam server-side).
 */
public class CorruptedSaciEntity extends MonsterEntity implements ISupernaturalMob {

    private static final int STEAL_INTERVAL_TICKS = 10 * 20;
    private static final int FLEE_DURATION_TICKS = 10 * 20;
    private static final int STEAL_PULSE_DURATION = 10; // ~0.5s de animacao do roubo em si

    private static final DataParameter<Boolean> FLEEING =
            EntityDataManager.defineId(CorruptedSaciEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HOLDING_ITEM =
            EntityDataManager.defineId(CorruptedSaciEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> STEAL_PULSE =
            EntityDataManager.defineId(CorruptedSaciEntity.class, DataSerializers.INT);

    private int stealTimer = 0;
    private int fleeTicksRemaining = 0;
    private ItemStack stolenItem = ItemStack.EMPTY;

    public CorruptedSaciEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
        this.xpReward = 5;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLEEING, false);
        this.entityData.define(HOLDING_ITEM, false);
        this.entityData.define(STEAL_PULSE, 0);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.36D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) return;

        if (this.entityData.get(STEAL_PULSE) > 0) {
            this.entityData.set(STEAL_PULSE, this.entityData.get(STEAL_PULSE) - 1);
        }

        if (fleeTicksRemaining > 0) {
            fleeTicksRemaining--;
            this.entityData.set(FLEEING, true);
            net.minecraft.entity.LivingEntity target = this.getTarget();
            if (target != null) {
                // continua "atacando enquanto foge" - o MeleeAttackGoal ja cuida do dano
                // via doHurtTarget; aqui so garantimos que ele nao para de se mover pra longe
                double dx = this.getX() - target.getX();
                double dz = this.getZ() - target.getZ();
                this.getNavigation().moveTo(this.getX() + dx, this.getY(), this.getZ() + dz, 1.3D);
            }
            if (fleeTicksRemaining <= 0) {
                this.entityData.set(FLEEING, false);
            }
            return;
        }

        stealTimer++;
        if (stealTimer >= STEAL_INTERVAL_TICKS && stolenItem.isEmpty()) {
            stealTimer = 0;
            for (PlayerEntity player : this.level.getEntitiesOfClass(PlayerEntity.class,
                    this.getBoundingBox().inflate(6))) {
                if (tryStealFrom(player)) break;
            }
        }
    }

    private boolean tryStealFrom(PlayerEntity player) {
        ItemStack offhand = player.getItemBySlot(EquipmentSlotType.OFFHAND);
        if (offhand.isEmpty() || isProtected(offhand)) return false;

        this.stolenItem = offhand.copy();
        player.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
        this.fleeTicksRemaining = FLEE_DURATION_TICKS;
        this.entityData.set(HOLDING_ITEM, true);
        this.entityData.set(STEAL_PULSE, STEAL_PULSE_DURATION);
        this.playSound(net.minecraft.util.SoundEvents.FOX_SCREECH, 1.0F, 1.4F);
        return true;
    }

    /** Nao rouba livros nem tomos ("nao gosta de ler" - item explicito). */
    private boolean isProtected(ItemStack stack) {
        if (stack.getItem() instanceof BookItem
                || stack.getItem() instanceof WrittenBookItem
                || stack.getItem() instanceof WritableBookItem) {
            return true;
        }
        String path = stack.getItem().getRegistryName() == null ? "" : stack.getItem().getRegistryName().getPath();
        return path.contains("tome") || path.contains("book");
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (fleeTicksRemaining > 0 && target instanceof net.minecraft.entity.LivingEntity) {
            // dano fixo de 2 mesmo fugindo (nao usa o attack damage attribute pra manter exatamente o valor da spec)
            return target.hurt(DamageSource.mobAttack(this), 2.0F);
        }
        return super.doHurtTarget(target);
    }

    @Override
    public void die(DamageSource source) {
        if (!stolenItem.isEmpty() && !this.level.isClientSide) {
            ItemEntity drop = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), stolenItem);
            this.level.addFreshEntity(drop);
            stolenItem = ItemStack.EMPTY;
            this.entityData.set(HOLDING_ITEM, false);
        }
        super.die(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if (!stolenItem.isEmpty()) {
            compound.put("StolenItem", stolenItem.save(new CompoundNBT()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("StolenItem")) {
            stolenItem = ItemStack.of(compound.getCompound("StolenItem"));
        }
    }

    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.OVERWORLD_SUPERNATURAL;
    }

    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.COMMON;
    }

    // ---------- Consultado pelo CorruptedSaciModel (cliente) ----------

    public boolean isFleeing() {
        return this.entityData.get(FLEEING);
    }

    public boolean isHoldingItem() {
        return this.entityData.get(HOLDING_ITEM);
    }

    public boolean isStealPulseActive() {
        return this.entityData.get(STEAL_PULSE) > 0;
    }

    public int getDeathTime() {
        return this.deathTime;
    }
}
