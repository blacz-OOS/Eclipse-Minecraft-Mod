package com.soldierskull.eclipse.skills;

import java.util.List;

import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

/**
 * Cobre as 4 skills de detecção que precisavam de "ícone/render":
 * Sentidos Vampíricos, Conhecimento de Monstros (identificação + ícone),
 * Olfato Predatório, Rastreador (rastro de partículas).
 *
 * TEXTURA: usei partículas vanilla (SOUL para o ícone, DUST vermelho pro
 * rastro) em vez de criar uma textura/ícone próprio do Eclipse - é
 * genérico e visualmente identificável (partícula distinta, incomum em
 * jogo normal) sem precisar registrar um renderer de entidade customizado.
 * Se quiser um ícone de verdade (sprite fixo acima da cabeça, não
 * partícula), isso é um sistema de render bem maior (RenderLivingEvent +
 * textura própria) - me avise se prefere investir nisso depois.
 *
 * Identificação ao mirar (texto "Lobisomem" / "Criatura Sobrenatural")
 * vira uma mensagem na action bar em vez de um nametag flutuante - mesmo
 * raciocínio de simplicidade.
 *
 * Checado 1x/segundo (ver ServerEvents.onPlayerTick), igual ao resto do
 * sistema de skills - não precisa de mais frequência que isso pra uma
 * habilidade passiva de percepção.
 */
public final class DetectionEvents {

    private static final RedstoneParticleData TRAIL_RED = new RedstoneParticleData(0.8F, 0.1F, 0.1F, 1.0F);

    private DetectionEvents() {
    }

    public static void tick(ServerPlayerEntity player, PlayerStats stats) {
        boolean vampireSense = stats.hasUnlockedSkill("vampiro_sentidos_vampiricos");
        boolean hunterSense = stats.hasUnlockedSkill("cacadores_conhecimento_de_monstros");

        if (vampireSense || hunterSense) {
            tickIdentifyAndIcon(player);
        }

        boolean werewolfTrail = stats.hasUnlockedSkill("lobisomem_olfato_predatorio");
        boolean hunterTrail = stats.hasUnlockedSkill("cacadores_rastreador");
        if (werewolfTrail || hunterTrail) {
            tickTrail(player);
        }
    }

    private static void tickIdentifyAndIcon(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.level;

        // Icone: uma particula "SOUL" acima da cabeca de toda criatura sobrenatural em 16 blocos, so visivel pra esse jogador.
        List<LivingEntity> nearby = world.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(16.0D), e -> e.isAlive() && SupernaturalEntities.isSupernatural(e));
        for (LivingEntity target : nearby) {
            world.sendParticles(player, ParticleTypes.SOUL, true,
                    target.getX(), target.getY() + target.getBbHeight() + 0.3D, target.getZ(),
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        // Identificacao ao mirar (com linha de visao, ate 16 blocos).
        LivingEntity looked = VampireSkillEffects.raytraceEntity(player, 16.0D);
        if (looked != null && SupernaturalEntities.isSupernatural(looked)) {
            player.displayClientMessage(new StringTextComponent(describeType(looked)), true);
        }
    }

    private static void tickTrail(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.level;
        List<LivingEntity> nearby = world.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(10.0D), e -> e.isAlive() && SupernaturalEntities.isSupernatural(e));

        for (LivingEntity target : nearby) {
            // "mancha mais forte quanto mais ferida" -> mais particulas quanto menor a vida proporcional.
            float healthFraction = target.getHealth() / target.getMaxHealth();
            int particleCount = 1 + (int) ((1.0F - healthFraction) * 4);
            world.sendParticles(player, TRAIL_RED, true,
                    target.getX(), target.getY() + 0.05D, target.getZ(),
                    particleCount, 0.3D, 0.0D, 0.3D, 0.0D);
        }
    }

    /** PLACEHOLDER: nomes genéricos por EntityType - troque por nomes/lore reais do Eclipse quando os mobs próprios existirem. */
    private static String describeType(LivingEntity entity) {
        return entity.getType().getDescription().getString() + "\nCriatura Sobrenatural";
    }
}
