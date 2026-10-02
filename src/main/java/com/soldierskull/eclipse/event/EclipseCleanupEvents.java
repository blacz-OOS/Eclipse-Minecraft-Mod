package com.soldierskull.eclipse.event;

import com.soldierskull.eclipse.Eclipse;
import com.soldierskull.eclipse.skills.EclipseEventManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

/**
 * Limpeza de estado estatico ligado a mundos.
 *
 * MOTIVO: EclipseEventManager guarda ServerWorld num mapa estatico
 * (ACTIVE_UNTIL). Sem nada removendo essas entradas, cada mundo que o
 * jogador abre fica referenciado pra sempre e nunca e coletado pelo GC -
 * um vazamento que piora a cada entra/sai de mundo na mesma sessao do
 * jogo, especialmente sensivel em maquinas com pouca RAM.
 *
 * O mod nao tinha NENHUM handler de descarregamento de mundo ou parada
 * de servidor antes disso.
 */
@Mod.EventBusSubscriber(modid = Eclipse.MOD_ID)
public final class EclipseCleanupEvents {

    private EclipseCleanupEvents() {
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof ServerWorld) {
            EclipseEventManager.forgetWorld((ServerWorld) event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        EclipseEventManager.clearAll();
    }
}
