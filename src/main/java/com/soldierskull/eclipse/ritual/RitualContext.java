package com.soldierskull.eclipse.ritual;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import com.soldierskull.eclipse.ritual.altar.AltarTileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

/**
 * Representa "o que está acontecendo neste ritual específico" - a
 * execução real, separada da definição ({@link Ritual}). Um
 * {@code RitualContext} nasce quando o altar entra em RUNNING e morre
 * quando o ritual termina (COMPLETED ou FAILED) - decisão confirmada,
 * não é persistido além do necessário pro altar sobreviver a um
 * restart do servidor (isso fica a cargo do
 * {@code AltarTileEntity}, que salva só os campos primitivos: ritual id,
 * progressTicks, initiator UUID).
 *
 * Não guarda referência de {@code ServerPlayerEntity} diretamente (o
 * jogador pode desconectar no meio do ritual) - guarda o UUID e resolve
 * via {@code world.getServer().getPlayerList()} quando precisar, e trata
 * jogador ausente como falha se o ritual exigir presença
 * ({@link Ritual#requiresInitiatorNearby()}).
 */
public class RitualContext {

    private final ServerWorld world;
    private final BlockPos altarPos;
    private final AltarTileEntity altar;
    private final Ritual ritual;
    @Nullable
    private final UUID initiator;

    private int progressTicks;
    private int ritualPowerReserved;
    @Nullable
    private ItemStack primaryIngredientHeld;
    private final List<UUID> consumedOfferingEntities = new ArrayList<>();

    public RitualContext(ServerWorld world, BlockPos altarPos, AltarTileEntity altar, Ritual ritual, @Nullable UUID initiator) {
        this.world = world;
        this.altarPos = altarPos;
        this.altar = altar;
        this.ritual = ritual;
        this.initiator = initiator;
        this.progressTicks = 0;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    public BlockPos getAltarPos() {
        return this.altarPos;
    }

    /** O altar que está executando este ritual - efeitos usam isso pra mexer no Ritual Power (ver AddRitualPowerEffect). */
    public AltarTileEntity getAltar() {
        return this.altar;
    }

    public Ritual getRitual() {
        return this.ritual;
    }

    @Nullable
    public UUID getInitiator() {
        return this.initiator;
    }

    public int getProgressTicks() {
        return this.progressTicks;
    }

    public void tickProgress() {
        this.progressTicks++;
    }

    /** Usado só ao reconstruir o contexto a partir do NBT salvo (altar sobrevivendo a um restart do servidor). */
    public void setProgressTicks(int progressTicks) {
        this.progressTicks = progressTicks;
    }

    /** Progresso normalizado 0.0-1.0, útil pra overlay e pra decidir backlash em falha. */
    public float getProgressFraction() {
        if (this.ritual.getDurationTicks() <= 0) {
            return 1.0F;
        }
        return Math.min(1.0F, (float) this.progressTicks / (float) this.ritual.getDurationTicks());
    }

    public boolean isDurationComplete() {
        return this.progressTicks >= this.ritual.getDurationTicks();
    }

    public int getRitualPowerReserved() {
        return this.ritualPowerReserved;
    }

    public void setRitualPowerReserved(int ritualPowerReserved) {
        this.ritualPowerReserved = ritualPowerReserved;
    }

    @Nullable
    public ItemStack getPrimaryIngredientHeld() {
        return this.primaryIngredientHeld;
    }

    public void setPrimaryIngredientHeld(@Nullable ItemStack primaryIngredientHeld) {
        this.primaryIngredientHeld = primaryIngredientHeld;
    }

    public List<UUID> getConsumedOfferingEntities() {
        return this.consumedOfferingEntities;
    }
}
