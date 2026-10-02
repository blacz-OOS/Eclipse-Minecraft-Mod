package com.soldierskull.eclipse.entity.supernatural.aetherparasite;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalOrigin;
import com.soldierskull.eclipse.entity.supernatural.SupernaturalRank;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Parasita de Éter.
 *
 * Comportamento:
 * - Voa livremente quando não encontra jogadores.
 * - Procura jogadores em um raio de 10 blocos.
 * - Persegue o jogador quando o encontra.
 * - Ao chegar a 1,5 bloco, tenta se fixar.
 * - Cada jogador pode ter no máximo 3 parasitas fixados simultaneamente.
 * - Cada parasita fixado causa 0,5 HP de dano por segundo.
 * - O parasita permanece preso até ser acertado (não precisa matá-lo -
 *   qualquer golpe que ele sobreviva já solta e faz fugir; um golpe
 *   letal remove normalmente via die()).
 *
 * ATUALIZADO (animação, item 3.6 da spec): antes só soltava ao MORRER.
 * Agora um golpe não-letal também solta e dispara uma fuga rápida
 * ("corpo contrai; solta; recua rapidamente; volta ao voo"). ATTACHED e
 * RECOIL_TICKS sincronizados via SynchedEntityData pro AetherParasiteModel
 * animar corretamente no cliente.
 */
public class AetherParasiteEntity extends MonsterEntity implements ISupernaturalMob {

    private static final int MAX_ATTACHED_PER_PLAYER = 3;
    private static final int RECOIL_DURATION = 6; // ~0.3s de fuga rapida apos ser acertado

    private static final DataParameter<Boolean> ATTACHED =
            EntityDataManager.defineId(AetherParasiteEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> RECOIL_TICKS =
            EntityDataManager.defineId(AetherParasiteEntity.class, DataSerializers.INT);

    /**
     * Jogador -> quantidade de parasitas atualmente presos nele.
     */
    private static final Map<UUID, Integer> ATTACHED_COUNT =
            new ConcurrentHashMap<>();

    /**
     * Jogador ao qual este parasita está atualmente preso.
     */
    private PlayerEntity attachedTo = null;

    /**
     * Controla o intervalo entre mudanças de destino durante o voo aleatório.
     */
    private int randomFlightTimer = 0;

    public AetherParasiteEntity(
            EntityType<? extends MonsterEntity> type,
            World world
    ) {
        super(type, world);

        this.setNoGravity(true);

        this.moveControl = new FlyingMovementController(
                this,
                20,
                true
        );
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACHED, false);
        this.entityData.define(RECOIL_TICKS, 0);
    }

    /**
     * Atributos do Parasita de Éter.
     */
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FLYING_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    /**
     * Goals utilizados pela entidade.
     *
     * Não utilizamos RandomFlyingGoal porque essa Goal não existe
     * na API utilizada pelo Forge 1.16.5 deste projeto.
     *
     * O voo é controlado manualmente pelo método aiStep().
     */
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(
                5,
                new LookRandomlyGoal(
                        this
                )
        );
    }

    /**
     * Lógica principal da entidade.
     */
    @Override
    public void aiStep() {
        super.aiStep();

        /*
         * A lógica de movimentação e dano deve ser executada
         * somente no servidor.
         */
        if (this.level.isClientSide) {
            return;
        }

        if (this.entityData.get(RECOIL_TICKS) > 0) {
            this.entityData.set(RECOIL_TICKS, this.entityData.get(RECOIL_TICKS) - 1);
            // durante o recoil, afasta rapidamente do ultimo ponto conhecido (ja sem attachedTo)
            this.setDeltaMovement(this.getDeltaMovement().scale(1.0D).add(0, 0.02D, 0));
        }

        /*
         * =========================================================
         * PARASITA PRESO AO JOGADOR
         * =========================================================
         */

        if (attachedTo != null) {

            /*
             * Se o jogador morreu, o parasita se solta.
             */
            if (attachedTo.isDeadOrDying()) {
                detach();
                return;
            }

            /*
             * Mantém o parasita fisicamente sobre o jogador.
             */
            Vector3d targetPosition = attachedTo.position().add(
                    0.0D,
                    attachedTo.getBbHeight() * 0.6D,
                    0.0D
            );

            this.setPos(
                    targetPosition.x,
                    targetPosition.y,
                    targetPosition.z
            );

            /*
             * Causa 0,5 HP de dano a cada segundo.
             *
             * Minecraft executa 20 ticks por segundo.
             */
            if (this.tickCount % 20 == 0) {
                attachedTo.hurt(
                        DamageSource.mobAttack(this),
                        0.5F
                );
            }

            /*
             * Enquanto estiver preso, não executa a lógica
             * normal de perseguição.
             */
            return;
        }

        /*
         * =========================================================
         * PROCURA POR JOGADORES
         * =========================================================
         */

        PlayerEntity nearestPlayer =
                this.level.getNearestPlayer(this, 10);

        /*
         * =========================================================
         * JOGADOR ENCONTRADO
         * =========================================================
         */

        if (nearestPlayer != null
                && !nearestPlayer.isSpectator()
                && !nearestPlayer.isCreative()) {

            double distanceSquared =
                    this.distanceToSqr(nearestPlayer);

            /*
             * Se estiver suficientemente próximo,
             * tenta se fixar.
             */
            if (distanceSquared < 1.5D * 1.5D) {

                tryAttach(nearestPlayer);

            } else {

                /*
                 * Continua voando em direção ao jogador.
                 */
                this.getMoveControl().setWantedPosition(
                        nearestPlayer.getX(),
                        nearestPlayer.getY() + 1.0D,
                        nearestPlayer.getZ(),
                        1.0D
                );

                /*
                 * Reinicia o contador do voo aleatório,
                 * pois agora o destino é o jogador.
                 */
                randomFlightTimer = 0;
            }

            return;
        }

        /*
         * =========================================================
         * NENHUM JOGADOR ENCONTRADO
         * =========================================================
         *
         * O parasita passa a voar aleatoriamente.
         */
        randomFlight();
    }

    /**
     * Faz o parasita escolher periodicamente uma nova posição
     * aleatória para voar.
     */
    private void randomFlight() {

        randomFlightTimer++;

        /*
         * Escolhe um novo destino aproximadamente a cada
         * 30 ticks = 1,5 segundo.
         */
        if (randomFlightTimer < 30) {
            return;
        }

        randomFlightTimer = 0;

        /*
         * Distância horizontal máxima aproximada:
         * 4 blocos para cada lado.
         */
        double targetX =
                this.getX()
                        + (this.random.nextDouble() - 0.5D) * 8.0D;

        /*
         * Distância vertical máxima aproximada:
         * 2,5 blocos para cada lado.
         */
        double targetY =
                this.getY()
                        + (this.random.nextDouble() - 0.5D) * 5.0D;

        /*
         * Distância horizontal máxima aproximada:
         * 4 blocos para cada lado.
         */
        double targetZ =
                this.getZ()
                        + (this.random.nextDouble() - 0.5D) * 8.0D;

        /*
         * Mantém a entidade dentro dos limites verticais
         * normais do mundo.
         */
        targetY = Math.max(
                1.0D,
                Math.min(targetY, 255.0D)
        );

        /*
         * Define o novo destino.
         */
        this.getMoveControl().setWantedPosition(
                targetX,
                targetY,
                targetZ,
                0.8D
        );
    }

    /**
     * Tenta prender o parasita ao jogador.
     */
    private void tryAttach(PlayerEntity player) {

        UUID playerUUID = player.getUUID();

        int currentCount =
                ATTACHED_COUNT.getOrDefault(
                        playerUUID,
                        0
                );

        /*
         * Limite máximo de 3 parasitas por jogador.
         */
        if (currentCount >= MAX_ATTACHED_PER_PLAYER) {
            return;
        }

        /*
         * Registra mais um parasita preso.
         */
        ATTACHED_COUNT.put(
                playerUUID,
                currentCount + 1
        );

        /*
         * Registra o jogador como alvo deste parasita.
         */
        this.attachedTo = player;
        this.entityData.set(ATTACHED, true);

        /*
         * Garante que o parasita continue sem gravidade.
         */
        this.setNoGravity(true);
    }

    /**
     * Remove este parasita da contagem do jogador
     * ao qual estava preso.
     */
    private void detach() {

        if (attachedTo != null) {

            UUID playerUUID = attachedTo.getUUID();

            int currentCount =
                    ATTACHED_COUNT.getOrDefault(
                            playerUUID,
                            1
                    );

            int newCount =
                    Math.max(
                            0,
                            currentCount - 1
                    );

            if (newCount == 0) {

                /*
                 * Remove a entrada quando não há mais
                 * parasitas presos ao jogador.
                 */
                ATTACHED_COUNT.remove(playerUUID);

            } else {

                ATTACHED_COUNT.put(
                        playerUUID,
                        newCount
                );
            }
        }

        attachedTo = null;
        this.entityData.set(ATTACHED, false);
    }

    /**
     * Recebe dano normalmente. Se estiver preso e SOBREVIVER ao golpe
     * (item 3.6 da spec de animação: "corpo contrai; solta o jogador;
     * recua rapidamente; volta ao voo"), solta na hora - não precisa
     * matar o parasita pra ele se soltar, so acerta-lo.
     */
    @Override
    public boolean hurt(
            DamageSource source,
            float amount
    ) {
        boolean wasAttached = attachedTo != null;
        boolean result = super.hurt(source, amount);
        if (result && wasAttached && this.isAlive()) {
            detach();
            this.entityData.set(RECOIL_TICKS, RECOIL_DURATION);
        }
        return result;
    }

    // ---------- Consultado pelo AetherParasiteModel (cliente) ----------

    public boolean isAttached() {
        return this.entityData.get(ATTACHED);
    }

    public boolean isRecoiling() {
        return this.entityData.get(RECOIL_TICKS) > 0;
    }

    public int getDeathTime() {
        return this.deathTime;
    }

    /**
     * Quando o parasita morre, libera sua vaga no contador
     * do jogador.
     */
    @Override
    public void die(DamageSource source) {

        detach();

        super.die(source);
    }

    /**
     * Garante que o contador seja liberado durante a morte
     * da entidade.
     */
    @Override
    protected void tickDeath() {

        detach();

        super.tickDeath();
    }

    /**
     * Origem sobrenatural da entidade.
     */
    @Override
    public SupernaturalOrigin getSupernaturalOrigin() {
        return SupernaturalOrigin.ABYSSAL;
    }

    /**
     * Rank sobrenatural da entidade.
     */
    @Override
    public SupernaturalRank getSupernaturalRank() {
        return SupernaturalRank.TRIVIAL;
    }
}