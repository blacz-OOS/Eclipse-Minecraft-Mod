package com.soldierskull.eclipse.skills;

import java.util.List;
import java.util.UUID;

import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Efeitos reais das habilidades da árvore Vampiro, com os números
 * exatos que você deu na segunda especificação. Passivas simples que só
 * mudam velocidade/dano/resistência de forma direta ficam em
 * SkillAttributeHandler (atributo permanente) ou CombatEvents (cálculo
 * ao vivo) - aqui só o que precisa de lógica própria.
 *
 * O QUE NÃO ESTÁ AQUI (registrado na árvore, mas sem efeito ainda):
 *   - Sentidos Vampíricos, Visão Noturna: precisam de detecção +
 *     renderização de ícone acima da entidade (sistema de HUD/render
 *     que não existe ainda).
 *   - Forma de Névoa: precisa de override do movimento do jogador pra
 *     atravessar blocos (noclip parcial) - infraestrutura de movimento
 *     que não existe.
 *   - Regeneração Superior: depende do conceito de "fome cheia" da
 *     Regeneração Sanguínea, que por sua vez depende de beber sangue em
 *     garrafa/sucção - sistema de "fome vampírica" ainda não existe
 *     (só registrei Regeneração Sanguínea com efeito parcial abaixo).
 */
public final class VampireSkillEffects {

    private static final UUID FOME_DE_SANGUE_DANO = uuid("vampiro_fome_de_sangue_dano");
    private static final UUID FOME_DE_SANGUE_VEL = uuid("vampiro_fome_de_sangue_vel");
    private static final UUID FOME_DE_SANGUE_RES = uuid("vampiro_fome_de_sangue_res");
    private static final UUID VEL_SOBRENATURAL_VEL = uuid("vampiro_velocidade_sobrenatural_vel");
    private static final UUID SENHOR_DO_SANGUE_DANO = uuid("vampiro_senhor_do_sangue_dano");
    private static final UUID SENHOR_DO_SANGUE_VEL = uuid("vampiro_senhor_do_sangue_vel");
    private static final UUID SENHOR_DO_SANGUE_RES = uuid("vampiro_senhor_do_sangue_res");
    private static final UUID ASCENSAO_VEL = uuid("vampiro_ascensao_vel");
    private static final UUID ASCENSAO_RES = uuid("vampiro_ascensao_res");
    private static final UUID ASCENSAO_DANO = uuid("vampiro_ascensao_dano");

    private static UUID uuid(String seed) {
        return UUID.nameUUIDFromBytes(("eclipse.skill." + seed).getBytes());
    }

    private VampireSkillEffects() {
    }

    /** 3. Visão Noturna: aplica Visão Noturna com duração muito longa ao desbloquear (sem sistema de "refresh" contínuo ainda - se acabar após dias sem relog, reloga pra reaplicar). */
    public static final SkillEffect VISAO_NOTURNA = new SkillEffect() {
        @Override
        public void onUnlock(ServerPlayerEntity player, PlayerStats stats) {
            player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 999_999, 0, false, false));
        }
    };

    /** 2. Presas Vampíricas: mordida no alvo mirado (raytrace de entidade), 2 de dano, cura 0,5 HP + 0,5 Energia. */
    public static final SkillEffect PRESAS_VAMPIRICAS = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            LivingEntity target = raytraceEntity(player, 4.0D);
            if (target == null) {
                return;
            }
            target.hurt(net.minecraft.util.DamageSource.playerAttack(player), 2.0F);
            player.heal(0.5F);
            stats.addCommonEnergy(1); // "recupera 0,5 de energia" arredondado pra cima: addCommonEnergy trabalha em inteiros (ver PlayerStats) - ajuste se precisar de precisao fracionaria
        }
    };

    /** 6. Passo Sombrio: investida de 3 blocos na direção do olhar. Atravessar blocos ("tecnicamente seguro") não implementado - fica como impulso normal. */
    public static final SkillEffect PASSO_SOMBRIO = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            Vector3d look = player.getLookAngle();
            player.setDeltaMovement(look.x * 1.6D, Math.max(player.getDeltaMovement().y, 0.15D), look.z * 1.6D);
            player.hurtMarked = true; // forca sincronizar a velocidade com o cliente
        }
    };

    /** 7. Fome de Sangue: 15s de dano x2, velocidade x2, resistência x2 (ARMOR). Custo de fome/alimentação não implementado (sem sistema de fome vampírica ainda). */
    public static final SkillEffect FOME_DE_SANGUE = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            long duration = 15_000L;
            BuffManager.applyTimedMultiplier(player, Attributes.ATTACK_DAMAGE, FOME_DE_SANGUE_DANO, "eclipse.fome_de_sangue.dano", 2.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.MOVEMENT_SPEED, FOME_DE_SANGUE_VEL, "eclipse.fome_de_sangue.vel", 2.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.ARMOR, FOME_DE_SANGUE_RES, "eclipse.fome_de_sangue.res", 2.0D, duration);
        }
    };

    /**
     * 9. Dominação Sanguínea: versão SIMPLIFICADA (7x7 blocos = raio 3,5).
     * Implementado: mortos-vivos ficam lentos (Slowness), aracnídeos ficam
     * fracos (Weakness, aproximando "perdem 1/3 do dano"), mobs pacíficos
     * passam a atacar quem os bater (ou o próprio jogador, na falta de um
     * "quem bateu por último" registrado). NÃO implementado: mobs do
     * Nether sem dano de fogo, fantasmas sem voar/invisibilidade (não há
     * hook genérico pra essas duas flags em todas as entidades vanilla) -
     * e nada disso se aplica a bosses (checagem por getMaxHealth alto
     * como aproximação de "boss", já que o Eclipse não tem uma flag de
     * boss própria ainda).
     */
    public static final SkillEffect DOMINACAO_SANGUINEA = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            List<LivingEntity> targets = player.level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(3.5D), e -> e != player && e.isAlive() && e.getMaxHealth() < 100F);

            for (LivingEntity target : targets) {
                boolean undead = target.getMobType() == net.minecraft.entity.CreatureAttribute.UNDEAD;
                boolean arthropod = target.getMobType() == net.minecraft.entity.CreatureAttribute.ARTHROPOD;

                if (undead) {
                    target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200, 1)); // ~1,8x mais lento (aprox.)
                } else if (arthropod) {
                    target.addEffect(new EffectInstance(Effects.WEAKNESS, 200, 0));
                } else if (target instanceof net.minecraft.entity.CreatureEntity
                        && !(target instanceof net.minecraft.entity.monster.MonsterEntity)) {
                    if (target instanceof net.minecraft.entity.MobEntity) {
                        ((net.minecraft.entity.MobEntity) target).setTarget(player);
                    }
                }
            }
        }
    };

    /** 10. Forma de Névoa: 5s de noclip (atravessa blocos/mobs) + 2,5 de dano a quem for atravessado. Ver MovementEvents para o noclip/dano; aqui só liga o estado. */
    public static final SkillEffect FORMA_DE_NEVOA = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            MovementEvents.startFogForm(player, 5_000L);
        }
    };

    /** 12. Hipnose: humanoides próximos recebem Lentidão II + Fraqueza I por 5s; o jogador causa 2x de dano enquanto ativo (via BuffManager, 5s). */
    public static final SkillEffect HIPNOSE = new SkillEffect() {
        private static final long DURATION = 5_000L;

        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            List<LivingEntity> targets = player.level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(4.0D),
                    e -> e != player && e.isAlive() && e instanceof net.minecraft.entity.player.PlayerEntity == false
                            && e.getType().getCategory() == net.minecraft.entity.EntityClassification.MONSTER);
            for (LivingEntity target : targets) {
                target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 100, 1));
                target.addEffect(new EffectInstance(Effects.WEAKNESS, 100, 0));
            }
            UUID modifierId = uuid("vampiro_hipnose_dano");
            BuffManager.applyTimedMultiplier(player, Attributes.ATTACK_DAMAGE, modifierId, "eclipse.hipnose.dano", 2.0D, DURATION);
        }
    };

    /** 13. Velocidade Sobrenatural: 15s de velocidade x2,5 (salto/velocidade de ataque não têm attribute vanilla direto - só movimento implementado). */
    public static final SkillEffect VELOCIDADE_SOBRENATURAL = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            BuffManager.applyTimedMultiplier(player, Attributes.MOVEMENT_SPEED, VEL_SOBRENATURAL_VEL, "eclipse.vel_sobrenatural", 2.5D, 15_000L);
        }
    };

    /** 14. Senhor do Sangue: 15s de dano/velocidade/resistência x3 (regeneração x3 não implementada - sem sistema de regen customizado por buff ainda). */
    public static final SkillEffect SENHOR_DO_SANGUE = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            long duration = 15_000L;
            BuffManager.applyTimedMultiplier(player, Attributes.ATTACK_DAMAGE, SENHOR_DO_SANGUE_DANO, "eclipse.senhor_do_sangue.dano", 3.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.MOVEMENT_SPEED, SENHOR_DO_SANGUE_VEL, "eclipse.senhor_do_sangue.vel", 3.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.ARMOR, SENHOR_DO_SANGUE_RES, "eclipse.senhor_do_sangue.res", 3.0D, duration);
        }
    };

    /**
     * 15. Ascensão Vampírica: 25s de velocidade x4, resistência x6, dano
     * x3,5 e "sentidos ampliados" (Glowing nos mobs visíveis - aproximado
     * como todo mob num raio de 20 blocos, já que "na visão do jogador"
     * precisaria de raytrace por entidade). Regeneração x5 não
     * implementada (mesmo motivo do Senhor do Sangue).
     */
    public static final SkillEffect ASCENSAO_VAMPIRICA = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            long duration = 25_000L;
            BuffManager.applyTimedMultiplier(player, Attributes.MOVEMENT_SPEED, ASCENSAO_VEL, "eclipse.ascensao.vel", 4.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.ARMOR, ASCENSAO_RES, "eclipse.ascensao.res", 6.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.ATTACK_DAMAGE, ASCENSAO_DANO, "eclipse.ascensao.dano", 3.5D, duration);

            List<LivingEntity> nearby = player.level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(20.0D), e -> e != player && e.isAlive());
            for (LivingEntity mob : nearby) {
                mob.addEffect(new EffectInstance(Effects.GLOWING, (int) (duration / 50L), 0));
            }
        }
    };

    /**
     * "Flecha de Sangue" (bônus, fora da especificação original de 15
     * habilidades): ataque à distância, diferente de Presas Vampíricas
     * (que é corpo a corpo) - por isso NÃO é duplicada, mantive o
     * conceito. Reimplementei sem depender de uma entidade de projétil
     * própria (a classe original chamava BloodArrowEntity, que não
     * existe no projeto) - em vez disso, uso o mesmo raytrace instantâneo
     * de Presas Vampíricas, só que com alcance maior (12 blocos) e sem
     * cura. PLACEHOLDER de posição na árvore: registrei como nível
     * racial 6, custo 2, sem pré-requisito - não fazia parte da tabela
     * de 15 habilidades que você confirmou, então essa posição é só uma
     * proposta; me diga se deve ser diferente (nível, custo, dano).
     */
    public static final SkillEffect FLECHA_DE_SANGUE = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            LivingEntity target = raytraceEntity(player, 12.0D);
            if (target == null) {
                return;
            }
            target.hurt(net.minecraft.util.DamageSource.playerAttack(player), 3.0F);
        }
    };

    static LivingEntity raytraceEntity(ServerPlayerEntity player, double range) {
        Vector3d start = player.getEyePosition(1.0F);
        Vector3d look = player.getLookAngle();
        Vector3d end = start.add(look.x * range, look.y * range, look.z * range);
        AxisAlignedBB searchBox = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0D);

        EntityRayTraceResult result = ProjectileHelper.getEntityHitResult(player, start, end, searchBox,
                e -> e instanceof LivingEntity && e != player && e.isAlive(), range * range);

        if (result == null) {
            return null;
        }
        return result.getEntity() instanceof LivingEntity ? (LivingEntity) result.getEntity() : null;
    }
}
