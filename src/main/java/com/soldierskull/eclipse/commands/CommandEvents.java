package com.soldierskull.eclipse.commands;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandEvents {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        EclipseCommand.register(event.getDispatcher());
        NPCDialogueCommand.register(event.getDispatcher()); // Etapa 3 - clique em opção de diálogo de NPC
    }
}