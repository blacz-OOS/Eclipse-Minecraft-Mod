package com.soldierskull.eclipse.transformation;

import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.stats.AffinityType;
import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

/**
 * Fase 9 - Transformação sobrenatural, regra 14 do documento original:
 *
 *   "50% de afinidade + entrada no Abismo → possibilidade de
 *   transformação. A transformação não deve ser instantânea... ~5 dias
 *   de Minecraft."
 *
 * Fica de olho em todo jogador HUMANO a cada segundo (mesmo tick de 1s
 * do resto do sistema de skills/raça): se a afinidade Vampírica OU
 * Lupina estiver >=50% E o jogador estiver no Abismo (ver AbyssLocation
 * - placeholder até a Fase 13 existir), inicia a contagem. Uma vez
 * iniciada, o progresso é contado em DIAS DE MINECRAFT DECORRIDOS no
 * mundo (não em tempo real/wall-clock como os cooldowns de skill) -
 * "5 dias de Minecraft" no documento original claramente se refere ao
 * relógio do jogo, não ao relógio real; por isso a contagem PAUSA
 * quando o servidor está desligado (o dia do jogo não passa), diferente
 * dos cooldowns. Avise se a intenção era outra.
 *
 * SUPOSIÇÕES/REGRAS CONFIRMADAS:
 *   1. A transformação continua contando mesmo que o jogador SAIA do
 *      Abismo antes dos 5 dias - só a entrada inicial é obrigatória.
 *      Só que agora isso é rastreado: se ele ficar no Abismo o tempo
 *      TODO (nunca sair), ganha a conquista "O Corrompido" ao completar.
 *   2. Vampírica e Lupina são inversamente proporcionais (confirmado -
 *      subir uma sempre desce a outra), então as duas nunca ficam >=50%
 *      ao mesmo tempo. O código ainda escolhe a maior por segurança
 *      (defensivo), mas esse cenário de empate não deve acontecer na
 *      prática.
 *   3. Se o jogador estiver na facção Caçadores (que exige ser Humano)
 *      quando a transformação completa, ele é removido da facção
 *      automaticamente (confirmado - mantém como está).
 */
public final class TransformationManager {

    /** ~5 dias de Minecraft, conforme o documento original. */
    public static final long TRANSFORMATION_DURATION_DAYS = 5L;

    private TransformationManager() {
    }

    /** Chamado 1x/segundo por jogador (ver ServerEvents.onPlayerTick). */
    public static void tick(ServerPlayerEntity player, PlayerStats stats) {
        if (stats.getRace() != RaceType.HUMAN) {
            return; // ja e sobrenatural - nada a fazer aqui
        }

        if (stats.isTransforming()) {
            checkProgress(player, stats);
        } else {
            checkEligibilityAndStart(player, stats);
        }
    }

    private static void checkEligibilityAndStart(ServerPlayerEntity player, PlayerStats stats) {
        int vampirica = stats.getAffinity(AffinityType.VAMPIRIC);
        int lupina = stats.getAffinity(AffinityType.LUPINE);

        boolean eligible = (vampirica >= 50 || lupina >= 50) && AbyssLocation.isInAbyss(player);
        if (!eligible) {
            return;
        }

        RaceType target = vampirica >= lupina ? RaceType.VAMPIRE : RaceType.WEREWOLF;

        long currentDay = ((ServerWorld) player.level).getDayTime() / 24000L;
        stats.startTransformation(target, currentDay);

        player.displayClientMessage(
                new net.minecraft.util.text.StringTextComponent(
                        "Você sente uma mudança começando... (transformação em " + target.getDisplayName() + " iniciada)"),
                false);
    }

    private static void checkProgress(ServerPlayerEntity player, PlayerStats stats) {
        if (!AbyssLocation.isInAbyss(player)) {
            stats.markLeftAbyssDuringTransformation();
        }

        long currentDay = ((ServerWorld) player.level).getDayTime() / 24000L;
        long elapsed = currentDay - stats.getTransformationStartDay();

        if (elapsed < TRANSFORMATION_DURATION_DAYS) {
            return;
        }

        complete(player, stats);
    }

    private static void complete(ServerPlayerEntity player, PlayerStats stats) {
        RaceType target = stats.getTransformationTargetRace();
        boolean stayedWholeTime = !stats.didLeaveAbyssDuringTransformation();

        // Cacadores so aceita Humano (regra confirmada anteriormente) - se o
        // jogador ainda estiver na faccao quando a transformacao completa,
        // remove ele dela pra nao bloquear a troca de raca (ver suposicao 3
        // no comentario da classe).
        if (stats.getFaction() == FactionType.HUNTERS) {
            stats.clearFaction();
        }

        stats.clearTransformation();
        stats.setRace(target);

        player.displayClientMessage(
                new net.minecraft.util.text.StringTextComponent(
                        "Sua transformação em " + target.getDisplayName() + " se completou."),
                false);

        if (stayedWholeTime) {
            grantOCorrompido(player);
        }
    }

    /**
     * Concede a conquista "O Corrompido" (ver data/eclipse/advancements/
     * o_corrompido.json - critério "minecraft:impossible", só é concedida
     * por código, nunca automaticamente pelo jogo).
     */
    private static void grantOCorrompido(ServerPlayerEntity player) {
        net.minecraft.advancements.Advancement advancement = player.getServer().getAdvancements()
                .getAdvancement(new net.minecraft.util.ResourceLocation("eclipse", "o_corrompido"));
        if (advancement == null) {
            return; // datapack nao carregado por algum motivo - falha silenciosa, nao deveria travar a transformacao
        }
        net.minecraft.advancements.AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(advancement, criterion);
            }
        }
    }

    /** Cancela uma transformação em andamento (uso administrativo, ou futuramente algum item/ritual que reverta o processo). */
    public static void cancel(PlayerStats stats) {
        stats.clearTransformation();
    }
}
