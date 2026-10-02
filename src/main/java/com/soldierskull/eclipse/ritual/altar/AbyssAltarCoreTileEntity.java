package com.soldierskull.eclipse.ritual.altar;

import java.util.EnumSet;
import java.util.Set;

import com.soldierskull.eclipse.block.ModBlocks;
import com.soldierskull.eclipse.ritual.Ritual;
import com.soldierskull.eclipse.ritual.RitualCategory;
import com.soldierskull.eclipse.ritual.RitualManager;
import com.soldierskull.eclipse.ritual.RitualState;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * Núcleo do Abyss Altar (Bloco D) - multibloco 9×9, binário (Bloco
 * D/decisão confirmada: ou a estrutura está 100% completa, ou não
 * funciona nada). O "círculo" deste altar é a própria estrutura, não
 * chalk nem anel de pedra - {@link #isCircleValid} simplesmente
 * devolve a flag {@code linked}, calculada periodicamente em
 * {@link #tick()} (não todo tick - a estrutura não muda com
 * frequência, então o custo do scan de 81 blocos é amortizado).
 */
public class AbyssAltarCoreTileEntity extends AltarTileEntity {

    private static final int LINK_CHECK_INTERVAL_TICKS = 40; // 2s
    // Offsets (dx, dz) relativos ao core, no mesmo Y - ver leiaute no
    // documento de especificação (Bloco D).
    private static final int[][] PILLAR_OFFSETS = {{-3, -3}, {3, -3}, {-3, 3}, {3, 3}};
    private static final int[][] NODE_OFFSETS = {{0, -3}, {-3, 0}, {3, 0}, {0, 3}};
    private static final int[][] RUNE_OFFSETS = {{-2, -2}, {2, -2}, {-2, 2}, {2, 2}};
    private static final int PILLAR_HEIGHT = 4;

    private boolean linked = false;
    private int linkCheckCooldown = 0;

    public AbyssAltarCoreTileEntity() {
        super(ModTileEntities.ABYSS_ALTAR_CORE.get());
    }

    @Override
    protected Set<RitualCategory> allowedCategories() {
        return EnumSet.of(RitualCategory.GENERAL, RitualCategory.ABYSSAL, RitualCategory.ECLIPSE,
                RitualCategory.ENDGAME, RitualCategory.CULTIST);
    }

    @Override
    protected int ritualPowerMax() {
        return 1000;
    }

    @Override
    public boolean isCircleValid(World world, BlockPos pos, Ritual ritual) {
        return this.linked;
    }

    public boolean isLinked() {
        return this.linked;
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide || !(this.level instanceof ServerWorld)) {
            return;
        }
        if (this.linkCheckCooldown <= 0) {
            this.linkCheckCooldown = LINK_CHECK_INTERVAL_TICKS;
            this.linked = checkStructure(this.level, this.getBlockPos());
            if (!this.linked && this.getState() != RitualState.RUNNING) {
                this.setLastMessage(new StringTextComponent("Estrutura do Abyss Altar incompleta"));
            }
        } else {
            this.linkCheckCooldown--;
        }
        RitualManager.tick(this, (ServerWorld) this.level);
    }

    /** Scan binário dos 9x9 - qualquer peça fora do lugar já invalida a estrutura inteira. */
    private static boolean checkStructure(World world, BlockPos core) {
        for (int[] offset : PILLAR_OFFSETS) {
            for (int height = 0; height < PILLAR_HEIGHT; height++) {
                BlockPos pos = core.offset(offset[0], height, offset[1]);
                if (!isBlock(world, pos, ModBlocks.ABYSSAL_PILLAR.get())) {
                    return false;
                }
            }
        }
        for (int[] offset : NODE_OFFSETS) {
            BlockPos pos = core.offset(offset[0], 0, offset[1]);
            if (!isBlock(world, pos, ModBlocks.CHARGED_ABYSS_CRYSTAL.get())) {
                return false;
            }
        }
        for (int[] offset : RUNE_OFFSETS) {
            BlockPos pos = core.offset(offset[0], 0, offset[1]);
            if (!isBlock(world, pos, ModBlocks.ABYSSAL_RUNE.get())) {
                return false;
            }
        }
        return checkFoundation(world, core);
    }

    /** O piso 9x9 inteiro precisa ser a fundação, exceto onde há core/pilar/nó/rune. */
    private static boolean checkFoundation(World world, BlockPos core) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (dx == 0 && dz == 0) {
                    continue; // o próprio core
                }
                if (isSpecialCell(dx, dz)) {
                    continue; // pilar/nó/rune, já verificado acima
                }
                if (!isBlock(world, core.offset(dx, 0, dz), ModBlocks.ABYSSAL_RUIN_FOUNDATION.get())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isSpecialCell(int dx, int dz) {
        for (int[] offset : PILLAR_OFFSETS) {
            if (offset[0] == dx && offset[1] == dz) {
                return true;
            }
        }
        for (int[] offset : NODE_OFFSETS) {
            if (offset[0] == dx && offset[1] == dz) {
                return true;
            }
        }
        for (int[] offset : RUNE_OFFSETS) {
            if (offset[0] == dx && offset[1] == dz) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBlock(World world, BlockPos pos, Block expected) {
        return world.getBlockState(pos).getBlock() == expected;
    }
}
