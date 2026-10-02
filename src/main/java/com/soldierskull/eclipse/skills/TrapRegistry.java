package com.soldierskull.eclipse.skills;

import java.util.ArrayList;
import java.util.List;

import com.soldierskull.eclipse.block.ModBlocks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

/**
 * "Armadilha de Caçador" / "Armadilha de Prata": agora um bloco de
 * verdade no mundo (modelo enviado pelo usuário - antes só partículas
 * de redstone), que atrai criaturas sobrenaturais num raio de 10
 * blocos e, quando uma chega perto o bastante, aplica Lentidão
 * extrema por 5s e a armadilha se desativa - o bloco é removido do
 * mundo no mesmo momento (mesma regra da spec original: "a armadilha é
 * ativada e deixa de funcionar"). O bloco é só a representação visual;
 * toda a lógica de atração/gatilho continua aqui, baseada em raio, não
 * em colisão com o bloco.
 *
 * Checado uma vez por segundo (mesmo tick de 1s do resto do sistema de
 * skills) - não precisa de precisão de tick a tick pra isso.
 */
public final class TrapRegistry {

    private static final class Trap {
        final ServerWorld world;
        final Vector3d center;
        final BlockPos blockPos;
        final long expireAtMillis;
        boolean triggered = false;

        Trap(ServerWorld world, Vector3d center, BlockPos blockPos, long expireAtMillis) {
            this.world = world;
            this.center = center;
            this.blockPos = blockPos;
            this.expireAtMillis = expireAtMillis;
        }
    }

    private static final List<Trap> ACTIVE = new ArrayList<>();
    private static final RedstoneParticleData RED = new RedstoneParticleData(1.0F, 0.0F, 0.0F, 1.2F);

    private TrapRegistry() {
    }

    /** Coloca o bloco de verdade em {@code pos} (se o espaço estiver livre) e registra a armadilha. Retorna false se o espaço não era válido - nada é colocado nem registrado nesse caso. */
    public static boolean place(ServerWorld world, BlockPos pos, long durationMillis) {
        if (!world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }
        world.setBlock(pos, ModBlocks.TRAP.get().defaultBlockState(), 3);
        Vector3d center = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.05D, pos.getZ() + 0.5D);
        ACTIVE.add(new Trap(world, center, pos, System.currentTimeMillis() + durationMillis));
        return true;
    }

    public static void tick() {
        if (ACTIVE.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        List<Trap> expired = new ArrayList<>();

        for (Trap trap : ACTIVE) {
            if (trap.triggered || now >= trap.expireAtMillis) {
                expired.add(trap);
                continue;
            }

            renderSquare(trap);

            List<LivingEntity> nearby = trap.world.getEntitiesOfClass(LivingEntity.class,
                    new AxisAlignedBB(trap.center.x - 10, trap.center.y - 3, trap.center.z - 10,
                            trap.center.x + 10, trap.center.y + 3, trap.center.z + 10),
                    e -> e.isAlive() && SupernaturalEntities.isSupernatural(e));

            for (LivingEntity target : nearby) {
                Vector3d toCenter = trap.center.subtract(target.position()).normalize();
                double distSq = target.position().distanceToSqr(trap.center);

                if (distSq <= 1.5D * 1.5D) {
                    target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 100, 9)); // "lentidao extrema" por 5s
                    trap.triggered = true;
                    net.minecraft.util.text.StringTextComponent msg = new net.minecraft.util.text.StringTextComponent(
                            "Uma armadilha disparou em " + target.getName().getString() + "!");
                    for (net.minecraft.entity.player.ServerPlayerEntity nearbyPlayer : trap.world.getEntitiesOfClass(
                            net.minecraft.entity.player.ServerPlayerEntity.class,
                            new AxisAlignedBB(trap.center.x - 20, trap.center.y - 10, trap.center.z - 20,
                                    trap.center.x + 20, trap.center.y + 10, trap.center.z + 20))) {
                        nearbyPlayer.displayClientMessage(msg, false);
                    }
                } else {
                    // "atrai": pequeno empurrao na direcao do centro a cada segundo
                    target.setDeltaMovement(target.getDeltaMovement().add(toCenter.x * 0.15D, 0.0D, toCenter.z * 0.15D));
                    target.hurtMarked = true;
                }
            }
        }

        for (Trap trap : expired) {
            if (trap.world.getBlockState(trap.blockPos).getBlock() == ModBlocks.TRAP.get()) {
                trap.world.removeBlock(trap.blockPos, false);
            }
        }
        ACTIVE.removeAll(expired);
    }

    private static void renderSquare(Trap trap) {
        double half = 0.6D;
        double y = trap.center.y;
        for (double dx = -half; dx <= half; dx += 0.3D) {
            spawn(trap.world, trap.center.x + dx, y, trap.center.z - half);
            spawn(trap.world, trap.center.x + dx, y, trap.center.z + half);
        }
        for (double dz = -half; dz <= half; dz += 0.3D) {
            spawn(trap.world, trap.center.x - half, y, trap.center.z + dz);
            spawn(trap.world, trap.center.x + half, y, trap.center.z + dz);
        }
    }

    private static void spawn(ServerWorld world, double x, double y, double z) {
        world.sendParticles(RED, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }
}
