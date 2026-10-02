package com.soldierskull.eclipse;

import com.soldierskull.eclipse.block.ModBlocks;
import com.soldierskull.eclipse.client.KeyInit;
import com.soldierskull.eclipse.container.HunterBadgeScreen;
import com.soldierskull.eclipse.container.ModContainers;
import com.soldierskull.eclipse.entity.supernatural.ghost.GhostRenderer;
import com.soldierskull.eclipse.entity.supernatural.ghost.GhostSounds;
import com.soldierskull.eclipse.entity.supernatural.ghost.GhostSpawn;
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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
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

        // Registro dos conteúdos
        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModTileEntities.register(eventBus);
        ModContainers.register(eventBus);
        GhostSounds.register(eventBus);

        com.soldierskull.eclipse.structures.ModStructures.register(eventBus);

        // Registro dos ParticleTypes
        com.soldierskull.eclipse.particle.ModParticleTypes.register(eventBus);

        // Handlers de estruturas e spawner
        eventBus.addListener(
                com.soldierskull.eclipse.structures.StructureRegistryHandler::onCommonSetup
        );

        MinecraftForge.EVENT_BUS.addListener(
                com.soldierskull.eclipse.structures.StructureRegistryHandler::onBiomeLoad
        );

        MinecraftForge.EVENT_BUS.addListener(
                com.soldierskull.eclipse.entity.supernatural.spawn.OverworldSpawnHandler::onBiomeLoad
        );

        MinecraftForge.EVENT_BUS.addListener(
                com.soldierskull.eclipse.structures.StructureRegistryHandler::onWorldLoad
        );

        com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.register(eventBus);
        com.soldierskull.eclipse.entity.npc.ModNPCEntities.register(eventBus);
        com.soldierskull.eclipse.effect.ModEffects.register(eventBus);

        // Ouvintes do Mod Event Bus
        eventBus.addListener(this::setup);
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);
        eventBus.addListener(this::doClientStuff);

        // Forge Event Bus global
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        LOGGER.info("INICIALIZANDO MOD ECLIPSE...");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        event.enqueueWork(GhostSpawn::registerSpawns);

        CapabilityManager.INSTANCE.register(
                PlayerStats.class,
                new Capability.IStorage<PlayerStats>() {

                    @Override
                    public INBT writeNBT(
                            Capability<PlayerStats> capability,
                            PlayerStats instance,
                            Direction side) {

                        return new CompoundNBT();
                    }

                    @Override
                    public void readNBT(
                            Capability<PlayerStats> capability,
                            PlayerStats instance,
                            Direction side,
                            INBT nbt) {
                    }
                },
                PlayerStats::new
        );

        PacketHandler.register();

        RitualRegistry.bootstrap();

        com.soldierskull.eclipse.entity.npc.hunter.HunterDialogue.register();
        com.soldierskull.eclipse.entity.npc.cultist.CultistDialogue.register();
    }

    private void doClientStuff(FMLClientSetupEvent event) {

        KeyInit.register();

        event.enqueueWork(() -> net.minecraft.client.gui.ScreenManager.register(
                ModContainers.HUNTER_BADGE.get(),
                HunterBadgeScreen::new
        ));

        RenderTypeLookup.setRenderLayer(
                ModBlocks.ABYSS_CRYSTAL.get(),
                RenderType.cutout()
        );

        RenderTypeLookup.setRenderLayer(
                ModBlocks.CHARGED_ABYSS_CRYSTAL.get(),
                RenderType.cutout()
        );

        RenderTypeLookup.setRenderLayer(
                ModBlocks.ABYSS_ALTAR_CORE.get(),
                RenderType.cutout()
        );

        RenderTypeLookup.setRenderLayer(
                ModBlocks.ABYSSAL_RUNE.get(),
                RenderType.cutout()
        );

        RenderTypeLookup.setRenderLayer(
                ModBlocks.ABYSSAL_RUIN_FOUNDATION.get(),
                RenderType.cutout()
        );

        RenderTypeLookup.setRenderLayer(
                ModBlocks.RITUAL_CHALK.get(),
                RenderType.cutout()
        );

        RenderTypeLookup.setRenderLayer(
                ModBlocks.BLOOD_CHALK.get(),
                RenderType.cutout()
        );

        // Ghost
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.GHOST.get(),
                GhostRenderer::new
        );

        // Mobs do Abismo
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.ABYSSAL_HAND.get(),
                com.soldierskull.eclipse.entity.supernatural.abyssalhand.AbyssalHandRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.BLOOD_WORM.get(),
                com.soldierskull.eclipse.entity.supernatural.bloodworm.BloodWormRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.AETHER_PARASITE.get(),
                com.soldierskull.eclipse.entity.supernatural.aetherparasite.AetherParasiteRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.ASH_GHOUL.get(),
                com.soldierskull.eclipse.entity.supernatural.ashghoul.AshGhoulRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.VOID_OBSERVER.get(),
                com.soldierskull.eclipse.entity.supernatural.voidobserver.VoidObserverRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.EMBER_IMP.get(),
                com.soldierskull.eclipse.entity.supernatural.emberimp.EmberImpRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.VOID_BOLT.get(),
                manager -> new net.minecraft.client.renderer.entity.SpriteRenderer<>(
                        manager,
                        net.minecraft.client.Minecraft.getInstance().getItemRenderer()
                )
        );

        // IMPORTANTE:
        // O registro da factory da particula NAO fica mais aqui.
        // Ele foi movido para ParticleFactoryRegisterEvent abaixo.

        // NPCs
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.npc.ModNPCEntities.HUNTER_NPC.get(),
                com.soldierskull.eclipse.entity.npc.hunter.HunterNPCRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.npc.ModNPCEntities.CULTIST_NPC.get(),
                com.soldierskull.eclipse.entity.npc.cultist.CultistNPCRenderer::new
        );

        // Mobs do Overworld
        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.DRY_BODY.get(),
                com.soldierskull.eclipse.entity.supernatural.drybody.DryBodyRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.MIST_GHOULIN.get(),
                com.soldierskull.eclipse.entity.supernatural.mistghoulin.MistGhoulinRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.CORRUPTED_SACI.get(),
                com.soldierskull.eclipse.entity.supernatural.corruptedsaci.CorruptedSaciRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.SULFUR_HOUND.get(),
                com.soldierskull.eclipse.entity.supernatural.sulfurhound.SulfurHoundRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.FOREST_SPECTER.get(),
                com.soldierskull.eclipse.entity.supernatural.forestspecter.ForestSpecterRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.MAPINGUARI.get(),
                com.soldierskull.eclipse.entity.supernatural.mapinguari.MapinguariRenderer::new
        );

        RenderingRegistry.registerEntityRenderingHandler(
                com.soldierskull.eclipse.entity.supernatural.ModSupernaturalEntities.WENDIGO.get(),
                com.soldierskull.eclipse.entity.supernatural.wendigo.WendigoRenderer::new
        );

        LOGGER.info(
                "Got game settings {}",
                ((Minecraft) event.getMinecraftSupplier().get()).options
        );
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("examplemod", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(InterModProcessEvent event) {
        LOGGER.info(
                "Got IMC {}",
                event.getIMCStream()
                        .map((m) -> m.getMessageSupplier().get())
                        .collect(Collectors.toList())
        );
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("Servidor iniciando...");
    }

    @EventBusSubscriber(
            modid = Eclipse.MOD_ID,
            bus = Bus.MOD,
            value = Dist.CLIENT
    )
    public static class ModEventBusEvents {

        @SubscribeEvent
        public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {

            Minecraft.getInstance().particleEngine.register(
                    com.soldierskull.eclipse.particle.ModParticleTypes.LASER.get(),
                    com.soldierskull.eclipse.particle.LaserParticle.Factory::new
            );
        }

        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            // Atributos do Ghost sao registrados em ModSupernaturalEntities.
        }

        @SubscribeEvent
        public static void onBlocksRegistry(
                RegistryEvent.Register<Block> blockRegistryEvent) {

            Eclipse.LOGGER.info("HELLO from Register Block");
        }
    }
}