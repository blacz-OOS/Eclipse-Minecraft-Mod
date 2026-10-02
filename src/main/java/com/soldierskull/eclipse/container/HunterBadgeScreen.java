package com.soldierskull.eclipse.container;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Fundo desenhado na mão (sem textura externa) - a primeira versão
 * reaproveitava a textura vanilla do Shulker Box, mas ela tem a palavra
 * "Inventory" fixa numa posição pensada pra 3 linhas de slots; como a
 * Hunter Badge só tem 1 linha (7 slots), a label ficava flutuando fora
 * do lugar e um bloco da textura sobrava vazio - relatado como "gui
 * bugada". Desenhar os retângulos na posição exata de cada Slot do
 * Container elimina esse descompasso, ao custo de ficar menos bonito
 * que uma textura de verdade (troca aceitável até existir arte própria).
 */
@OnlyIn(Dist.CLIENT)
public class HunterBadgeScreen extends ContainerScreen<HunterBadgeContainer> {

    private static final int COLOR_PANEL = 0xF0202020;
    private static final int COLOR_SLOT = 0xFF8B8B8B;

    public HunterBadgeScreen(HunterBadgeContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 134;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        fill(matrixStack, relX, relY, relX + this.imageWidth, relY + this.imageHeight, COLOR_PANEL);

        for (Slot slot : this.menu.slots) {
            int slotX = relX + slot.x - 1;
            int slotY = relY + slot.y - 1;
            fill(matrixStack, slotX, slotY, slotX + 18, slotY + 18, COLOR_SLOT);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
