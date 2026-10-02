package com.soldierskull.eclipse.entity.supernatural.mapinguari;

import com.soldierskull.eclipse.Eclipse;
import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Controla o surgimento especial do Mapinguari.
 *
 * Regras:
 *
 * - O jogador precisa estar em um bioma da categoria FOREST.
 * - Apenas mortes de MonsterEntity ou ISupernaturalMob contam.
 * - Animais passivos, villagers e outros seres não contam.
 * - O contador é separado por bioma.
 * - A cada 50 mortes existe 45% de chance de iniciar o surgimento.
 * - Quando o surgimento é bem-sucedido, o contador volta para 0.
 * - O Mapinguari surge após aproximadamente 3 segundos.
 * - A posição de surgimento fica entre 12 e 20 blocos do jogador.
 *
 * Estado persistente:
 *
 * EclipseMapinguari
 * ├── ForestBiome : String
 * └── KillCount   : int
 */
@Mod.EventBusSubscriber(modid = Eclipse.MOD_ID)
public class MapinguariSpawnHandler {

    /*
     * ============================================================
     * CONFIGURAÇÃO
     * ============================================================
     */

    /**
     * Quantidade de mortes necessárias para uma tentativa.
     */
    private static final int KILLS_REQUIRED = 50;

    /**
     * Chance de surgimento após atingir o número necessário
     * de mortes.
     */
    private static final float SPAWN_CHANCE = 0.45F;

    /**
     * Chave principal usada no PersistentData do jogador.
     */
    private static final String NBT_KEY = "EclipseMapinguari";

    /**
     * Nome do campo que armazena o bioma.
     */
    private static final String NBT_FOREST_BIOME = "ForestBiome";

    /**
     * Nome do campo que armazena a quantidade de mortes.
     */
    private static final String NBT_KILL_COUNT = "KillCount";

    /**
     * Distância mínima do jogador para o surgimento.
     */
    private static final double MIN_SPAWN_DISTANCE = 12.0D;

    /**
     * Distância máxima do jogador para o surgimento.
     */
    private static final double MAX_SPAWN_DISTANCE = 20.0D;

    /**
     * Tempo de espera antes do surgimento.
     *
     * 60 ticks = 3 segundos.
     */
    private static final int SPAWN_DELAY_TICKS = 60;


    /*
     * ============================================================
     * SPAWNS PENDENTES
     * ============================================================
     */

    /**
     * Guarda os spawns que estão esperando o tempo de materialização.
     *
     * UUID do jogador -> spawn pendente.
     */
    private static final Map<UUID, PendingSpawn> PENDING =
            new HashMap<>();


    /**
     * Representa um Mapinguari que está aguardando para surgir.
     */
    private static class PendingSpawn {

        final ServerWorld world;
        final BlockPos pos;
        int delayTicks;

        PendingSpawn(
                ServerWorld world,
                BlockPos pos,
                int delayTicks
        ) {
            this.world = world;
            this.pos = pos;
            this.delayTicks = delayTicks;
        }
    }


    /*
     * ============================================================
     * VERIFICAÇÃO DO BIOMA DO JOGADOR
     * ============================================================
     */

    /**
     * Verifica periodicamente se o jogador ainda está
     * no mesmo bioma de floresta em que começou a contagem.
     *
     * Caso saia do bioma:
     *
     * ForestBiome = ""
     * KillCount = 0
     */
    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {

        /*
         * Só executa no final do tick.
         */
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        /*
         * Não executa no cliente.
         */
        if (event.player.level.isClientSide) {
            return;
        }

        /*
         * Não precisamos verificar todo tick.
         *
         * 40 ticks = 2 segundos.
         */
        if (event.player.tickCount % 40 != 0) {
            return;
        }

        PlayerEntity player = event.player;

        /*
         * Obtém os dados persistentes.
         */
        CompoundNBT data =
                player.getPersistentData()
                        .getCompound(NBT_KEY);

        String storedBiome =
                data.getString(NBT_FOREST_BIOME);

        /*
         * Se não existe uma contagem ativa,
         * não há nada para verificar.
         */
        if (storedBiome.isEmpty()) {
            return;
        }

        /*
         * Obtém o bioma atual.
         */
        Biome biome =
                player.level.getBiome(
                        player.blockPosition()
                );

        /*
         * Obtém o ID do bioma.
         */
        String currentBiomeId =
                biome.getRegistryName() == null
                        ? ""
                        : biome.getRegistryName().toString();

        /*
         * Verifica se ainda está em uma floresta
         * e exatamente no mesmo bioma.
         */
        boolean stillInForest =
                biome.getBiomeCategory() == Biome.Category.FOREST
                        && storedBiome.equals(currentBiomeId);

        /*
         * Saiu do bioma válido.
         */
        if (!stillInForest) {

            data.putString(
                    NBT_FOREST_BIOME,
                    ""
            );

            data.putInt(
                    NBT_KILL_COUNT,
                    0
            );

            player.getPersistentData().put(
                    NBT_KEY,
                    data
            );
        }
    }


    /*
     * ============================================================
     * MORTE DE ENTIDADES
     * ============================================================
     */

    /**
     * Conta mortes válidas realizadas pelo jogador.
     */
    @SubscribeEvent
    public static void onLivingDeath(
            LivingDeathEvent event
    ) {

        /*
         * Obtém quem causou a morte.
         */
        Entity killerEntity =
                event.getSource().getEntity();

        /*
         * Apenas jogadores podem gerar a contagem.
         */
        if (!(killerEntity instanceof PlayerEntity)) {
            return;
        }

        /*
         * Apenas servidor.
         */
        if (event.getEntity().level.isClientSide) {
            return;
        }

        PlayerEntity player =
                (PlayerEntity) killerEntity;

        LivingEntity victim =
                event.getEntityLiving();


        /*
         * --------------------------------------------------------
         * Verifica se a entidade morta conta.
         * --------------------------------------------------------
         *
         * Conta:
         *
         * 1. MonsterEntity
         * 2. ISupernaturalMob
         *
         * Não conta:
         *
         * - animais passivos
         * - villagers
         * - jogadores
         * - outras entidades não hostis
         */
        boolean countsAsValidKill =
                victim instanceof MonsterEntity
                        || victim instanceof ISupernaturalMob;

        if (!countsAsValidKill) {
            return;
        }


        /*
         * --------------------------------------------------------
         * Verifica o bioma.
         * --------------------------------------------------------
         */

        Biome biome =
                player.level.getBiome(
                        player.blockPosition()
                );

        if (biome.getBiomeCategory()
                != Biome.Category.FOREST) {

            return;
        }


        /*
         * --------------------------------------------------------
         * Obtém ID do bioma.
         * --------------------------------------------------------
         */

        String biomeId =
                biome.getRegistryName() == null
                        ? ""
                        : biome.getRegistryName().toString();

        if (biomeId.isEmpty()) {
            return;
        }


        /*
         * --------------------------------------------------------
         * Obtém dados persistentes.
         * --------------------------------------------------------
         */

        CompoundNBT data =
                player.getPersistentData()
                        .getCompound(NBT_KEY);

        String storedBiome =
                data.getString(NBT_FOREST_BIOME);


        /*
         * --------------------------------------------------------
         * Mudança de floresta.
         * --------------------------------------------------------
         *
         * Se o jogador começou a matar mobs em outra floresta,
         * a contagem começa novamente para aquele bioma.
         */
        if (!biomeId.equals(storedBiome)) {

            data = new CompoundNBT();

            data.putString(
                    NBT_FOREST_BIOME,
                    biomeId
            );

            data.putInt(
                    NBT_KILL_COUNT,
                    0
            );
        }


        /*
         * --------------------------------------------------------
         * Incrementa contador.
         * --------------------------------------------------------
         */

        int count =
                data.getInt(NBT_KILL_COUNT) + 1;

        data.putInt(
                NBT_KILL_COUNT,
                count
        );

        player.getPersistentData().put(
                NBT_KEY,
                data
        );


        /*
         * --------------------------------------------------------
         * Verifica se chegou a 50.
         * --------------------------------------------------------
         */

        if (count % KILLS_REQUIRED != 0) {
            return;
        }


        /*
         * --------------------------------------------------------
         * Rola chance de 45%.
         * --------------------------------------------------------
         */

        if (player.getRandom().nextFloat()
                < SPAWN_CHANCE) {

            triggerSpawn(player);

            /*
             * Surgimento bem-sucedido:
             * reinicia o contador.
             */
            data.putInt(
                    NBT_KILL_COUNT,
                    0
            );

            player.getPersistentData().put(
                    NBT_KEY,
                    data
            );
        }

        /*
         * Se falhar:
         *
         * o contador continua.
         *
         * Exemplo:
         *
         * 50 mortes -> falhou
         * 100 mortes -> nova tentativa
         * 150 mortes -> nova tentativa
         */
    }


    /*
     * ============================================================
     * INICIA O SURGIMENTO
     * ============================================================
     */

    private static void triggerSpawn(
            PlayerEntity player
    ) {

        /*
         * Precisamos de um mundo servidor.
         */
        if (!(player.level instanceof ServerWorld)) {
            return;
        }

        ServerWorld world =
                (ServerWorld) player.level;


        /*
         * --------------------------------------------------------
         * Escolhe uma direção aleatória.
         * --------------------------------------------------------
         */

        double angle =
                player.getRandom().nextDouble()
                        * Math.PI
                        * 2.0D;


        /*
         * --------------------------------------------------------
         * Escolhe distância entre 12 e 20 blocos.
         * --------------------------------------------------------
         */

        double distance =
                MIN_SPAWN_DISTANCE
                        + player.getRandom().nextDouble()
                        * (MAX_SPAWN_DISTANCE
                        - MIN_SPAWN_DISTANCE);


        /*
         * --------------------------------------------------------
         * Calcula posição.
         * --------------------------------------------------------
         */

        BlockPos pos =
                player.blockPosition().offset(
                        (int) (Math.cos(angle) * distance),
                        0,
                        (int) (Math.sin(angle) * distance)
                );


        /*
         * --------------------------------------------------------
         * CORREÇÃO PRINCIPAL:
         *
         * MOTION_SURFACE NÃO EXISTE EM 1.16.5.
         *
         * O correto é MOTION_BLOCKING.
         * --------------------------------------------------------
         */

        pos =
                world.getHeightmapPos(
                        Heightmap.Type.MOTION_BLOCKING,
                        pos
                );


        /*
         * --------------------------------------------------------
         * Som de aviso.
         * --------------------------------------------------------
         */

        world.playSound(
                null,
                pos,
                SoundEvents.RAVAGER_ROAR,
                SoundCategory.HOSTILE,
                2.0F,
                0.5F
        );


        /*
         * --------------------------------------------------------
         * Agenda a materialização.
         *
         * 60 ticks = aproximadamente 3 segundos.
         * --------------------------------------------------------
         */

        PENDING.put(
                player.getUUID(),
                new PendingSpawn(
                        world,
                        pos,
                        SPAWN_DELAY_TICKS
                )
        );
    }


    /*
     * ============================================================
     * PROCESSAMENTO DOS SPAWNS PENDENTES
     * ============================================================
     */

    @SubscribeEvent
    public static void onWorldTick(
            TickEvent.WorldTickEvent event
    ) {

        /*
         * Só executa no final do tick.
         */
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        /*
         * Só servidor.
         */
        if (event.world.isClientSide) {
            return;
        }


        /*
         * Iterador seguro para remover entradas
         * enquanto percorremos o mapa.
         */
        Iterator<Map.Entry<UUID, PendingSpawn>> iterator =
                PENDING.entrySet().iterator();


        while (iterator.hasNext()) {

            Map.Entry<UUID, PendingSpawn> entry =
                    iterator.next();

            PendingSpawn pending =
                    entry.getValue();


            /*
             * Só processa o mundo correspondente.
             */
            if (pending.world != event.world) {
                continue;
            }


            /*
             * Reduz o tempo restante.
             */
            pending.delayTicks--;


            /*
             * Ainda não chegou a hora.
             */
            if (pending.delayTicks > 0) {
                continue;
            }


            /*
             * ----------------------------------------------------
             * Cria o Mapinguari.
             * ----------------------------------------------------
             */

            MapinguariEntity mapinguari =
                    ModSupernaturalEntities
                            .MAPINGUARI
                            .get()
                            .create(pending.world);


            if (mapinguari != null) {

                /*
                 * Coloca a entidade na posição calculada.
                 */
                mapinguari.moveTo(
                        pending.pos.getX() + 0.5D,
                        pending.pos.getY(),
                        pending.pos.getZ() + 0.5D,
                        0.0F,
                        0.0F
                );


                /*
                 * Adiciona ao mundo.
                 */
                pending.world.addFreshEntity(
                        mapinguari
                );
            }


            /*
             * Remove o spawn da fila.
             */
            iterator.remove();
        }
    }
}