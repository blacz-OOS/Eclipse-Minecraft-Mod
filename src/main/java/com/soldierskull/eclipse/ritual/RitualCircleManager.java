package com.soldierskull.eclipse.ritual;

import com.soldierskull.eclipse.block.RitualChalkBlock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Detecta se existe um círculo válido ao redor de um centro (o altar) -
 * NÃO é uma estrutura salva em NBT (Bloco C/#17, decisão confirmada em
 * toda a especificação): o mundo é varrido sob demanda. Só cobre o caso
 * "círculo desenhado com chalk" (Altar comum / Blood Altar); Moon Altar
 * e Abyss Altar usam a própria estrutura deles como círculo e terão sua
 * própria verificação quando forem implementados (Etapa 9).
 */
public final class RitualCircleManager {

    private RitualCircleManager() {
    }

    /**
     * Testa o perímetro (borda do quadrado, não o interior) na mesma
     * altura Y do centro. Todo bloco do perímetro precisa ser um
     * {@link RitualChalkBlock} que suporte a categoria pedida.
     */
    public static boolean isCircleValid(World world, BlockPos center, RitualCircleSize size, RitualCategory category) {
        int radius = size.getRadius();
        BlockPos.Mutable cursor = new BlockPos.Mutable();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                boolean onPerimeter = Math.max(Math.abs(dx), Math.abs(dz)) == radius;
                if (!onPerimeter) {
                    continue;
                }
                cursor.set(center.getX() + dx, center.getY(), center.getZ() + dz);
                if (!(world.getBlockState(cursor).getBlock() instanceof RitualChalkBlock)) {
                    return false;
                }
                RitualChalkBlock chalk = (RitualChalkBlock) world.getBlockState(cursor).getBlock();
                if (!chalk.supports(category)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Variante usada pelo Moon Altar (Bloco A/D) - o círculo é o próprio
     * anel de pedra da estrutura, não chalk, e não depende da
     * categoria do ritual (a estrutura já é fixa, sempre a mesma pra
     * qualquer ritual GENERAL/LUNAR/WEREWOLF).
     */
    public static boolean isNaturalRingValid(World world, BlockPos center, RitualCircleSize size, net.minecraft.block.Block ringBlock) {
        int radius = size.getRadius();
        BlockPos.Mutable cursor = new BlockPos.Mutable();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                boolean onPerimeter = Math.max(Math.abs(dx), Math.abs(dz)) == radius;
                if (!onPerimeter) {
                    continue;
                }
                cursor.set(center.getX() + dx, center.getY(), center.getZ() + dz);
                if (world.getBlockState(cursor).getBlock() != ringBlock) {
                    return false;
                }
            }
        }
        return true;
    }
}
