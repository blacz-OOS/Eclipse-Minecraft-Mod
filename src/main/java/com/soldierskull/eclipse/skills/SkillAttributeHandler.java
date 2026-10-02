package com.soldierskull.eclipse.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Skills PASSIVAS que concedem um multiplicador permanente de atributo
 * (ex.: Velocidade Predatória: velocidade x1,8) usam este handler, no
 * mesmo padrão de StatAttributeHandler (UUID fixo por skill, reaplicado
 * no login/respawn porque atributos não são salvos em NBT).
 *
 * Só cobre as skills passivas que mapeiam limpo pra um atributo vanilla
 * (velocidade, dano de ataque, armadura). Passivas com efeito mais
 * complexo (detecção, marcas, receitas) NÃO passam por aqui - ver
 * VampireSkillEffects/WerewolfSkillEffects/etc. para o que cada uma
 * realmente faz.
 */
public final class SkillAttributeHandler {

    private static final Map<String, Attribute> ATTRIBUTE_BY_SKILL = new HashMap<>();
    private static final Map<String, UUID> MODIFIER_ID_BY_SKILL = new HashMap<>();
    private static final Map<String, Double> MULTIPLIER_BY_SKILL = new HashMap<>();

    private static void register(String internalId, Attribute attribute, double multiplier) {
        ATTRIBUTE_BY_SKILL.put(internalId, attribute);
        MODIFIER_ID_BY_SKILL.put(internalId, UUID.nameUUIDFromBytes(("eclipse.skill." + internalId).getBytes()));
        MULTIPLIER_BY_SKILL.put(internalId, multiplier);
    }

    static {
        register("vampiro_velocidade_predatoria", Attributes.MOVEMENT_SPEED, 1.8D);
        register("lobisomem_forca_bestial", Attributes.ATTACK_DAMAGE, 2.0D);
        register("lobisomem_pele_de_ferro", Attributes.ARMOR, 2.5D);
        register("lobisomem_predador_alfa_dano", Attributes.ATTACK_DAMAGE, 3.0D);
        register("lobisomem_predador_alfa_velocidade", Attributes.MOVEMENT_SPEED, 2.0D);
        register("lobisomem_predador_alfa_resistencia", Attributes.ARMOR, 2.0D);
    }

    private SkillAttributeHandler() {
    }

    /** Reaplica TODAS as skills passivas com atributo permanente que o jogador já tem desbloqueadas. Chamar no login/respawn. */
    public static void reapplyAll(PlayerEntity player, PlayerStats stats) {
        for (String internalId : ATTRIBUTE_BY_SKILL.keySet()) {
            boolean unlocked = stats.hasUnlockedSkill(baseSkillId(internalId));
            apply(player, internalId, unlocked);
        }
    }

    /** Liga/desliga o modificador permanente de uma entrada específica. Chamar ao desbloquear a skill correspondente. */
    public static void apply(PlayerEntity player, String internalId, boolean active) {
        Attribute attribute = ATTRIBUTE_BY_SKILL.get(internalId);
        if (attribute == null) {
            return;
        }
        ModifiableAttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        UUID modifierId = MODIFIER_ID_BY_SKILL.get(internalId);
        AttributeModifier existing = instance.getModifier(modifierId);
        if (existing != null) {
            instance.removeModifier(existing);
        }
        if (active) {
            double multiplier = MULTIPLIER_BY_SKILL.get(internalId);
            instance.addPermanentModifier(new AttributeModifier(modifierId, "eclipse.skill." + internalId, multiplier - 1D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    /** Reaplica (ou desliga) uma única entrada a partir do id real da skill - usado no onUnlock de cada SkillEffect. */
    public static void applyForSkill(PlayerEntity player, String realSkillId, boolean active) {
        for (Map.Entry<String, Attribute> entry : ATTRIBUTE_BY_SKILL.entrySet()) {
            if (baseSkillId(entry.getKey()).equals(realSkillId)) {
                apply(player, entry.getKey(), active);
            }
        }
    }

    /** Fábrica: SkillEffect cujo onUnlock liga o atributo permanente correspondente (usado no registro da SkillTree). */
    public static SkillEffect asEffect(String realSkillId) {
        return new SkillEffect() {
            @Override
            public void onUnlock(net.minecraft.entity.player.ServerPlayerEntity player, PlayerStats stats) {
                applyForSkill(player, realSkillId, true);
            }
        };
    }

    // "lobisomem_predador_alfa_dano"/"_velocidade" sao 2 entradas internas pra
    // 1 skill so (Predador Alfa mexe em 2 atributos ao mesmo tempo) - mapeia
    // de volta pro id real da skill pra checar hasUnlockedSkill().
    private static String baseSkillId(String internalId) {
        if (internalId.startsWith("lobisomem_predador_alfa")) {
            return "lobisomem_predador_alfa";
        }
        return internalId;
    }
}
