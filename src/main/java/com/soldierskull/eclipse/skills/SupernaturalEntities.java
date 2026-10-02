package com.soldierskull.eclipse.skills;

import java.util.HashSet;
import java.util.Set;

import com.soldierskull.eclipse.entity.supernatural.ISupernaturalMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

/**
 * Quais tipos de entidade contam como "criatura sobrenatural" para
 * efeitos que dependem disso (Conhecimento Anatômico, Ritual de
 * Contenção, Sentidos Vampíricos, Conhecimento de Monstros, etc.).
 *
 * ATUALIZADO: o Ghost foi adicionado ao set (era o TODO pendente), e
 * agora isSupernatural() também reconhece qualquer entidade que
 * implemente ISupernaturalMob - ou seja, os 13 mobs da Fase 14
 * (Abismo + Overworld) já contam automaticamente, sem precisar listar
 * cada um aqui à mão. O set abaixo continua existindo só para os mobs
 * vanilla "sobrenaturais" que não têm essa interface.
 */
public final class SupernaturalEntities {

    private static final Set<EntityType<?>> SUPERNATURAL = new HashSet<>();

    static {
        SUPERNATURAL.add(EntityType.ZOMBIE);
        SUPERNATURAL.add(EntityType.SKELETON);
        SUPERNATURAL.add(EntityType.WITCH);
        SUPERNATURAL.add(EntityType.WITHER_SKELETON);
        SUPERNATURAL.add(EntityType.PHANTOM);
        // O Ghost NAO entra mais nesta lista: ele implementa ISupernaturalMob,
        // igual aos outros mobs da categoria, e isSupernatural() ja o pega por
        // ai. Alem de redundante, o .get() aqui rodava em bloco static e podia
        // estourar se a classe fosse carregada antes do fim do registro.
    }

    private SupernaturalEntities() {
    }

    public static boolean isSupernatural(Entity entity) {
        return entity instanceof ISupernaturalMob || SUPERNATURAL.contains(entity.getType());
    }
}

