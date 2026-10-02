package com.soldierskull.eclipse.skills;

import java.util.UUID;

import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;

/**
 * Efeitos reais das habilidades da árvore de facção Cultistas.
 *
 * NOTA DE MAPEAMENTO: tratei "corrupção" deste documento como sendo a
 * mesma coisa que "Energia Abissal" já existente em PlayerStats - o
 * próprio código antigo do Eclipse já comentava "Corruption
 * (Corruption)" em PacketConvertEnergy.java, então não é uma suposição
 * nova, só uma confirmação. Avise se "corrupção" deveria ser um recurso
 * SEPARADO da Energia Abissal.
 *
 * O QUE NÃO ESTÁ AQUI (registrado, sem efeito ainda):
 *   - Conhecimento Proibido, Marca do Culto, Sussurros do Abismo, Olho
 *     do Abismo: dependem de sistemas de conteúdo oculto/estruturas que
 *     não existem ainda.
 *   - Pequeno Ritual, Ritual Maior, Voz do Abismo: descritos de forma
 *     genérica no documento ("produz um benefício", "manipula
 *     criaturas/fenômenos"), sem número ou mecânica concreta - por
 *     instrução sua, deixados sem efeito.
 *   - Ritual do Eclipse: precisa de um sistema de "iniciar um eclipse"
 *     (manipulação de dia/noite) que não existe no mod ainda - é
 *     provavelmente o maior item novo de infraestrutura pendente.
 *
 * Invocações (zumbis/golems) são uma versão SIMPLIFICADA: nascem
 * fortalecidos e com boné de couro (pra não pegar fogo no sol), e são
 * removidos depois do tempo dito - mas continuam com o comportamento
 * hostil padrão do jogo (atacam o jogador também), porque uma IA de
 * "aliado que luta pelo invocador" exigiria um EntityType customizado
 * com goals próprios, que não existe ainda.
 */
public final class CultistSkillEffects {

    private CultistSkillEffects() {
    }

    /** 5. Sacrifício: perde 5 de vida (se tiver mais que isso), recupera 100% da Energia Abissal/Corrupção. */
    public static final SkillEffect SACRIFICIO = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            if (player.getHealth() <= 5.0F) {
                return; // nao deixa o sacrificio matar o jogador
            }
            player.hurt(DamageSource.MAGIC, 5.0F);
            stats.setCorruption(stats.getCorruptionMax());
        }
    };

    /** 6. Corrupção Controlada: aumenta permanentemente o máximo de Energia Abissal/Corrupção em 10. */
    public static final SkillEffect CORRUPCAO_CONTROLADA = new SkillEffect() {
        @Override
        public void onUnlock(ServerPlayerEntity player, PlayerStats stats) {
            stats.addCorruptionMax(10);
        }
    };

    /**
     * 7. Ritual de Invocação: custa 15 de Energia Abissal/Corrupção -
     * checado e debitado manualmente aqui (SkillManager só debita Common
     * Energy via Skill.energyCost automaticamente; corrupção é um pool
     * separado). Invoca 3 zumbis com 1,5x de vida máxima, imunes a
     * queimar no sol (boné de couro) e removidos após 15s. "Ajudar" o
     * jogador (IA aliada) NÃO implementado - ver nota da classe.
     */
    public static final SkillEffect RITUAL_DE_INVOCACAO = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            if (stats.getCorruption() < 15) {
                return;
            }
            stats.removeCorruption(15);

            ServerWorld world = (ServerWorld) player.level;
            for (int i = 0; i < 3; i++) {
                ZombieEntity zombie = new ZombieEntity(world);
                zombie.moveTo(player.getX() + (i - 1), player.getY(), player.getZ() + 1, player.yRot, 0);
                ModifiableAttributeInstance health = zombie.getAttribute(Attributes.MAX_HEALTH);
                if (health != null) {
                    health.addPermanentModifier(new AttributeModifier(uuid("ritual_invocacao_vida"), "eclipse.ritual_invocacao", 0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));
                    zombie.setHealth((float) health.getValue());
                }
                zombie.setItemSlot(net.minecraft.inventory.EquipmentSlotType.HEAD, new ItemStack(Items.LEATHER_HELMET));
                world.addFreshEntity(zombie);
                SummonRegistry.schedule(zombie, System.currentTimeMillis() + 15_000L);
            }
        }
    };

    /** 8. Pacto Abissal: custa 5 de Energia Abissal/Corrupção (checado/debitado manualmente, mesmo motivo do Ritual de Invocação). 20s de dano/velocidade/resistência x3. */
    public static final SkillEffect PACTO_ABISSAL = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            if (stats.getCorruption() < 5) {
                return;
            }
            stats.removeCorruption(5);

            long duration = 20_000L;
            BuffManager.applyTimedMultiplier(player, Attributes.ATTACK_DAMAGE, uuid("cultistas_pacto_dano"), "eclipse.pacto.dano", 3.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.MOVEMENT_SPEED, uuid("cultistas_pacto_vel"), "eclipse.pacto.vel", 3.0D, duration);
            BuffManager.applyTimedMultiplier(player, Attributes.ARMOR, uuid("cultistas_pacto_res"), "eclipse.pacto.res", 3.0D, duration);
        }
    };

    /** 11. Invocação Superior: custa 45 de Energia Abissal/Corrupção. Invoca 2 Iron Golems removidos após 10s. IA aliada NÃO implementada (mesma ressalva do Ritual de Invocação). */
    public static final SkillEffect INVOCACAO_SUPERIOR = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            if (stats.getCorruption() < 45) {
                return;
            }
            stats.removeCorruption(45);

            ServerWorld world = (ServerWorld) player.level;
            for (int i = 0; i < 2; i++) {
                IronGolemEntity golem = new IronGolemEntity(net.minecraft.entity.EntityType.IRON_GOLEM, world);
                golem.moveTo(player.getX() + (i == 0 ? -1 : 1), player.getY(), player.getZ() + 1, player.yRot, 0);
                world.addFreshEntity(golem);
                SummonRegistry.schedule(golem, System.currentTimeMillis() + 10_000L);
            }
        }
    };

    /**
     * 13. Ritual do Eclipse: exige Essência do Eclipse na mão (consumida),
     * custa TODA a Corrupção/Energia Abissal, inicia o evento (ver
     * EclipseEventManager - versão simplificada, sem escurecimento
     * visual do céu).
     */
    public static final SkillEffect RITUAL_DO_ECLIPSE = new SkillEffect() {
        @Override
        public void onActivate(ServerPlayerEntity player, PlayerStats stats) {
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            ItemStack essenceStack = mainHand.getItem() == com.soldierskull.eclipse.item.ModItems.ECLIPSE_ESSENCE.get() ? mainHand
                    : (offHand.getItem() == com.soldierskull.eclipse.item.ModItems.ECLIPSE_ESSENCE.get() ? offHand : null);
            if (essenceStack == null) {
                return; // precisa da Essencia do Eclipse em uma das maos
            }
            if (stats.getCorruption() <= 0) {
                return;
            }

            essenceStack.shrink(1);
            stats.setCorruption(0); // "todos os pontos de corrupcao"

            EclipseEventManager.start((ServerWorld) player.level, 5 * 60_000L); // 5 minutos - duracao nao especificada, placeholder
        }
    };

    private static UUID uuid(String seed) {
        return UUID.nameUUIDFromBytes(("eclipse.skill." + seed).getBytes());
    }
}
