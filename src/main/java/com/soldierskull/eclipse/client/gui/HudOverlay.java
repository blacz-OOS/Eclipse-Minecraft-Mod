
package com.soldierskull.eclipse.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.soldierskull.eclipse.client.ClientStatsCache;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid = "eclipse", value = Dist.CLIENT)
public class HudOverlay extends AbstractGui {

    // Layout
    private static final int MARGIN = 10;
    private static final int BAR_WIDTH = 60;
    private static final int BAR_HEIGHT = 5;
    private static final float TEXT_SCALE = 0.7F;
    private static final int LABEL_HEIGHT = Math.round(9 * TEXT_SCALE) + 1;
    private static final int ROW_GAP = 2;
    private static final int ROW_HEIGHT = LABEL_HEIGHT + BAR_HEIGHT + ROW_GAP;

    private static final int ENERGY_ROW = 0;
    private static final int CORRUPTION_ROW = 1;
    private static final int HEALTH_ROW = 2;

    // Colors
    private static final int COLOR_BAR_BG = 0xFF585858;
    private static final int COLOR_BAR_BORDER = 0xFF000000;
    private static final int COLOR_HP_BAR = 0xFFE04040;
    private static final int COLOR_ENERGY_BAR = 0xFF55AAFF;
    private static final int COLOR_CORRUPTION_BAR = 0xFFAA55FF;
    private static final int COLOR_TEXT = 0xFFFFFF;

    private HudOverlay() {
    }

    // Cancel vanilla health bar
    @SubscribeEvent
    public static void onRenderOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            event.setCanceled(true);
        }
    }

    // Render custom HUD
    @SubscribeEvent
    public static void onRenderOverlayPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        PlayerEntity player = minecraft.player;

        if (player == null || minecraft.options.hideGui) {
            return;
        }

        MatrixStack matrixStack = event.getMatrixStack();
        FontRenderer font = minecraft.font;

        drawBarRow(
                matrixStack,
                font,
                I18n.get("hud.eclipse.energy"),
                ClientStatsCache.commonEnergy,
                ClientStatsCache.commonEnergyMax,
                COLOR_ENERGY_BAR,
                ENERGY_ROW
        );

        drawBarRow(
                matrixStack,
                font,
                I18n.get("hud.eclipse.corruption"),
                ClientStatsCache.corruption,
                ClientStatsCache.corruptionMax,
                COLOR_CORRUPTION_BAR,
                CORRUPTION_ROW
        );

        if (minecraft.gameMode != null
                && minecraft.gameMode.getPlayerMode() != GameType.CREATIVE) {

            int currentHp = Math.round(player.getHealth());
            int maxHp = Math.round(player.getMaxHealth());

            drawBarRow(
                    matrixStack,
                    font,
                    I18n.get("hud.eclipse.health"),
                    currentHp,
                    maxHp,
                    COLOR_HP_BAR,
                    HEALTH_ROW
            );
        }
    }

    private static int rowY(int row) {
        return MARGIN + row * ROW_HEIGHT + LABEL_HEIGHT;
    }

    private static void drawBarRow(
            MatrixStack matrixStack,
            FontRenderer font,
            String label,
            int current,
            int max,
            int barColor,
            int row
    ) {
        int barY = rowY(row);

        drawBar(
                matrixStack,
                MARGIN,
                barY,
                BAR_WIDTH,
                BAR_HEIGHT,
                current,
                max,
                barColor
        );

        String valueText = label + ": " + current + " / " + max;

        matrixStack.pushPose();
        matrixStack.translate(MARGIN, barY - LABEL_HEIGHT, 0);
        matrixStack.scale(TEXT_SCALE, TEXT_SCALE, 1.0F);

        font.drawShadow(matrixStack, valueText, 0, 0, COLOR_TEXT);

        matrixStack.popPose();
    }

    private static void drawBar(
            MatrixStack matrixStack,
            int x,
            int y,
            int width,
            int height,
            int current,
            int max,
            int fillColor
    ) {
        // Background
        fill(
                matrixStack,
                x,
                y,
                x + width,
                y + height,
                COLOR_BAR_BG
        );

        // Colored fill
        int filledWidth = max > 0
                ? Math.round(width * Math.max(
                0F,
                Math.min(1F, current / (float) max)
        ))
                : 0;

        if (filledWidth > 0) {
            fill(
                    matrixStack,
                    x,
                    y,
                    x + filledWidth,
                    y + height,
                    fillColor
            );
        }

        // Border
        fill(matrixStack, x, y, x + width, y + 1, COLOR_BAR_BORDER);
        fill(matrixStack, x, y + height - 1, x + width, y + height, COLOR_BAR_BORDER);
        fill(matrixStack, x, y, x + 1, y + height, COLOR_BAR_BORDER);
        fill(matrixStack, x + width - 1, y, x + width, y + height, COLOR_BAR_BORDER);
    }
}