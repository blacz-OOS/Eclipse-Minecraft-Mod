package com.soldierskull.eclipse.entity.supernatural;

import com.soldierskull.eclipse.Eclipse;
import com.soldierskull.eclipse.entity.supernatural.abyssalhand.AbyssalHandEntity;
import com.soldierskull.eclipse.entity.supernatural.aetherparasite.AetherParasiteEntity;
import com.soldierskull.eclipse.entity.supernatural.ashghoul.AshGhoulEntity;
import com.soldierskull.eclipse.entity.supernatural.bloodworm.BloodWormEntity;
import com.soldierskull.eclipse.entity.supernatural.emberimp.EmberImpEntity;
import com.soldierskull.eclipse.entity.supernatural.ghost.GhostEntity;
import com.soldierskull.eclipse.entity.supernatural.voidobserver.VoidBoltEntity;
import com.soldierskull.eclipse.entity.supernatural.voidobserver.VoidObserverEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registro de TODOS os mobs sobrenaturais do Eclipse.
 *
 * O Ghost foi movido pra ca (antes vivia em entity.ghost.ModEntityTypes,
 * um DeferredRegister separado - e havia ainda um terceiro,
 * GhostEntityRegistry, orfao, que registrava um segundo EntityType com o
 * mesmo nome "ghost"). Agora ha um unico DeferredRegister de entidades
 * sobrenaturais, com atributos registrados no mesmo lugar, igual aos
 * outros mobs.
 *
 * Historico: Fase 14, lote 1 (6 mobs do Abismo).
 * Os 7 mobs do Overworld (dry_body, mist_ghoulin, corrupted_saci,
 * sulfur_hound, forest_specter, mapinguari, wendigo) entram no lote 2.
 */
@Mod.EventBusSubscriber(modid = Eclipse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSupernaturalEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, Eclipse.MOD_ID);

    public static final RegistryObject<EntityType<AbyssalHandEntity>> ABYSSAL_HAND =
            ENTITY_TYPES.register("abyssal_hand", () -> EntityType.Builder
                    .of(AbyssalHandEntity::new, EntityClassification.MONSTER)
                    .sized(0.9F, 0.5F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "abyssal_hand").toString()));

    public static final RegistryObject<EntityType<BloodWormEntity>> BLOOD_WORM =
            ENTITY_TYPES.register("blood_worm", () -> EntityType.Builder
                    .of(BloodWormEntity::new, EntityClassification.MONSTER)
                    .sized(1.0F, 0.8F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "blood_worm").toString()));

    public static final RegistryObject<EntityType<AetherParasiteEntity>> AETHER_PARASITE =
            ENTITY_TYPES.register("aether_parasite", () -> EntityType.Builder
                    .of(AetherParasiteEntity::new, EntityClassification.MONSTER)
                    .sized(0.3F, 0.3F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "aether_parasite").toString()));

    public static final RegistryObject<EntityType<AshGhoulEntity>> ASH_GHOUL =
            ENTITY_TYPES.register("ash_ghoul", () -> EntityType.Builder
                    .of(AshGhoulEntity::new, EntityClassification.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "ash_ghoul").toString()));

    public static final RegistryObject<EntityType<VoidObserverEntity>> VOID_OBSERVER =
            ENTITY_TYPES.register("void_observer", () -> EntityType.Builder
                    .of(VoidObserverEntity::new, EntityClassification.MONSTER)
                    .sized(0.8F, 0.8F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "void_observer").toString()));

    public static final RegistryObject<EntityType<VoidBoltEntity>> VOID_BOLT =
            ENTITY_TYPES.register("void_bolt", () -> EntityType.Builder
                    .<VoidBoltEntity>of(VoidBoltEntity::new, EntityClassification.MISC)
                    .sized(0.25F, 0.25F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "void_bolt").toString()));

    public static final RegistryObject<EntityType<EmberImpEntity>> EMBER_IMP =
            ENTITY_TYPES.register("ember_imp", () -> EntityType.Builder
                    .of(EmberImpEntity::new, EntityClassification.MONSTER)
                    .sized(0.5F, 0.75F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "ember_imp").toString()));

    // ---------- Ghost (integrado a categoria sobrenatural) ----------
    // Hitbox mantida exatamente como estava no registry antigo (0.6 x 1.95).
    public static final RegistryObject<EntityType<GhostEntity>> GHOST =
            ENTITY_TYPES.register("ghost", () -> EntityType.Builder
                    .of(GhostEntity::new, EntityClassification.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "ghost").toString()));

    // ---------- Fase 14, lote 2 - mobs do Overworld ----------

    public static final RegistryObject<EntityType<com.soldierskull.eclipse.entity.supernatural.drybody.DryBodyEntity>> DRY_BODY =
            ENTITY_TYPES.register("dry_body", () -> EntityType.Builder
                    .of(com.soldierskull.eclipse.entity.supernatural.drybody.DryBodyEntity::new, EntityClassification.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "dry_body").toString()));

    public static final RegistryObject<EntityType<com.soldierskull.eclipse.entity.supernatural.mistghoulin.MistGhoulinEntity>> MIST_GHOULIN =
            ENTITY_TYPES.register("mist_ghoulin", () -> EntityType.Builder
                    .of(com.soldierskull.eclipse.entity.supernatural.mistghoulin.MistGhoulinEntity::new, EntityClassification.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "mist_ghoulin").toString()));

    public static final RegistryObject<EntityType<com.soldierskull.eclipse.entity.supernatural.corruptedsaci.CorruptedSaciEntity>> CORRUPTED_SACI =
            ENTITY_TYPES.register("corrupted_saci", () -> EntityType.Builder
                    .of(com.soldierskull.eclipse.entity.supernatural.corruptedsaci.CorruptedSaciEntity::new, EntityClassification.MONSTER)
                    .sized(0.5F, 0.75F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "corrupted_saci").toString()));

    public static final RegistryObject<EntityType<com.soldierskull.eclipse.entity.supernatural.sulfurhound.SulfurHoundEntity>> SULFUR_HOUND =
            ENTITY_TYPES.register("sulfur_hound", () -> EntityType.Builder
                    .of(com.soldierskull.eclipse.entity.supernatural.sulfurhound.SulfurHoundEntity::new, EntityClassification.MONSTER)
                    .sized(0.9F, 0.8F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "sulfur_hound").toString()));

    public static final RegistryObject<EntityType<com.soldierskull.eclipse.entity.supernatural.forestspecter.ForestSpecterEntity>> FOREST_SPECTER =
            ENTITY_TYPES.register("forest_specter", () -> EntityType.Builder
                    .of(com.soldierskull.eclipse.entity.supernatural.forestspecter.ForestSpecterEntity::new, EntityClassification.MONSTER)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "forest_specter").toString()));

    public static final RegistryObject<EntityType<com.soldierskull.eclipse.entity.supernatural.mapinguari.MapinguariEntity>> MAPINGUARI =
            ENTITY_TYPES.register("mapinguari", () -> EntityType.Builder
                    .of(com.soldierskull.eclipse.entity.supernatural.mapinguari.MapinguariEntity::new, EntityClassification.MONSTER)
                    .sized(1.6F, 3.5F)
                    .fireImmune()
                    .build(new ResourceLocation(Eclipse.MOD_ID, "mapinguari").toString()));

    public static final RegistryObject<EntityType<com.soldierskull.eclipse.entity.supernatural.wendigo.WendigoEntity>> WENDIGO =
            ENTITY_TYPES.register("wendigo", () -> EntityType.Builder
                    .of(com.soldierskull.eclipse.entity.supernatural.wendigo.WendigoEntity::new, EntityClassification.MONSTER)
                    .sized(0.8F, 2.4F)
                    .build(new ResourceLocation(Eclipse.MOD_ID, "wendigo").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    /** Registra os atributos (createAttributes) de cada entidade viva. */
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ABYSSAL_HAND.get(), AbyssalHandEntity.createAttributes().build());
        event.put(BLOOD_WORM.get(), BloodWormEntity.createAttributes().build());
        event.put(AETHER_PARASITE.get(), AetherParasiteEntity.createAttributes().build());
        event.put(ASH_GHOUL.get(), AshGhoulEntity.createAttributes().build());
        event.put(VOID_OBSERVER.get(), VoidObserverEntity.createAttributes().build());
        event.put(EMBER_IMP.get(), EmberImpEntity.createAttributes().build());
        event.put(GHOST.get(), GhostEntity.createAttributes().build());

        // Fase 14, lote 2
        event.put(DRY_BODY.get(), com.soldierskull.eclipse.entity.supernatural.drybody.DryBodyEntity.createAttributes().build());
        event.put(MIST_GHOULIN.get(), com.soldierskull.eclipse.entity.supernatural.mistghoulin.MistGhoulinEntity.createAttributes().build());
        event.put(CORRUPTED_SACI.get(), com.soldierskull.eclipse.entity.supernatural.corruptedsaci.CorruptedSaciEntity.createAttributes().build());
        event.put(SULFUR_HOUND.get(), com.soldierskull.eclipse.entity.supernatural.sulfurhound.SulfurHoundEntity.createAttributes().build());
        event.put(FOREST_SPECTER.get(), com.soldierskull.eclipse.entity.supernatural.forestspecter.ForestSpecterEntity.createAttributes().build());
        event.put(MAPINGUARI.get(), com.soldierskull.eclipse.entity.supernatural.mapinguari.MapinguariEntity.createAttributes().build());
        event.put(WENDIGO.get(), com.soldierskull.eclipse.entity.supernatural.wendigo.WendigoEntity.createAttributes().build());
    }
}
