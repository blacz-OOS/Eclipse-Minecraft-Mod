package com.soldierskull.eclipse.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

/**
 * Aplica buffs de atributo TEMPORÁRIOS (ex.: Fúria, Fome de Sangue,
 * Ascensão Vampírica) e remove automaticamente quando o tempo acaba.
 *
 * LIMITAÇÃO CONHECIDA: os modificadores ativos aqui NÃO são persistidos
 * em NBT (mesmo padrão já usado pelos atributos permanentes em
 * StatAttributeHandler - "Attribute modifiers are not saved to disk").
 * Se o jogador deslogar no meio de um buff de 15-30 segundos, o buff se
 * perde ao reconectar (o cooldown da skill, esse sim, é persistente -
 * ver PlayerStats.skillCooldownEnd). Dado que essas durações são curtas,
 * tratei isso como aceitável; avise se precisar ser diferente.
 *
 * Expiração é verificada uma vez por segundo, reaproveitando o mesmo
 * intervalo (REGEN_TICK_INTERVAL = 20 ticks) já usado em
 * ServerEvents.onPlayerTick - ver SkillTickEvents.
 */
public final class BuffManager {

    private static final class ActiveBuff {
        final Attribute attribute;
        final UUID modifierId;
        final long expireAtMillis;

        ActiveBuff(Attribute attribute, UUID modifierId, long expireAtMillis) {
            this.attribute = attribute;
            this.modifierId = modifierId;
            this.expireAtMillis = expireAtMillis;
        }
    }

    private static final Map<UUID, List<ActiveBuff>> ACTIVE = new HashMap<>();

    private BuffManager() {
    }

    /**
     * Aplica um multiplicador temporário sobre o TOTAL do atributo (ex.:
     * multiplier=2.0 → dobra o valor atual do atributo por `durationMillis`).
     * modifierId deve ser único por (jogador, skill, atributo) - use um
     * UUID fixo por combinação skill+atributo (mesmo padrão de
     * StatAttributeHandler), assim reativar a skill só reinicia a duração
     * em vez de empilhar modificadores.
     */
    public static void applyTimedMultiplier(LivingEntity entity, Attribute attribute, UUID modifierId,
                                            String name, double multiplier, long durationMillis) {
        ModifiableAttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier existing = instance.getModifier(modifierId);
        if (existing != null) {
            instance.removeModifier(existing);
        }
        // MULTIPLY_TOTAL soma (multiplier - 1) aos multiplicadores existentes e
        // aplica sobre (base + ADDITION) - ver AttributeModifier.Operation.
        instance.addTransientModifier(new AttributeModifier(modifierId, name, multiplier - 1D, AttributeModifier.Operation.MULTIPLY_TOTAL));

        ACTIVE.computeIfAbsent(entity.getUUID(), k -> new ArrayList<>())
                .add(new ActiveBuff(attribute, modifierId, System.currentTimeMillis() + durationMillis));
    }

    /** Remove imediatamente (ex.: ao sair da Forma de Névoa antes do tempo, se algum dia for necessário). */
    public static void clear(LivingEntity entity, Attribute attribute, UUID modifierId) {
        ModifiableAttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier existing = instance.getModifier(modifierId);
        if (existing != null) {
            instance.removeModifier(existing);
        }
        List<ActiveBuff> list = ACTIVE.get(entity.getUUID());
        if (list != null) {
            list.removeIf(b -> b.modifierId.equals(modifierId));
        }
    }

    /** Chamado periodicamente (ver SkillTickEvents) para remover buffs vencidos. */
    public static void tick(LivingEntity entity) {
        List<ActiveBuff> list = ACTIVE.get(entity.getUUID());
        if (list == null || list.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        List<ActiveBuff> expired = new ArrayList<>();
        for (ActiveBuff buff : list) {
            if (now >= buff.expireAtMillis) {
                expired.add(buff);
            }
        }
        for (ActiveBuff buff : expired) {
            ModifiableAttributeInstance instance = entity.getAttribute(buff.attribute);
            if (instance != null) {
                AttributeModifier existing = instance.getModifier(buff.modifierId);
                if (existing != null) {
                    instance.removeModifier(existing);
                }
            }
        }
        list.removeAll(expired);
    }

    public static void clearAll(UUID entityId) {
        ACTIVE.remove(entityId);
    }
}
