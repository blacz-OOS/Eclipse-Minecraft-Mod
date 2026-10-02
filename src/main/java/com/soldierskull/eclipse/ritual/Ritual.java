package com.soldierskull.eclipse.ritual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

/**
 * Definição de um ritual - dado imutável e registrado (ver
 * {@code RitualRegistry}, próxima etapa), NUNCA a execução em si (isso é
 * {@link RitualContext}, que vive no TileEntity do altar). Um único
 * objeto {@code Ritual} é compartilhado por todos os altares
 * compatíveis com sua {@link RitualCategory} - decisão confirmada,
 * Bloco C/#37-45 da conversa original.
 *
 * Construído via {@link Builder} pra deixar a declaração de cada
 * ritual legível, ex. (channel_power, Bloco F):
 *
 * <pre>
 * Ritual.builder(new ResourceLocation("eclipse", "channel_power"),
 *         new StringTextComponent("Channel Power"), RitualCategory.GENERAL)
 *     .circleSize(RitualCircleSize.SMALL)
 *     .durationTicks(100)
 *     .requiresInitiatorNearby(true)
 *     .build();
 * </pre>
 */
public class Ritual {

    private final ResourceLocation id;
    private final ITextComponent displayName;
    private final RitualCategory category;
    private final RitualCircleSize circleSize;
    private final int ritualPowerCost;
    private final int durationTicks;
    @Nullable
    private final RitualIngredient primaryIngredient;
    private final List<RitualIngredient> offerings;
    private final List<RitualRequirement> requirements;
    private final List<RitualEffect> effects;
    private final boolean requiresInitiatorNearby;

    private Ritual(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.category = builder.category;
        this.circleSize = builder.circleSize;
        this.ritualPowerCost = builder.ritualPowerCost;
        this.durationTicks = builder.durationTicks;
        this.primaryIngredient = builder.primaryIngredient;
        this.offerings = Collections.unmodifiableList(new ArrayList<>(builder.offerings));
        this.requirements = Collections.unmodifiableList(new ArrayList<>(builder.requirements));
        this.effects = Collections.unmodifiableList(new ArrayList<>(builder.effects));
        this.requiresInitiatorNearby = builder.requiresInitiatorNearby;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public ITextComponent getDisplayName() {
        return this.displayName;
    }

    public RitualCategory getCategory() {
        return this.category;
    }

    public RitualCircleSize getCircleSize() {
        return this.circleSize;
    }

    public int getRitualPowerCost() {
        return this.ritualPowerCost;
    }

    public int getDurationTicks() {
        return this.durationTicks;
    }

    @Nullable
    public RitualIngredient getPrimaryIngredient() {
        return this.primaryIngredient;
    }

    public List<RitualIngredient> getOfferings() {
        return this.offerings;
    }

    public List<RitualRequirement> getRequirements() {
        return this.requirements;
    }

    public List<RitualEffect> getEffects() {
        return this.effects;
    }

    public boolean requiresInitiatorNearby() {
        return this.requiresInitiatorNearby;
    }

    /** Testa todos os requirements; retorna o primeiro que falhou (null = todos passaram). */
    @Nullable
    public RitualRequirement findFailingRequirement(RitualContext context) {
        for (RitualRequirement requirement : this.requirements) {
            if (!requirement.test(context)) {
                return requirement;
            }
        }
        return null;
    }

    public static Builder builder(ResourceLocation id, ITextComponent displayName, RitualCategory category) {
        return new Builder(id, displayName, category);
    }

    public static Builder builder(ResourceLocation id, String displayName, RitualCategory category) {
        return new Builder(id, new StringTextComponent(displayName), category);
    }

    public static class Builder {

        private final ResourceLocation id;
        private final ITextComponent displayName;
        private final RitualCategory category;
        private RitualCircleSize circleSize = RitualCircleSize.SMALL;
        private int ritualPowerCost = 0;
        private int durationTicks = 100; // 5s a 20 ticks/s
        @Nullable
        private RitualIngredient primaryIngredient = null;
        private final List<RitualIngredient> offerings = new ArrayList<>();
        private final List<RitualRequirement> requirements = new ArrayList<>();
        private final List<RitualEffect> effects = new ArrayList<>();
        private boolean requiresInitiatorNearby = false;

        private Builder(ResourceLocation id, ITextComponent displayName, RitualCategory category) {
            this.id = id;
            this.displayName = displayName;
            this.category = category;
        }

        public Builder circleSize(RitualCircleSize circleSize) {
            this.circleSize = circleSize;
            return this;
        }

        public Builder ritualPowerCost(int ritualPowerCost) {
            this.ritualPowerCost = ritualPowerCost;
            return this;
        }

        public Builder durationTicks(int durationTicks) {
            this.durationTicks = durationTicks;
            return this;
        }

        public Builder primaryIngredient(RitualIngredient primaryIngredient) {
            this.primaryIngredient = primaryIngredient;
            return this;
        }

        public Builder offering(RitualIngredient offering) {
            this.offerings.add(offering);
            return this;
        }

        public Builder requirement(RitualRequirement requirement) {
            this.requirements.add(requirement);
            return this;
        }

        public Builder effect(RitualEffect effect) {
            this.effects.add(effect);
            return this;
        }

        public Builder requiresInitiatorNearby(boolean requiresInitiatorNearby) {
            this.requiresInitiatorNearby = requiresInitiatorNearby;
            return this;
        }

        public Ritual build() {
            return new Ritual(this);
        }
    }
}
