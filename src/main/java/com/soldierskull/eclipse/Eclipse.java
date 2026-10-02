package com.soldierskull.eclipse;

import com.soldierskull.eclipse.block.ModBlocks;
import com.soldierskull.eclipse.client.KeyInit;
import com.soldierskull.eclipse.entity.supernatural.ghost.GhostEntity;
import com.soldierskull.eclipse.entity.supernatural.ghost.GhostRenderer;
import com.soldierskull.eclipse.entity.supernatural.ghost.GhostSounds;
import com.soldierskull.eclipse.entity.supernatural.ghost.GhostSpawn;
import com.soldierskull.eclipse.container.HunterBadgeScreen;
import com.soldierskull.eclipse.container.ModContainers;
import com.soldierskull.eclipse.item.ModItems;
import com.soldierskull.eclipse.network.PacketHandler;
import com.soldierskull.eclipse.ritual.RitualRegistry;
import com.soldierskull.eclipse.ritual.altar.ModTileEntities;
import com.soldierskull.eclipse.stats.PlayerStats;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

@Mod(Eclipse.MOD_ID)
public class Eclipse {
    public static final String MOD_ID = "eclipse";
    private static final Logger LOGGER = LogManager.getLogger();

    public Eclipse() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 1. Registro dos conteúdos (Items, Blocks, Entities, Sounds)
        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        // ModEntityTypes.register(eventBus); -- REMOVIDO: o Ghost ja e registrado
        // por ModSupernaturalEntities. Dois DeferredRegister registrando
        // "eclipse:ghost" causavam o crash "Duplicate GlobalEntityTypeAttributes".
        ModTileEntities.register(eventBus); // Sistema de Rituais - Altar (Etapa 2)
        ModContainers.register(eventBus); // Hunter Badge (mochila de 7 slots)
        GhostSounds.register(eventBus);
        com.soldierskull.eclipse.structures.ModStructures.register(eventBus); // Fase 13.6 - Abyssal Ruins

        // BUGFIX: StructureRegistryHandler nunca era registrado em nenhum event bus.
        // FMLCommonSetupEvent roda no MOD bus e BiomeLoadingEvent roda no FORGE bus,
        // por isso @Mod.EventBusSubscriber (que só aceita 1 bus) não serve aqui -
        // registramos manualmente os dois métodos nos bus corretos.
        eventBus.addListener(
                com.soldierskull.eclipse.structures.StructureRegistryHandler::onCommonSetup
        );
        // BISECT: desativado temporariamente para testar se a Abyssal Ruins
        // sendo adicionada ao bioma eh a causa do bug (Build C).
         MinecraftForge.EVENT_BUS.addListener(
                com.soldierskull.eclipse.structures.StructureRegistryHandler::onBiomeLoad
       );
        // mesmo motivo/padrao acima - OverworldSpawnHandler tambem usa BiomeLoadingEvent
        // (Forge bus), nao pode ser @Mod.EventBusSubscriber(bus = MOD).
        MinecraftForge.EVENT_BUS.addListener(
                com.soldierskull.eclipse.entity.supernatural.spawn.OverworldSpawnHandler::onBiomeLoad
        );

        // BUGFIX (Etapa 2): StructureRegistryHandler.onWorldLoad() tinha
        // @SubscribeEvent mas nunca foi registrado em NENHUM event bus -
        // o próprio comentário do método já explicava que essa correção
        // por-mundo é "a abordagem correta" (o hack de reflection sozinho
        // no DEFAULTS global não é suficiente), mas o método nunca rodava
        // de verdade. Afeta tanto Hunter Camp (Etapa 2) quanto Abyssal Ruins.
        MinecraftForge.EVENT_BUS.addListener(
                com.soldierskull.eclipse.structures.StructureRegistryHandler::onWorldLoad
        );
        com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.register(eventBus); // Fase 14 lote 1
        com.soldierskull.eclipse.entity.npc.ModNPCEntities.register(eventBus); // NPCs de estrutura (Fase 13)
        com.soldierskull.eclipse.effect.ModEffects.register(eventBus); // Fase 14 - efeito Bleeding

        // 2. Ouvintes de eventos do Mod Event Bus
        eventBus.addListener(this::setup);
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);
        eventBus.addListener(this::doClientStuff);

        // 3. Registro do Forge Event Bus global
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        LOGGER.info("INICIALIZANDO MOD ECLIPSE...");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        event.enqueueWork(GhostSpawn::registerSpawns);

        CapabilityManager.INSTANCE.register(PlayerStats.class, new Capability.IStorage<PlayerStats>() {
            @Override
            public INBT writeNBT(Capability<PlayerStats> capability, PlayerStats instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<PlayerStats> capability, PlayerStats instance, Direction side, INBT nbt) {
            }
        }, PlayerStats::new);

        PacketHandler.register();

        RitualRegistry.bootstrap(); // Sistema de Rituais - channel_power + abyss_crystal_charging (Bloco F)

        com.soldierskull.eclipse.entity.npc.hunter.HunterDialogue.register(); // Etapa 3 - NPC + Diálogo
        com.soldierskull.eclipse.entity.npc.cultist.CultistDialogue.register();
    }

    private void doClientStuff(FMLClientSetupEvent event) {
        KeyInit.register();

        event.enqueueWork(() -> net.minecraft.client.gui.ScreenManager.register(ModContainers.HUNTER_BADGE.get(), HunterBadgeScreen::new));

        RenderTypeLookup.setRenderLayer(ModBlocks.ABYSS_CRYSTAL.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.CHARGED_ABYSS_CRYSTAL.get(), RenderType.cutout());

        // Bloco D (Abyss Altar): core/rune/foundation nao sao blocos
        // cheios (core e um conjunto de pilares finos, rune e uma placa
        // de 4px, foundation uma base de 8px) e as 3 texturas tem pixels
        // totalmente transparentes de verdade (nao so uma cor de fundo).
        // Sem cutout, a camada "solid" padrao NAO testa alpha nenhum -
        // cada pixel "transparente" vira solido, geralmente preto/lixo
        // de textura, exatamente o tipo de bug visual pedido pra evitar
        // aqui. abyssal_pillar fica de fora: as texturas dele sao 100%
        // opacas (sem canal alpha variavel), entao ele continua "solid"
        // (igual a uma tora vanilla) - cutout ali so custaria performance
        // a toa, sem nenhum ganho visual.
        RenderTypeLookup.setRenderLayer(ModBlocks.ABYSS_ALTAR_CORE.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ABYSSAL_RUNE.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ABYSSAL_RUIN_FOUNDATION.get(), RenderType.cutout());

        // BUGFIX: Ritual Chalk e Blood Chalk agora tem 5 variantes de
        // marca cada, com pixels de verdade transparentes (fora do
        // desenho da marca) - sem cutout, a camada solid padrao
        // renderizaria esses pixels como lixo solido em vez de vazios.
        // (Se voce ja tinha essas duas linhas de uma correcao anterior,
        // chamar de novo nao causa erro - so redefine o mesmo valor.)
        RenderTypeLookup.setRenderLayer(ModBlocks.RITUAL_CHALK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.BLOOD_CHALK.get(), RenderType.cutout());

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.GHOST.get(),
                GhostRenderer::new
        );

        // Fase 14, lote 1 - mobs do Abismo
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.ABYSSAL_HAND.get(),
                com.soldierskull.eclipse.entity.supernatural.abyssalhand.AbyssalHandRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.BLOOD_WORM.get(),
                com.soldierskull.eclipse.entity.supernatural.bloodworm.BloodWormRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.AETHER_PARASITE.get(),
                com.soldierskull.eclipse.entity.supernatural.aetherparasite.AetherParasiteRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.ASH_GHOUL.get(),
                com.soldierskull.eclipse.entity.supernatural.ashghoul.AshGhoulRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.VOID_OBSERVER.get(),
                com.soldierskull.eclipse.entity.supernatural.voidobserver.VoidObserverRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.EMBER_IMP.get(),
                com.soldierskull.eclipse.entity.supernatural.emberimp.EmberImpRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.VOID_BOLT.get(),
                manager -> new net.minecraft.client.renderer.entity.SpriteRenderer<>(manager,
                        net.minecraft.client.Minecraft.getInstance().getItemRenderer()));

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.npc.ModNPCEntities.HUNTER_NPC.get(),
                com.soldierskull.eclipse.entity.npc.hunter.HunterNPCRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.npc.ModNPCEntities.CULTIST_NPC.get(),
                com.soldierskull.eclipse.entity.npc.cultist.CultistNPCRenderer::new);

        // Fase 14, lote 2 - mobs do Overworld
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.DRY_BODY.get(),
                com.soldierskull.eclipse.entity.supernatural.drybody.DryBodyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.MIST_GHOULIN.get(),
                com.soldierskull.eclipse.entity.supernatural.mistghoulin.MistGhoulinRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.CORRUPTED_SACI.get(),
                com.soldierskull.eclipse.entity.supernatural.corruptedsaci.CorruptedSaciRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.SULFUR_HOUND.get(),
                com.soldierskull.eclipse.entity.supernatural.sulfurhound.SulfurHoundRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.FOREST_SPECTER.get(),
                com.soldierskull.eclipse.entity.supernatural.forestspecter.ForestSpecterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.MAPINGUARI.get(),
                com.soldierskull.eclipse.entity.supernatural.mapinguari.MapinguariRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.WENDIGO.get(),
                com.soldierskull.eclipse.entity.supernatural.wendigo.WendigoRenderer::new);

        LOGGER.info("Got game settings {}", ((Minecraft) event.getMinecraftSupplier().get()).options);
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("examplemod", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(InterModProcessEvent event) {
        LOGGER.info("Got IMC {}", event.getIMCStream().map((m) -> m.getMessageSupplier().get()).collect(Collectors.toList()));
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("Servidor iniciando...");
    }

    @EventBusSubscriber(modid = Eclipse.MOD_ID, bus = Bus.MOD)
    public static class ModEventBusEvents {

        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            // Atributos do Ghost sao registrados em ModSupernaturalEntities.
            // Registrar de novo aqui duplicava a entrada e travava o carregamento.
        }

        @SubscribeEvent
        public static void onBlocksRegistry(RegistryEvent.Register<Block> blockRegistryEvent) {
            Eclipse.LOGGER.info("HELLO from Register Block");
        }
    }
}