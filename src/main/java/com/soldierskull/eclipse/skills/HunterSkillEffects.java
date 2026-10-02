package com.soldierskull.eclipse.skills;

import java.util.List;
import java.util.UUID;

import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Efeitos reais das habilidades da árvore de facção Caçadores.
 *
 * O QUE NÃO ESTÁ AQUI (registrado, sem efeito ainda):
 *   - Conhecimento de Monstros, Rastreador: precisam de detecção +
 *     renderização (ícone sobre a cabeça / partículas de rastro) -
 *     mesmo sistema de render que falta pro Vampiro.
 *   - Treinamento com Prata: precisa de identificar "arma/ferramenta de
 *     prata" - o Eclipse já tem itens de prata (Silver Sword/Dagger),
 *     mas não verifiquei se há uma tag/interface comum pra "é de prata"
 *     que eu possa checar de forma genérica; me diga qual usar.
 *   - Armadilha de Caçador / Armadilha de Prata: precisam de uma
 *     entidade ou bloco de armadilha no mundo (sistema de armadilhas que
 *     não existe).
 *   - Preparação Alquímica, Munição Especial: desbloqueiam receitas /
 *     itens / trocas de vilager que ainda não existem.
 *   - Mestre Caçador: só bonifica mecânicas de outras habilidades que
 *     ainda não estão implementadas (marca, prata, rastreamento).
 */
public final class HunterSkillEffects {

    private HunterSkillEffects() {
    }

    /**
     * 6. Preparação Alquímica: SIMPLIFICAÇÃO - o documento original
     * descreve "desbloquear o uso/receitas de consumíveis especializados"
     * (não uma habilidade ativável). Um sistema de receitas condicionadas
     * a skill desbloqueada é uma peça de infraestrutura maior (precisaria
     * de "advancement locking" de receitas); por ora, ao desbloquear,
     * concede 3 Águas Benta de largada - a receita de crafting em si
     * (data/eclipse/recipes/holy_water.json) já é sempre craftável por
     * qualquer jogador, não só quem tem essa skill. Avise se quiser que
     * eu implemente o bloqueio de receita de verdade.
     */
    public static final SkillEffect PREPARACAO_ALQUIMICA = new SkillEffect() {
        @Override
        public void onUnlock(ServerPlayerEntity player, PlayerStats stats) {
            player.addItem(new net.minecraft.item.ItemStack(com.soldierskull.eclipse.item.ModItems.HOLY_WATER.get(), 3));
        }
    };

    /**
     * 5. Armadilha de Caçador / 8. Armadilha de Prata: cria uma marca no
     * chão (partículas vermelhas) que atrai sobrenaturais num raio de 10
     * blocos e aplica Lentidão extrema por 5s quando um se aproxima -
     * ver TrapRegistry. Colocada aos pés do jogador (sem raytrace de
     * bloco, por simplicidade). As duas habilidades usam o mesmo
     * mecanismo - Armadilha de Prata era descrita com efeitos extras
     * (dano, Fraqueza) que não foram repetidos na última especificação,
     * então por ora ambas fazem a mesma coisa; avise se a de Prata deve
     * ser mais forte.
     */
    public static final SkillEffect ARMADILHA = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            com.soldierskull.eclipse.skills.TrapRegistry.place(
                    (net.minecraft.world.server.ServerWorld) player.level,
                    player.blockPosition(),
                    60_000L);
        }
    };

    /** 10. Munição Especial: concede o Arco Especial (5 de durabilidade, flechas com +3 de dano vs sobrenaturais) ao desbloquear. */
    public static final SkillEffect MUNICAO_ESPECIAL = new SkillEffect() {
        @Override
        public void onUnlock(ServerPlayerEntity player, PlayerStats stats) {
            player.addItem(new net.minecraft.item.ItemStack(com.soldierskull.eclipse.item.ModItems.HUNTER_SPECIAL_BOW.get()));
        }
    };

    /** 4. Golpe Preciso: arma uma janela de 5s em que o próximo golpe corpo a corpo recebe bônus de dano (ver CombatEvents - valor de bônus é placeholder, não especificado no documento). */
    public static final SkillEffect GOLPE_PRECISO = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            CombatEvents.armGolpePreciso(player.getUUID(), 5_000L);
        }
    };

    /**
     * 7. Marca do Caçador: versão SIMPLIFICADA - aplica Fraqueza I +
     * Lentidão I por 30s no alvo mirado (reaproveita o raytrace do
     * Vampiro). NÃO implementado: ícone visual da marca e "dano
     * aumentado dos ataques do Caçador contra o alvo marcado" (precisaria
     * de um registro de "quem está marcado por quem", não só o efeito de
     * poção).
     */
    public static final SkillEffect MARCA_DO_CACADOR = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            LivingEntity target = VampireSkillEffects.raytraceEntity(player, 8.0D);
            if (target == null) {
                return;
            }
            target.addEffect(new EffectInstance(Effects.WEAKNESS, 600, 0));
            target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 600, 0));
        }
    };

    /** 11. Ritual de Contenção: área de 5 blocos de raio, 10s de Lentidão II + Fraqueza I em criaturas sobrenaturais próximas (placeholder de "sobrenatural" - ver SupernaturalEntities). */
    public static final SkillEffect RITUAL_DE_CONTENCAO = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            List<LivingEntity> targets = player.level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(5.0D), e -> e != player && e.isAlive() && SupernaturalEntities.isSupernatural(e));
            for (LivingEntity target : targets) {
                target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200, 1));
                target.addEffect(new EffectInstance(Effects.WEAKNESS, 200, 0));
            }
        }
    };

    /** 13. Arsenal do Exterminador: 30s de Força IV (amplificador 3) + Resistência III (amplificador 2). Bônus de dano de prata/munição/+1 flat contra sobrenaturais não implementados (dependem de outras habilidades ainda sem efeito). */
    public static final SkillEffect ARSENAL_DO_EXTERMINADOR = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            player.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 600, 3));
            player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 600, 2));
        }
    };
}
