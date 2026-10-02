package com.soldierskull.eclipse.entity.npc.hunter;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.world.World;

/**
 * NPC Caçador (Fase 13 - Hunter Camp). Fica perto da fogueira, passa
 * impressão de estar descansando (spec original: "Isso cria a
 * impressão de que estão descansando").
 *
 * ETAPA 3: agora dá diálogo de verdade ao clique direito - ver
 * {@link HunterDialogue} e
 * {@link com.soldierskull.eclipse.entity.npc.dialogue.NPCInteractionHandler}
 * (a interação é tratada lá via evento do Forge, não aqui na entidade).
 *
 * COMPORTAMENTO (decisão registrada, não estava especificado): neutro -
 * nunca ataca o jogador por iniciativa própria, mas revida se atacado
 * (HurtByTargetGoal). Se quiser hostil ou totalmente pacífico mesmo sob
 * ataque, avisa que eu ajusto.
 */
public class HunterNPCEntity extends CreatureEntity {

    public HunterNPCEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return CreatureEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 0.7D, 0.05F));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public CreatureAttribute getMobType() {
        return CreatureAttribute.UNDEFINED;
    }
}
