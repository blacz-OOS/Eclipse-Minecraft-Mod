package com.soldierskull.eclipse.item;

import com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry<Item>) ForgeRegistries.ITEMS, "eclipse");


    //drops de mobs


    public static final RegistryObject<Item> SANGUINE_GLAND = ITEMS.register("sanguine_gland",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> ECTOPLASMIC_DUST = ITEMS.register("ectoplasmic_dust",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> SPECTRAL_SHROUD =ITEMS.register("spectral_shroud",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> CORRUPTED_LEATHER = ITEMS.register("corrupted_leather",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> CORRUPTED_BONE = ITEMS.register("corrupted_bone",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> MAPINGUARI_TONGUE = ITEMS.register("mapinguari_tongue",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> WENDIGO_HORN = ITEMS.register("wendigo_horn",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> ABYSS_DUST = ITEMS.register("abyss_dust",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> ABYSS_CORE = ITEMS.register("abyss_core",
            () -> new AbyssShardItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(16), 20));
    public static final RegistryObject<Item> VAMPIRE_FANG = ITEMS.register("vampire_fang",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> VAMPIRE_HEART = ITEMS.register("vampire_heart",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));
    public static final RegistryObject<Item> WEREWOLF_FANG = ITEMS.register("werewolf_fang",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> WEREWOLF_FUR = ITEMS.register("werewolf_fur",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> WEREWOLF_LIVER = ITEMS.register("werewolf_liver",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));
    public static final RegistryObject<Item> ALPHA_FANG = ITEMS.register("alpha_fang",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));


    //craftaveis
//caçador
    public static final RegistryObject<Item> SILVER_SWORD = ITEMS.register("silver_sword",
            () -> new com.soldierskull.eclipse.item.SilverSwordItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));

    public static final RegistryObject<Item> SILVER_DAGGER = ITEMS.register("silver_dagger",
            () -> new com.soldierskull.eclipse.item.SilverDaggerItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));

    public static final RegistryObject<Item> HOLY_WATER = ITEMS.register("holy_water",
            () -> new com.soldierskull.eclipse.item.HolyWaterItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(16)));

    public static final RegistryObject<Item> LUNAR_ESSENCE = ITEMS.register("lunar_essence",
            () -> new com.soldierskull.eclipse.item.LunarEssenceItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(16)));
    //minério

    public static final RegistryObject<Item> RAW_SILVER = ITEMS.register("raw_silver",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> SILVER = ITEMS.register("silver",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> PURE_SILVER = ITEMS.register("pure_silver",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));



    // Fase 14, lote 1 - spawn eggs dos mobs do Abismo (uteis pra testar antes do spawn natural estar afinado)
    public static final RegistryObject<Item> ABYSSAL_HAND_SPAWN_EGG = ITEMS.register("abyssal_hand_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.ABYSSAL_HAND,
                    0x1a1a1a, 0x5c0000, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> BLOOD_WORM_SPAWN_EGG = ITEMS.register("blood_worm_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.BLOOD_WORM,
                    0x5c0000, 0x1a1a1a, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> AETHER_PARASITE_SPAWN_EGG = ITEMS.register("aether_parasite_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.AETHER_PARASITE,
                    0x2e2e6e, 0x9ad1ff, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> ASH_GHOUL_SPAWN_EGG = ITEMS.register("ash_ghoul_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.ASH_GHOUL,
                    0x4a4a4a, 0x8c7a6b, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> VOID_OBSERVER_SPAWN_EGG = ITEMS.register("void_observer_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.VOID_OBSERVER,
                    0x100010, 0x7a00ff, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> EMBER_IMP_SPAWN_EGG = ITEMS.register("ember_imp_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.EMBER_IMP,
                    0x330000, 0xff6600, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));

    // Fase 14, lote 2 - spawn eggs dos mobs do Overworld
    public static final RegistryObject<Item> DRY_BODY_SPAWN_EGG = ITEMS.register("dry_body_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.DRY_BODY,
                    0xc9b48f, 0x5c4a35, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> MIST_GHOULIN_SPAWN_EGG = ITEMS.register("mist_ghoulin_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.MIST_GHOULIN,
                    0x9aa79a, 0x4a5a4a, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> CORRUPTED_SACI_SPAWN_EGG = ITEMS.register("corrupted_saci_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.CORRUPTED_SACI,
                    0x8b0000, 0x1a1a1a, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> SULFUR_HOUND_SPAWN_EGG = ITEMS.register("sulfur_hound_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.SULFUR_HOUND,
                    0x0d0d0d, 0xff2200, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> FOREST_SPECTER_SPAWN_EGG = ITEMS.register("forest_specter_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.FOREST_SPECTER,
                    0xd8f0e0, 0x2d4d3a, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> MAPINGUARI_SPAWN_EGG = ITEMS.register("mapinguari_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.MAPINGUARI,
                    0x4a3520, 0xd94040, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> WENDIGO_SPAWN_EGG = ITEMS.register("wendigo_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.WENDIGO,
                    0xe8e8e8, 0x3a3a3a, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> GHOST_SPAWN_EGG = ITEMS.register("ghost_spawn_egg",
            () -> new ForgeSpawnEggItem(ModSupernaturalEntities.GHOST, 0x333333, 0x708090,
                    new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));

    // NPCs de estrutura (Fase 13)
    public static final RegistryObject<Item> HUNTER_NPC_SPAWN_EGG = ITEMS.register("hunter_npc_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.npc.ModNPCEntities.HUNTER_NPC,
                    0x5c4a35, 0x8a6d3f, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> CULTIST_NPC_SPAWN_EGG = ITEMS.register("cultist_npc_spawn_egg",
            () -> new ForgeSpawnEggItem(com.soldierskull.eclipse.entity.npc.ModNPCEntities.CULTIST_NPC,
                    0x2a1a2a, 0x7a1a1a, new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));

    //especial
    public static final RegistryObject<Item> ABYSS_SHARD = ITEMS.register("abyss_shard",
            () -> new AbyssShardItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(16)));

    /** Munição Especial (Caçadores): concedido ao desbloquear a skill - ver HunterSkillEffects.MUNICAO_ESPECIAL. */
    public static final RegistryObject<Item> HUNTER_SPECIAL_BOW = ITEMS.register("hunter_special_bow",
            () -> new com.soldierskull.eclipse.item.HunterSpecialBow(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1).durability(5)));

    /** Regeneração Sanguínea/Superior (Vampiro): cura ao beber, ver VampireBloodBottleItem. */
    public static final RegistryObject<Item> VAMPIRE_BLOOD_BOTTLE = ITEMS.register("vampire_blood_bottle",
            () -> new com.soldierskull.eclipse.item.VampireBloodBottleItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(16)));

    /** Ritual do Eclipse (Cultistas): precisa estar na mão pra iniciar o evento - ver CultistSkillEffects.RITUAL_DO_ECLIPSE. */
    public static final RegistryObject<Item> ECLIPSE_ESSENCE = ITEMS.register("eclipse_essence",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));

    // ---- Fase 10: itens dos Caçadores (arma de Treinamento com Prata + consumível de Preparação Alquímica) ----


    /** Cultistas (Fase 10): capacete que aumenta o máximo de Corrupção em 15 enquanto equipado - ver CultistMaskHandler. */
    public static final RegistryObject<Item> CULTIST_MASK = ITEMS.register("cultist_mask",
            () -> new com.soldierskull.eclipse.item.CultistMaskItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));

    // ---- Fase 10: resto da lista de itens (seção 24 do documento original) ----
    // Materiais do Abismo em cadeia de raridade (Dust -> Crystal -> Core),
    // cada um restaurando mais Energia Abissal/Corrupção que o anterior.
    // Dust é só material de crafting (não tem uso direto); Crystal/Core
    // reaproveitam AbyssShardItem com quantidade maior.

    // ABYSS_CRYSTAL saiu daqui - agora é um bloco (ver ModBlocks), sem a
    // função de restaurar corrupção (removida por instrução sua). A
    // versão de item dele (BlockItem) vem automaticamente do registro do
    // bloco, não precisa de entrada separada aqui.

    // Materiais do Eclipse - cadeia similar (Fragment -> Crystal -> Relic),
    // alimentando a economia da Essência do Eclipse (Ritual do Eclipse,
    // Fase 8). Relic é o topo da cadeia - registrado como material puro,
    // sem uso definido ainda (aguardando um sistema que o consuma, ex.
    // Fase 13 - altares/estruturas).
    public static final RegistryObject<Item> ECLIPSE_FRAGMENT = ITEMS.register("eclipse_fragment",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> ECLIPSE_CRYSTAL = ITEMS.register("eclipse_crystal",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> ECLIPSE_RELIC = ITEMS.register("eclipse_relic",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));

    /** Lobisomem (Fase 10): versão "de bolso" da Fúria Lunar, mais fraca e utilizável a qualquer hora - ver LunarEssenceItem. */


    /** Caçadores (Fase 10): versão craftável/consumível da Armadilha de Caçador, sem exigir a skill - ver HunterTrapItem. */
    public static final RegistryObject<Item> HUNTER_TRAP = ITEMS.register("hunter_trap",
            () -> new com.soldierskull.eclipse.item.HunterTrapItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(8)));

    // ---- Materiais SEM função de jogo ainda (registrados só pra existir no
    // jogo, aguardando outras fases): Fang/Heart/Fur dependem de mobs
    // sobrenaturais que ainda não existem (Fase 14); Tome/Journal/Idol
    // dependem do sistema de livros/lore de quest (Fase 11/12); Cultist
    // Mark e Abyssal Relic não têm mecânica concreta definida no
    // documento. NÃO inventei receita, drop nem efeito pra nenhum destes -
    // isso fica pra quando a fase correspondente existir.

    // grimório do eclipse, livro guia para os jogadores
    public static final RegistryObject<Item> ECLIPSE_GRIMOIRE = ITEMS.register("eclipse_grimoire",
            () -> new EclipseGrimoireItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));

     /** Antes era um item sem função; agora abre uma mochila de 7 slots restrita a item de caçador - ver HunterBadgeItem. */
    public static final RegistryObject<Item> HUNTER_BADGE = ITEMS.register("hunter_badge",
            () -> new HunterBadgeItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));
    // BUGFIX: Vampire Tome, Werewolf Tome, Hunter Journal e Forbidden Tome
    // foram removidos - os 4 faziam exatamente a mesma coisa (entregar/
    // aceitar quest, listar rituais), cada um restrito a uma raca/faccao
    // fixa. Unificados no ECLIPSE_GRIMOIRE (ver EclipseGrimoireItem), que
    // le a raca/faccao ATUAL do jogador em vez de depender do item fisico.
    public static final RegistryObject<Item> CULTIST_MARK = ITEMS.register("cultist_mark",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP)));
    public static final RegistryObject<Item> CULTIST_IDOL = ITEMS.register("cultist_idol",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));
    public static final RegistryObject<Item> ABYSSAL_RELIC = ITEMS.register("abyssal_relic",
            () -> new Item(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
