package com.soldierskull.eclipse.entity.supernatural.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * PLACEHOLDER GEOMETRICO COMPARTILHADO - um unico bloco retangular.
 * Usado pelos mobs do lote 1 que ainda nao tem modelo Blockbench
 * dedicado (Vorme, Parasita, Carnical, Vazio, Diabrete). Cada um usa
 * dimensoes proprias (largura/altura/profundidade) pra pelo menos
 * sugerir a silhueta certa (ex.: Vorme comprido, Parasita minusculo).
 *
 * NAO E ARTE FINAL. Servem so pra a mecanica ser testavel em jogo antes
 * de qualquer sessao de modelagem real (secao 42/43 da Fase 14).
 */
public class SimpleBoxModel<T extends Entity> extends EntityModel<T> {
    private final ModelRenderer box;

    public SimpleBoxModel(float width, float height, float depth, int texW, int texH) {
        texWidth = texW;
        texHeight = texH;
        box = new ModelRenderer(this);
        box.setPos(0.0F, 24.0F - height * 16, 0.0F);
        box.texOffs(0, 0).addBox(-width * 8, -height * 16, -depth * 8, width * 16, height * 16, depth * 16);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // sem animacao - placeholder estatico
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        box.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
