package com.soldierskull.eclipse.skills;

import java.util.List;
import java.util.UUID;

import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Efeitos reais das habilidades da árvore Lobisomem.
 *
 * O QUE NÃO ESTÁ AQUI (registrado, mas sem efeito ainda):
 *   - Olfato Predatório: precisa de renderização de partículas/rastro no
 *     chão (sistema de render que não existe).
 *   - Salto Predatório: precisa de detecção de "aterrissagem" (evento de
 *     fall/landing rastreado por jogador) que não existe ainda.
 *   - Fúria Lunar: precisa de leitura de fase lunar do mundo escalando o
 *     bônus continuamente - decidi não aproximar isso com um número
 *     chutado; me diga a fórmula exata (ou confirme uma aproximação) e eu
 *     implemento.
 *   - Forma Alfa: implementei só os multiplicadores de atributo: "ataques
 *     especiais" e "rugido aprimorado" não têm mecânica própria definida
 *     (o rugido aprimorado presumivelmente reusa Rugido Intimidador com
 *     bônus - não implementado até confirmar).
 */
public final class WerewolfSkillEffects {

    private static UUID uuid(String seed) {
        return UUID.nameUUIDFromBytes(("eclipse.skill." + seed).getBytes());
    }

    private WerewolfSkillEffects() {
    }

    /** 6. Investida: avança 3 blocos, causa 1 de dano e empurra 2,5 blocos o alvo mirado (raytrace, reaproveitando o helper do Vampiro). */
    public static final SkillEffect INVESTIDA = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            Vector3d look = player.getLookAngle();
            player.setDeltaMovement(look.x * 1.4D, Math.max(player.getDeltaMovement().y, 0.1D), look.z * 1.4D);
            player.hurtMarked = true;

            LivingEntity target = VampireSkillEffects.raytraceEntity(player, 3.0D);
            if (target != null) {
                target.hurt(net.minecraft.util.DamageSource.playerAttack(player), 1.0F);
                target.setDeltaMovement(target.getDeltaMovement().add(look.x * 2.5D * 0.2D, 0.2D, look.z * 2.5D * 0.2D));
                target.hurtMarked = true;
            }
        }
    };

    /** 7. Fúria: 15s de força/velocidade/resistência x2. */
    public static final SkillEffect FURIA = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            long duration = 15_000L;
            BuffManager.applyTimedMultiplier(player, Attributes.ATTACK_DAMAGE, uuid("lobisomem_furia_dano"), "eclipse.furia.dano", 2.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.MOVEMENT_SPEED, uuid("lobisomem_furia_vel"), "eclipse.furia.vel", 2.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.ARMOR, uuid("lobisomem_furia_res"), "eclipse.furia.res", 2.0D, duration);
        }
    };

    /**
     * 9. Rugido Intimidador: aproximação de "paraliza por 2 segundos" -
     * não existe um estado de "paralisia" vanilla, então uso Lentidão em
     * amplificador muito alto (praticamente imóvel) por 2s. Avise se
     * quiser uma paralisia "de verdade" (bloquear ações via capability
     * própria) em vez dessa aproximação.
     */
    public static final SkillEffect RUGIDO_INTIMIDADOR = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            List<LivingEntity> targets = player.level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(7.0D), e -> e != player && e.isAlive());
            for (LivingEntity target : targets) {
                target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, 9)); // aprox. de "paralisado"
            }
        }
    };

    /** 10. Transformação Parcial: 60s de força x3, resistência x2,5, velocidade x2,5 (sangramento no golpe não implementado - ver nota da classe). */
    public static final SkillEffect TRANSFORMACAO_PARCIAL = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            long duration = 60_000L;
            BuffManager.applyTimedMultiplier(player, Attributes.ATTACK_DAMAGE, uuid("lobisomem_transf_parcial_dano"), "eclipse.transf_parcial.dano", 3.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.ARMOR, uuid("lobisomem_transf_parcial_res"), "eclipse.transf_parcial.res", 2.5D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.MOVEMENT_SPEED, uuid("lobisomem_transf_parcial_vel"), "eclipse.transf_parcial.vel", 2.5D, duration);
        }
    };

    /**
     * 12. Fúria Lunar: só ativa à noite. Multiplicador de todos os status
     * (dano/velocidade/resistência) de acordo com a fase lunar, tabela
     * exata que você deu. Duração NÃO especificada - usei 30s como
     * placeholder (ajuste se necessário). Ordem de fases igual à do
     * próprio Minecraft (World#getMoonPhase(): 0=Cheia, 1=Balsâmica,
     * 2=Quarto Minguante, 3=Minguante, 4=Nova, 5=Crescente, 6=Quarto
     * Crescente, 7=Gibosa).
     */
    public static final SkillEffect FURIA_LUNAR = new SkillEffect() {
        private static final long DURATION = 30_000L; // PLACEHOLDER - duracao nao especificada

        private final double[] MOON_MULTIPLIER = {
                2.0D,  // 0 Lua Cheia
                1.5D,  // 1 Lua Balsamica
                1.0D,  // 2 Quarto Minguante
                0.75D, // 3 Lua Minguante
                0.5D,  // 4 Lua Nova
                0.75D, // 5 Lua Crescente
                1.0D,  // 6 Quarto Crescente
                1.5D,  // 7 Lua Gibosa
        };

        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            if (player.level.isDay()) {
                return; // "durante a noite" - de dia a habilidade nao faz efeito
            }
            int moonPhase = player.level.getMoonPhase();
            double multiplier = MOON_MULTIPLIER[moonPhase];

            BuffManager.applyTimedMultiplier(player, Attributes.ATTACK_DAMAGE, uuid("lobisomem_furia_lunar_dano"), "eclipse.furia_lunar.dano", multiplier, DURATION);
            BuffManager.applyTimedMultiplier(player, Attributes.MOVEMENT_SPEED, uuid("lobisomem_furia_lunar_vel"), "eclipse.furia_lunar.vel", multiplier, DURATION);
            BuffManager.applyTimedMultiplier(player, Attributes.ARMOR, uuid("lobisomem_furia_lunar_res"), "eclipse.furia_lunar.res", multiplier, DURATION);
        }
    };

    /**
     * 11. Salto Predatório: impulso de salto de até 10 blocos na direção
     * do olhar. O dano de área "ao aterrissar" é resolvido pelo
     * LivingFallEvent em MovementEvents.onFall (marca o jogador aqui,
     * MovementEvents cuida do resto e cancela o dano de queda do
     * próprio jogador).
     */
    public static final SkillEffect SALTO_PREDATORIO = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            Vector3d look = player.getLookAngle();
            double horizontalStrength = 1.3D; // resulta em ~8-10 blocos dependendo do angulo de olhar
            player.setDeltaMovement(look.x * horizontalStrength, 0.9D, look.z * horizontalStrength);
            player.hurtMarked = true;
            MovementEvents.markPredatoryJump(player.getUUID());
        }
    };

    /** 15. Forma Alfa: força x6, resistência x5, velocidade x4 por 300s de cooldown (duração da transformação não foi especificada - usei 60s como placeholder; ajuste se necessário). Ataques especiais/rugido aprimorado não implementados. */
    public static final SkillEffect FORMA_ALFA = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            long duration = 60_000L; // PLACEHOLDER - duracao nao especificada no documento
            BuffManager.applyTimedMultiplier(player, Attributes.ATTACK_DAMAGE, uuid("lobisomem_forma_alfa_dano"), "eclipse.forma_alfa.dano", 6.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.ARMOR, uuid("lobisomem_forma_alfa_res"), "eclipse.forma_alfa.res", 5.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.MOVEMENT_SPEED, uuid("lobisomem_forma_alfa_vel"), "eclipse.forma_alfa.vel", 4.0D, duration);
        }
    };
}
