package com.soldierskull.eclipse.ritual.altar;

import com.soldierskull.eclipse.Eclipse;
import com.soldierskull.eclipse.ritual.Ritual;
import com.soldierskull.eclipse.ritual.RitualState;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * "Overlay" ao mirar no altar (Bloco B/#9-10, decisão confirmada: sem
 * GUI nenhuma). Não é bem um overlay de renderização - é uma mensagem
 * de action bar reenviada a cada {@link #MESSAGE_INTERVAL_TICKS}
 * enquanto o jogador mira num {@link AltarTileEntity} a curta
 * distância, o suficiente pra dar o feedback contínuo sem precisar
 * mexer em render events/mixins.
 */
@Mod.EventBusSubscriber(modid = Eclipse.MOD_ID)
public final class AltarOverlayHandler {

    private static final double REACH = 6.0D;
    private static final int MESSAGE_INTERVAL_TICKS = 10;

    private AltarOverlayHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.player.level.isClientSide) {
            return;
        }
        if (!(event.player instanceof ServerPlayerEntity)) {
            return;
        }
        if (event.player.tickCount % MESSAGE_INTERVAL_TICKS != 0) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        Vector3d eye = player.getEyePosition(1.0F);
        Vector3d look = player.getViewVector(1.0F);
        Vector3d end = eye.add(look.scale(REACH));

        RayTraceContext rayContext = new RayTraceContext(eye, end,
                RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player);
        RayTraceResult result = player.level.clip(rayContext);
        if (!(result instanceof BlockRayTraceResult)) {
            return;
        }

        TileEntity tileEntity = player.level.getBlockEntity(((BlockRayTraceResult) result).getBlockPos());
        if (!(tileEntity instanceof AltarTileEntity)) {
            return;
        }

        player.displayClientMessage(new StringTextComponent(buildOverlayText((AltarTileEntity) tileEntity)), true);
    }

    private static String buildOverlayText(AltarTileEntity altar) {
        StringBuilder text = new StringBuilder("Altar");
        Ritual candidate = altar.getCandidateRitual();
        if (candidate != null) {
            text.append(" - ").append(candidate.getDisplayName().getString());
        }
        text.append(" [").append(altar.getState()).append("]");
        text.append(" Power ").append(altar.getRitualPower()).append('/').append(altar.getRitualPowerMax());

        if (altar.getState() == RitualState.RUNNING && altar.getActiveContext() != null) {
            int percent = Math.round(altar.getActiveContext().getProgressFraction() * 100.0F);
            text.append(" - ").append(percent).append('%');
        } else if (altar.getLastMessage() != null) {
            text.append(" - ").append(altar.getLastMessage().getString());
        }
        return text.toString();
    }
}
