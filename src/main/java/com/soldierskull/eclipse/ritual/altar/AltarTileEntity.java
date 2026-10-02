package com.soldierskull.eclipse.ritual.altar;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

import com.soldierskull.eclipse.ritual.Ritual;
import com.soldierskull.eclipse.ritual.RitualCategory;
import com.soldierskull.eclipse.ritual.RitualCircleManager;
import com.soldierskull.eclipse.ritual.RitualContext;
import com.soldierskull.eclipse.ritual.RitualManager;
import com.soldierskull.eclipse.ritual.RitualRegistry;
import com.soldierskull.eclipse.ritual.RitualState;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * Estado de um Altar (comum) - toda a lógica de "o que fazer com esse
 * estado" vive em {@link RitualManager}; este TileEntity só guarda os
 * dados (Bloco B/#16, decisão confirmada: estado pertence ao altar, não
 * a um manager central nem a PlayerStats).
 *
 * Blood/Moon/Abyss Altar (Etapa 9) vão precisar de
 * {@code ritualPowerMax} e {@code allowedCategories} configuráveis por
 * subtipo - por enquanto isso está fixo aqui pro Altar comum (100 power,
 * GENERAL+HUNTER), e vira parâmetro de construtor quando os outros 3
 * altares forem implementados.
 */
public class AltarTileEntity extends TileEntity implements ITickableTileEntity {

    private static final int EVALUATE_INTERVAL_TICKS = 10; // não reavalia círculo/ingrediente todo tick, por performance

    private RitualState state = RitualState.INACTIVE;
    @Nullable
    private Ritual candidateRitual;
    @Nullable
    private RitualContext activeContext;
    private int ritualPower = 0;
    private ItemStack primaryIngredient = ItemStack.EMPTY;
    private int transientTimer = 0;
    private int evaluateCooldown = 0;
    @Nullable
    private ITextComponent lastMessage;

    public AltarTileEntity() {
        super(ModTileEntities.ALTAR.get());
    }

    /** Usado pelas subclasses (Blood/Moon/Abyss Altar - Etapa 8/9), que têm seu próprio TileEntityType. */
    protected AltarTileEntity(TileEntityType<?> type) {
        super(type);
    }

    /**
     * GENERAL+HUNTER, 100 de limite - configuração do Altar comum
     * (Bloco A). Subclasses (Blood/Moon/Abyss Altar) sobrescrevem
     * {@link #allowedCategories()}/{@link #ritualPowerMax()}, nunca os
     * campos - assim o resto do motor ({@link RitualManager}) não
     * precisa saber qual altar concreto está rodando.
     */
    protected Set<RitualCategory> allowedCategories() {
        return EnumSet.of(RitualCategory.GENERAL, RitualCategory.HUNTER);
    }

    protected int ritualPowerMax() {
        return 100;
    }

    /**
     * Verificação de círculo (Bloco A/#17) - por padrão, chalk desenhado
     * no perímetro (Altar comum/Blood Altar). Moon Altar sobrescreve pra
     * usar o próprio anel de pedra da estrutura; Abyss Altar sobrescreve
     * pra usar a flag {@code linked} do multibloco. Nenhum dos dois usa
     * {@link com.soldierskull.eclipse.ritual.RitualCircleManager} com
     * chalk.
     */
    public boolean isCircleValid(World world, BlockPos pos, Ritual ritual) {
        return RitualCircleManager.isCircleValid(world, pos, ritual.getCircleSize(), ritual.getCategory());
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide || !(this.level instanceof ServerWorld)) {
            return;
        }
        RitualManager.tick(this, (ServerWorld) this.level);
    }

    public Set<RitualCategory> getAllowedCategories() {
        return this.allowedCategories();
    }

    public int getRitualPowerMax() {
        return this.ritualPowerMax();
    }

    public int getRitualPower() {
        return this.ritualPower;
    }

    public void addRitualPower(int amount) {
        this.ritualPower = Math.min(this.ritualPowerMax(), this.ritualPower + amount);
        this.setChanged();
    }

    public void removeRitualPower(int amount) {
        this.ritualPower = Math.max(0, this.ritualPower - amount);
        this.setChanged();
    }

    public RitualState getState() {
        return this.state;
    }

    public void setState(RitualState state) {
        this.state = state;
        this.setChanged();
    }

    public int getEvaluateCooldown() {
        return this.evaluateCooldown;
    }

    public void setEvaluateCooldown(int evaluateCooldown) {
        this.evaluateCooldown = evaluateCooldown;
    }

    public static int getEvaluateIntervalTicks() {
        return EVALUATE_INTERVAL_TICKS;
    }

    @Nullable
    public Ritual getCandidateRitual() {
        return this.candidateRitual;
    }

    public void setCandidateRitual(@Nullable Ritual candidateRitual) {
        this.candidateRitual = candidateRitual;
    }

    @Nullable
    public RitualContext getActiveContext() {
        return this.activeContext;
    }

    public void setActiveContext(@Nullable RitualContext activeContext) {
        this.activeContext = activeContext;
    }

    public ItemStack getPrimaryIngredient() {
        return this.primaryIngredient;
    }

    public void setPrimaryIngredient(ItemStack primaryIngredient) {
        this.primaryIngredient = primaryIngredient;
        this.setChanged();
    }

    public int getTransientTimer() {
        return this.transientTimer;
    }

    public void setTransientTimer(int transientTimer) {
        this.transientTimer = transientTimer;
    }

    @Nullable
    public ITextComponent getLastMessage() {
        return this.lastMessage;
    }

    public void setLastMessage(@Nullable ITextComponent lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.putString("State", this.state.name());
        tag.putInt("RitualPower", this.ritualPower);
        tag.put("PrimaryIngredient", this.primaryIngredient.save(new CompoundNBT()));
        if (this.candidateRitual != null) {
            tag.putString("CandidateRitual", this.candidateRitual.getId().toString());
        }
        if (this.activeContext != null) {
            tag.putInt("ProgressTicks", this.activeContext.getProgressTicks());
            if (this.activeContext.getInitiator() != null) {
                tag.putUUID("Initiator", this.activeContext.getInitiator());
            }
        }
        tag.putInt("TransientTimer", this.transientTimer);
        return tag;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT tag) {
        super.load(blockState, tag);
        this.state = RitualState.valueOf(tag.getString("State").isEmpty() ? "INACTIVE" : tag.getString("State"));
        this.ritualPower = tag.getInt("RitualPower");
        this.primaryIngredient = ItemStack.of(tag.getCompound("PrimaryIngredient"));
        this.transientTimer = tag.getInt("TransientTimer");

        Ritual restoredRitual = null;
        if (tag.contains("CandidateRitual")) {
            restoredRitual = RitualRegistry.get(new ResourceLocation(tag.getString("CandidateRitual")));
            this.candidateRitual = restoredRitual;
        }

        // Se o ritual estava RUNNING quando o mundo salvou, reconstrói o
        // contexto (o mundo real / ServerWorld só existe depois que o
        // TileEntity é anexado - por isso isso não pode ser feito aqui
        // dentro; RitualManager.tick() detecta "RUNNING sem activeContext"
        // e reconstrói na primeira tick, usando os campos abaixo).
        if (this.state == RitualState.RUNNING && restoredRitual != null && tag.contains("ProgressTicks")) {
            this.pendingRestoreProgressTicks = tag.getInt("ProgressTicks");
            this.pendingRestoreInitiator = tag.hasUUID("Initiator") ? tag.getUUID("Initiator") : null;
        }
    }

    // Campos-ponte só usados entre load() e a primeira tick() em RitualManager
    // (não podem virar um RitualContext direto porque load() roda antes do
    // TileEntity ter um ServerWorld anexado via getLevel()).
    private int pendingRestoreProgressTicks = -1;
    @Nullable
    private UUID pendingRestoreInitiator = null;

    public boolean hasPendingRestore() {
        return this.pendingRestoreProgressTicks >= 0;
    }

    public int consumePendingRestoreProgressTicks() {
        int value = this.pendingRestoreProgressTicks;
        this.pendingRestoreProgressTicks = -1;
        return value;
    }

    @Nullable
    public UUID getPendingRestoreInitiator() {
        return this.pendingRestoreInitiator;
    }
}
