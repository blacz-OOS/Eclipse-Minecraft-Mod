package com.soldierskull.eclipse.ritual;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

import com.soldierskull.eclipse.block.ModBlocks;
import com.soldierskull.eclipse.faction.FactionProgression;
import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.item.ModItems;
import com.soldierskull.eclipse.ritual.effect.AddFactionXpEffect;
import com.soldierskull.eclipse.ritual.effect.AddRitualPowerEffect;
import com.soldierskull.eclipse.ritual.effect.ConsumeCorruptionEffect;
import com.soldierskull.eclipse.ritual.effect.GiveItemEffect;
import com.soldierskull.eclipse.ritual.effect.TriggerEclipseEffect;
import com.soldierskull.eclipse.ritual.requirement.FactionLevelRequirement;
import com.soldierskull.eclipse.ritual.requirement.FactionRequirement;
import com.soldierskull.eclipse.ritual.requirement.NightRequirement;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

/**
 * Registro estático dos rituais do Eclipse (Etapa 4 da ordem de
 * implementação, adiantada aqui só com o par de teste do Bloco F -
 * {@code channel_power} e {@code abyss_crystal_charging} - pra validar
 * o motor antes do {@code AltarBlock}/{@code AltarTileEntity} existirem).
 *
 * Cada {@link Ritual} é uma definição compartilhada - não confundir com
 * {@link RitualContext}, que é a execução real num altar específico.
 */
public final class RitualRegistry {

    private static final Map<ResourceLocation, Ritual> RITUALS = new LinkedHashMap<>();

    public static final ResourceLocation CHANNEL_POWER_ID = new ResourceLocation("eclipse", "channel_power");
    public static final ResourceLocation ABYSS_CRYSTAL_CHARGING_ID = new ResourceLocation("eclipse", "abyss_crystal_charging");
    public static final ResourceLocation ECLIPSE_RITUAL_ID = new ResourceLocation("eclipse", "eclipse_ritual");
    public static final ResourceLocation CULTIST_INITIATION_ID = new ResourceLocation("eclipse", "cultist_initiation");

    private RitualRegistry() {
    }

    public static void bootstrap() {
        register(buildChannelPower());
        register(buildAbyssCrystalCharging());
        register(buildEclipseRitual());
        register(buildCultistInitiation());
    }

    private static Ritual register(Ritual ritual) {
        RITUALS.put(ritual.getId(), ritual);
        return ritual;
    }

    @Nullable
    public static Ritual get(ResourceLocation id) {
        return RITUALS.get(id);
    }

    public static Map<ResourceLocation, Ritual> getAll() {
        return Collections.unmodifiableMap(RITUALS);
    }

    /**
     * Ritual 0 (Bloco F) - bootstrap de Ritual Power. Sem ingrediente,
     * sem custo de power (senão seria "preciso de power pra conseguir
     * power" - decisão confirmada). Converte 50 Corruption do
     * jogador em +25 Ritual Power do altar (2:1, com perda).
     */
    private static Ritual buildChannelPower() {
        return Ritual.builder(CHANNEL_POWER_ID, "Channel Power", RitualCategory.GENERAL)
                .circleSize(RitualCircleSize.SMALL)
                .durationTicks(100) // 5s
                .ritualPowerCost(0)
                .requiresInitiatorNearby(true)
                .effect(new ConsumeCorruptionEffect(50))
                .effect(new AddRitualPowerEffect(25))
                .build();
    }

    /**
     * Ritual 1 (Bloco F) - primeiro ritual "de item" de verdade.
     * Reaproveita Abyss Crystal, Abyss Dust e Charged Abyss Crystal já
     * registrados em {@link ModBlocks}/{@link ModItems} - nenhum item
     * novo precisou ser criado pra este ritual. O Charged Abyss Crystal
     * entregue nasce com NBT {@code Charges: 3} (Bloco F/#49).
     */
    private static Ritual buildAbyssCrystalCharging() {
        return Ritual.builder(ABYSS_CRYSTAL_CHARGING_ID, "Abyss Crystal Charging", RitualCategory.GENERAL)
                .circleSize(RitualCircleSize.SMALL)
                .durationTicks(100) // 5s
                .ritualPowerCost(10)
                .primaryIngredient(RitualIngredient.of(Ingredient.of(ModBlocks.ABYSS_CRYSTAL_ITEM.get()), 1))
                .offering(RitualIngredient.of(Ingredient.of(ModItems.ABYSS_DUST.get()), 2))
                .requirement(new NightRequirement())
                .effect(new GiveItemEffect(RitualRegistry::createChargedAbyssCrystal))
                .build();
    }

    private static ItemStack createChargedAbyssCrystal() {
        ItemStack stack = new ItemStack(ModBlocks.CHARGED_ABYSS_CRYSTAL_ITEM.get());
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putInt("Charges", 3);
        return stack;
    }

    /**
     * Ritual exclusivo prioritário do Abyss Altar (Bloco D/#31) - dispara
     * o evento Eclipse manualmente. O círculo não é verificado por
     * chalk/anel aqui - é a flag {@code linked} do multibloco
     * (ver {@code AbyssAltarCoreTileEntity#isCircleValid}), então
     * {@link RitualCircleSize#LARGE} serve só de documentação/futuro
     * reaproveitamento, não afeta a validação neste altar.
     *
     * TRAVADO a Cultista de nível máximo (decisão confirmada: nenhum
     * ritual transforma raça, e só um Cultista no rank máximo pode
     * acessar o poder mais extremo do Abyss Altar).
     */
    private static Ritual buildEclipseRitual() {
        return Ritual.builder(ECLIPSE_RITUAL_ID, "Eclipse Ritual", RitualCategory.ECLIPSE)
                .circleSize(RitualCircleSize.LARGE)
                .durationTicks(600) // 30s - ritual climático, não instantâneo
                .ritualPowerCost(500)
                .primaryIngredient(RitualIngredient.of(Ingredient.of(ModItems.ABYSS_SHARD.get()), 3))
                .offering(RitualIngredient.of(Ingredient.of(ModItems.ABYSS_DUST.get()), 4))
                .requirement(new NightRequirement())
                .requirement(new FactionRequirement(FactionType.CULTISTS))
                .requirement(new FactionLevelRequirement(FactionProgression.MAX_FACTION_LEVEL))
                .requiresInitiatorNearby(true)
                .effect(new TriggerEclipseEffect(10L * 60L * 1000L)) // 10 minutos de eclipse
                .build();
    }

    /**
     * Progressão de facção (Bloco E) - NÃO transforma raça (decisão
     * confirmada: só o gatilho automático do TransformationManager faz
     * isso). É um ritual de "treino"/avanço repetível pra Cultistas já
     * existentes subirem de nível rumo ao máximo, que por sua vez é
     * exigido pelo Eclipse Ritual acima.
     */
    private static Ritual buildCultistInitiation() {
        return Ritual.builder(CULTIST_INITIATION_ID, "Cultist Initiation", RitualCategory.CULTIST)
                .circleSize(RitualCircleSize.MEDIUM)
                .durationTicks(200) // 10s
                .ritualPowerCost(100)
                .primaryIngredient(RitualIngredient.of(Ingredient.of(ModItems.ABYSS_SHARD.get()), 1))
                .offering(RitualIngredient.of(Ingredient.of(ModItems.ABYSS_DUST.get()), 2))
                .requirement(new NightRequirement())
                .requirement(new FactionRequirement(FactionType.CULTISTS))
                .effect(new AddFactionXpEffect(50))
                .build();
    }
}
