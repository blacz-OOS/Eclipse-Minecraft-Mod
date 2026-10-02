//
// código-fonte recriado a partir de um arquivo .class pelo IntelliJ IDEA
// (com o motor de descompilação Fernflower)
//

package com.soldierskull.eclipse.block;

import java.util.EnumSet;

import com.soldierskull.eclipse.item.ModItemGroup;
import com.soldierskull.eclipse.ritual.RitualCategory;
import com.soldierskull.eclipse.ritual.altar.AbyssAltarCoreBlock;
import com.soldierskull.eclipse.ritual.altar.AltarBlock;
import com.soldierskull.eclipse.ritual.altar.BloodAltarBlock;
import com.soldierskull.eclipse.ritual.altar.MoonAltarBlock;
import net.minecraft.block.Block;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS;
    public static final DeferredRegister<Item> ITEMS;
    public static final RegistryObject<Block> DEADWOOD_LOG;
    public static final RegistryObject<Item> DEADWOOD_LOG_ITEM;
    public static final RegistryObject<Block> DEADWOOD_PLANKS;
    public static final RegistryObject<Item> DEADWOOD_PLANKS_ITEM;
    public static final RegistryObject<Block> SILVER_ORE;
    public static final RegistryObject<Item> SILVER_ORE_ITEM;
    public static final RegistryObject<Block> ABYSS_CRYSTAL;
    public static final RegistryObject<Item> ABYSS_CRYSTAL_ITEM;
    public static final RegistryObject<Block> CHARGED_ABYSS_CRYSTAL;
    public static final RegistryObject<Item> CHARGED_ABYSS_CRYSTAL_ITEM;
    public static final RegistryObject<Block> ALTAR;
    public static final RegistryObject<Item> ALTAR_ITEM;
    public static final RegistryObject<Block> RITUAL_CHALK;
    public static final RegistryObject<Item> RITUAL_CHALK_ITEM;
    public static final RegistryObject<Block> BLOOD_CHALK;
    public static final RegistryObject<Item> BLOOD_CHALK_ITEM;
    public static final RegistryObject<Block> BLOOD_ALTAR;
    public static final RegistryObject<Item> BLOOD_ALTAR_ITEM;
    public static final RegistryObject<Block> MOON_ALTAR;
    public static final RegistryObject<Item> MOON_ALTAR_ITEM;
    public static final RegistryObject<Block> MOON_ALTAR_STONE;
    public static final RegistryObject<Item> MOON_ALTAR_STONE_ITEM;
    public static final RegistryObject<Block> ABYSS_ALTAR_CORE;
    public static final RegistryObject<Item> ABYSS_ALTAR_CORE_ITEM;
    public static final RegistryObject<Block> ABYSSAL_PILLAR;
    public static final RegistryObject<Item> ABYSSAL_PILLAR_ITEM;
    public static final RegistryObject<Block> ABYSSAL_RUNE;
    public static final RegistryObject<Item> ABYSSAL_RUNE_ITEM;
    public static final RegistryObject<Block> ABYSSAL_RUIN_FOUNDATION;
    public static final RegistryObject<Item> ABYSSAL_RUIN_FOUNDATION_ITEM;
    public static final RegistryObject<Block> TRAP;

    public ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }

    static {
        BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "eclipse");
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "eclipse");
        DEADWOOD_LOG = BLOCKS.register("deadwood_log", () -> new RotatedPillarBlock(Properties.of(Material.WOOD).strength(1.8F).sound(SoundType.WOOD).harvestTool(ToolType.AXE)));
        DEADWOOD_LOG_ITEM = ITEMS.register("deadwood_log", () -> new BlockItem((Block)DEADWOOD_LOG.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));
        DEADWOOD_PLANKS = BLOCKS.register("deadwood_planks", () -> new Block(Properties.of(Material.WOOD).strength(1.8F).sound(SoundType.WOOD).harvestTool(ToolType.AXE)));
        DEADWOOD_PLANKS_ITEM = ITEMS.register("deadwood_planks", () -> new BlockItem((Block)DEADWOOD_PLANKS.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));
        SILVER_ORE = BLOCKS.register ("silver_ore", () -> new Block(Properties.of(Material.STONE).strength(2.5F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE)));
        SILVER_ORE_ITEM = ITEMS.register("silver_ore", () -> new BlockItem((Block)SILVER_ORE.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        // Cristal do Abismo (semelhante a ametista - confirmado): forma de
        // cluster, nao solido, gruda na face clicada. Sem funcao de
        // restaurar corrupcao (removida do item por instrucao sua).
        ABYSS_CRYSTAL = BLOCKS.register("abyss_crystal", () -> new com.soldierskull.eclipse.block.AbyssCrystalBlock(
                Properties.of(Material.STONE).strength(1.5F).sound(SoundType.GLASS).noOcclusion()));
        ABYSS_CRYSTAL_ITEM = ITEMS.register("abyss_crystal", () -> new BlockItem((Block) ABYSS_CRYSTAL.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        // Versao "carregada" (confirmado, bloco novo): mesma forma, brilha
        // (luminosidade 12/15) - textura ja fornecida.
        CHARGED_ABYSS_CRYSTAL = BLOCKS.register("charged_abyss_crystal", () -> new com.soldierskull.eclipse.block.AbyssCrystalBlock(
                Properties.of(Material.STONE).strength(1.5F).sound(SoundType.GLASS).noOcclusion().lightLevel(state -> 12)));
        CHARGED_ABYSS_CRYSTAL_ITEM = ITEMS.register("charged_abyss_crystal", () -> new BlockItem((Block) CHARGED_ABYSS_CRYSTAL.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        // Altar comum (Bloco A/#1 da especificacao de rituais): pedra
        // ritualistica, um bloco so, sem GUI. Tem TileEntity (AltarTileEntity).
        ALTAR = BLOCKS.register("altar", () -> new AltarBlock(
                Properties.of(Material.STONE).strength(3.0F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).noOcclusion()));
        ALTAR_ITEM = ITEMS.register("altar", () -> new BlockItem((Block) ALTAR.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        // Chalk usado pra desenhar o perimetro do circulo ritualistico
        // (Bloco A/C/#17-18). Ritual Chalk cobre GENERAL+HUNTER (Altar
        // comum); Blood Chalk cobre BLOOD+VAMPIRE (Blood Altar, Etapa 9).
        // BUGFIX: .noCollission() adicionado - sem isso o chalk usava a
        // colisao PADRAO de Block (cubo cheio de 16x16x16), apesar do
        // modelo visual ter so 0.1 de altura. O jogador tinha que pular
        // em cima dele como um bloco normal. Mesmo mecanismo que o
        // vanilla usa pra trip wire/vinhas sem suporte - zera a colisao
        // por completo, sem afetar o contorno de selecao.
        RITUAL_CHALK = BLOCKS.register("ritual_chalk", () -> new com.soldierskull.eclipse.block.RitualChalkBlock(
                Properties.of(Material.STONE).strength(0.5F).sound(SoundType.STONE).noOcclusion().noCollission(),
                EnumSet.of(RitualCategory.GENERAL, RitualCategory.HUNTER)));
        RITUAL_CHALK_ITEM = ITEMS.register("ritual_chalk", () -> new BlockItem((Block) RITUAL_CHALK.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        BLOOD_CHALK = BLOCKS.register("blood_chalk", () -> new com.soldierskull.eclipse.block.RitualChalkBlock(
                Properties.of(Material.STONE).strength(0.5F).sound(SoundType.STONE).noOcclusion().noCollission(),
                EnumSet.of(RitualCategory.BLOOD, RitualCategory.VAMPIRE)));
        BLOOD_CHALK_ITEM = ITEMS.register("blood_chalk", () -> new BlockItem((Block) BLOOD_CHALK.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        // Blood Altar (Bloco A/#2): pedra negra + ossos + sangue. Reaproveita
        // AltarBlock/AltarTileEntity - so troca categorias/limite de power.
        BLOOD_ALTAR = BLOCKS.register("blood_altar", () -> new BloodAltarBlock(
                Properties.of(Material.STONE).strength(3.0F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).noOcclusion()));
        BLOOD_ALTAR_ITEM = ITEMS.register("blood_altar", () -> new BlockItem((Block) BLOOD_ALTAR.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        // Moon Altar (Bloco A/#3): o bloco central do circulo de pedras.
        MOON_ALTAR = BLOCKS.register("moon_altar", () -> new MoonAltarBlock(
                Properties.of(Material.STONE).strength(3.0F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).noOcclusion().lightLevel(state -> 6)));
        MOON_ALTAR_ITEM = ITEMS.register("moon_altar", () -> new BlockItem((Block) MOON_ALTAR.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        // Pedra do anel do Moon Altar (Bloco A/C/#17) - a propria estrutura
        // e o circulo, sem chalk.
        MOON_ALTAR_STONE = BLOCKS.register("moon_altar_stone", () -> new Block(
                Properties.of(Material.STONE).strength(2.0F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).noOcclusion()));
        MOON_ALTAR_STONE_ITEM = ITEMS.register("moon_altar_stone", () -> new BlockItem((Block) MOON_ALTAR_STONE.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        // Abyss Altar (Bloco D): multibloco 9x9 binario. Core + Pillar +
        // Rune + Foundation (novos); os Crystal Node reaproveitam
        // CHARGED_ABYSS_CRYSTAL, ja registrado acima.
        ABYSS_ALTAR_CORE = BLOCKS.register("abyss_altar_core", () -> new AbyssAltarCoreBlock(
                Properties.of(Material.STONE).strength(5.0F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).noOcclusion().lightLevel(state -> 8)));
        ABYSS_ALTAR_CORE_ITEM = ITEMS.register("abyss_altar_core", () -> new BlockItem((Block) ABYSS_ALTAR_CORE.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        ABYSSAL_PILLAR = BLOCKS.register("abyssal_pillar", () -> new RotatedPillarBlock(
                Properties.of(Material.STONE).strength(4.0F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE)));
        ABYSSAL_PILLAR_ITEM = ITEMS.register("abyssal_pillar", () -> new BlockItem((Block) ABYSSAL_PILLAR.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));


        ABYSSAL_RUNE = BLOCKS.register("abyssal_rune", () -> new LowProfileBlock(
                Properties.of(Material.STONE).strength(3.0F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).lightLevel(state -> 4), 4.0D));
        ABYSSAL_RUNE_ITEM = ITEMS.register("abyssal_rune", () -> new BlockItem((Block) ABYSSAL_RUNE.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        ABYSSAL_RUIN_FOUNDATION = BLOCKS.register("abyssal_ruin_foundation", () -> new LowProfileBlock(
                Properties.of(Material.STONE).strength(4.0F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE), 8.0D));
        ABYSSAL_RUIN_FOUNDATION_ITEM = ITEMS.register("abyssal_ruin_foundation", () -> new BlockItem((Block) ABYSSAL_RUIN_FOUNDATION.get(), (new Item.Properties()).tab(ModItemGroup.ECLIPSE_GROUP)));

        // Armadilha de Cacador/Prata (ver TrapRegistry) - colocada/removida
        // por codigo, nunca pelo jogador diretamente (por isso sem
        // noCollission nem propriedades especiais alem de nao ser solida
        // pro shape reduzido definido em TrapBlock).
        // Sem BlockItem próprio de propósito (antes existiam 2 itens de
        // "trap": este e o "Hunter Trap"/HunterTrapItem - mesclados num
        // só). O único jeito de colocar esse bloco no mundo é via
        // HunterTrapItem (que chama TrapRegistry.place()).
        TRAP = BLOCKS.register("trap", () -> new TrapBlock(
                Properties.of(Material.METAL).strength(1.0F).sound(SoundType.METAL).noOcclusion().harvestTool(ToolType.PICKAXE)));

    }
}
