package com.soldierskull.eclipse.entity.supernatural.voidobserver;

import com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * Esfera de energia do Vazio Observador.
 *
 * Características:
 * - Causa 4.0 de dano.
 * - Aplica knockback forte.
 * - Usa Ender Pearl como representação visual temporária.
 * - Desaparece ao atingir uma entidade ou bloco.
 *
 * Compatível com Minecraft Forge 1.16.5.
 */
public class VoidBoltEntity extends ThrowableEntity implements IRendersAsItem {

    /**
     * Item usado pelo renderer vanilla para representar visualmente
     * o projétil.
     *
     * Placeholder temporário até existir uma textura/modelo próprio.
     */
    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.ENDER_PEARL);
    }

    /**
     * Construtor usado pelo registro da entidade.
     */
    public VoidBoltEntity(
            EntityType<? extends VoidBoltEntity> type,
            World world
    ) {
        super(type, world);
    }

    /**
     * Construtor usado quando o Vazio Observador dispara o projétil.
     */
    public VoidBoltEntity(
            World world,
            LivingEntity shooter
    ) {
        super(
                ModSupernaturalEntities.VOID_BOLT.get(),
                shooter,
                world
        );
    }

    /**
     * Processa o impacto contra uma entidade.
     */
    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);

        // O dano e o knockback devem ser processados apenas no servidor.
        if (this.level.isClientSide) {
            return;
        }

        if (!(result.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity target = (LivingEntity) result.getEntity();

        /*
         * Obtém o responsável pelo disparo.
         *
         * O método indirectMobAttack da versão 1.16.5 aceita:
         *
         *     Entity projectile
         *     LivingEntity owner
         */
        if (this.getOwner() instanceof LivingEntity) {

            LivingEntity owner =
                    (LivingEntity) this.getOwner();

            target.hurt(
                    DamageSource.indirectMobAttack(
                            this,
                            owner
                    ),
                    4.0F
            );

        } else {

            /*
             * Fallback caso o projétil não possua um dono válido.
             */
            target.hurt(
                    DamageSource.GENERIC,
                    4.0F
            );
        }

        /*
         * ========================================================
         * KNOCKBACK
         * ========================================================
         */

        double dx =
                target.getX() - this.getX();

        double dz =
                target.getZ() - this.getZ();

        double distance =
                Math.sqrt(
                        dx * dx +
                                dz * dz
                );

        /*
         * Normaliza a direção para que o knockback tenha
         * intensidade consistente.
         */
        if (distance > 0.0001D) {

            dx /= distance;
            dz /= distance;

            target.knockback(
                    1.4F,
                    dx,
                    dz
            );
        }

        /*
         * O projétil desaparece após atingir o alvo.
         */
        this.remove();
    }

    /**
     * Processa impacto contra blocos ou outras superfícies.
     */
    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);

        if (this.level.isClientSide) {
            return;
        }

        /*
         * Se o impacto não foi contra uma entidade, o projétil
         * desaparece.
         */
        if (result.getType() != RayTraceResult.Type.ENTITY) {
            this.remove();
        }
    }

    /**
     * O Void Bolt não possui dados sincronizados próprios.
     */
    @Override
    protected void defineSynchedData() {
    }
}