package com.soldierskull.eclipse.entity.supernatural.ashghoul;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Carniçal das Cinzas (model_ash_ghoul.java). CORRIGIDO:
 * "braço direito", "braço esquerdo", "perna direita", "perna esquerda"
 * (nomes com espaço - ilegal em Java) renomeados para armRight,
 * armLeft, legRight, legLeft.
 *
 * A postura "extremamente curvada" (spec 4.1) já vem esculpida na
 * geometria: cube_r1 (o torso) tem uma rotação estatica de 0.5672 rad
 * baked no Blockbench - e a curvatura do corpo, nao uma junta animavel.
 * head/pernas/braços penduram do bone "body" (que NAO tem essa rotação
 * - só o cubo do torso em si é inclinado), entao animar "body" move a
 * criatura inteira mantendo a postura curvada consistente.
 */
public class AshGhoulModel extends EntityModel<AshGhoulEntity> {

    private final ModelRenderer all;
    private final ModelRenderer body;
    private final ModelRenderer cubeR1; // torso, curvatura estatica - nao animar
    private final ModelRenderer head;
    private final ModelRenderer legRight; // era "perna direita"
    private final ModelRenderer legLeft;  // era "perna esquerda"
    private final ModelRenderer armRight; // era "braço direito"
    private final ModelRenderer armLeft;  // era "braço esquerdo"

    public AshGhoulModel() {
        texWidth = 64;
        texHeight = 64;

        all = new ModelRenderer(this);
        all.setPos(0.0F, 24.0F, 3.0F);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 0.0F, 0.0F);
        all.addChild(body);

        cubeR1 = new ModelRenderer(this);
        cubeR1.setPos(5.0F, -13.0F, 1.0F);
        body.addChild(cubeR1);
        setRotationAngle(cubeR1, 0.5672F, 0.0F, 0.0F);
        cubeR1.texOffs(0, 0).addBox(-10.0F, -16.0F, -1.0F, 10.0F, 17.0F, 4.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setPos(0.0F, -27.0F, -5.0F);
        body.addChild(head);
        head.texOffs(0, 21).addBox(-4.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        head.texOffs(32, 42).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 3.0F, 3.0F, 0.0F, false);

        legRight = new ModelRenderer(this);
        legRight.setPos(-4.0F, -14.0F, 2.0F);
        body.addChild(legRight);
        legRight.texOffs(0, 37).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, 0.0F, false);

        legLeft = new ModelRenderer(this);
        legLeft.setPos(4.0F, -14.0F, 2.0F);
        body.addChild(legLeft);
        legLeft.texOffs(16, 37).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, 0.0F, false);

        armRight = new ModelRenderer(this);
        armRight.setPos(-7.0F, -28.0F, -7.0F);
        body.addChild(armRight);
        armRight.texOffs(28, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 17.0F, 4.0F, 0.0F, false);
        armRight.texOffs(44, 15).addBox(-2.0F, 16.0F, -3.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        armRight.texOffs(48, 14).addBox(2.0F, 16.0F, -2.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        armRight.texOffs(48, 0).addBox(-3.0F, 16.0F, 1.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        armRight.texOffs(48, 5).addBox(-3.0F, 16.0F, -1.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);

        armLeft = new ModelRenderer(this);
        armLeft.setPos(7.0F, -28.0F, -7.0F);
        body.addChild(armLeft);
        armLeft.texOffs(32, 21).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 17.0F, 4.0F, 0.0F, false);
        armLeft.texOffs(44, 0).addBox(2.0F, 16.0F, -1.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        armLeft.texOffs(44, 10).addBox(1.0F, 16.0F, -3.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
        armLeft.texOffs(48, 10).addBox(-3.0F, 16.0F, -2.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        armLeft.texOffs(44, 5).addBox(2.0F, 16.0F, 1.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(AshGhoulEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;
        boolean hasTarget = entity.getTarget() != null;

        if (entity.isDetectionPulseActive()) {
            animateDetectionPulse(entity.getDetectionPulseProgress(), netHeadYaw);
        } else if (hasTarget) {
            animateChase(limbSwing, limbSwingAmount, netHeadYaw);
        } else {
            animateIdleListening(t);
        }

        // ataque de garras (spec 4.5) - usa o attackAnim padrao vanilla, ja sincronizado
        float attackAnim = entity.getAttackAnim(1.0F);
        if (attackAnim > 0.0F) {
            armRight.xRot -= attackAnim * 1.4F;
        }

        // 4.7 regeneracao - contracoes sutis (efeito de pocao ja sincronizado pelo vanilla)
        if (entity.hasEffect(net.minecraft.potion.Effects.REGENERATION)) {
            float pulse = MathHelper.sin(t * 8.0F) * 0.04F;
            body.y += pulse;
        }
    }

    private void resetPose() {
        body.xRot = 0.0F;
        body.y = 0.0F;
        head.xRot = 0.0F;
        head.yRot = 0.0F;
        legRight.xRot = 0.0F;
        legLeft.xRot = 0.0F;
        armRight.xRot = 0.0F;
        armLeft.xRot = 0.0F;
    }

    /** 4.1 IDLE - respiracao leve nos bracos, sem balanco de cabeca. */
    private void animateIdleListening(float t) {
        armRight.xRot = MathHelper.sin(t * 0.6F) * 0.05F;
        armLeft.xRot = MathHelper.sin(t * 0.6F + 1.0F) * 0.05F;
    }

    /**
     * 4.2 SOUND DETECTION - transicao MUITO perceptivel: cabeça e braços
     * congelam, corpo se eleva levemente, cabeça gira em direcao ao som.
     */
    private void animateDetectionPulse(float progress, float netHeadYaw) {
        // progress vai de 1 (acabou de detectar) a 0 (fim do pulso) - inverte pra facilitar
        float snap = 1.0F - progress;
        body.y = -MathHelper.sin(Math.min(1.0F, snap * 2.0F) * (float) Math.PI) * 1.2F; // eleva e volta
        head.yRot = netHeadYaw * 0.017453292F * snap; // gira rapido em direcao a fonte do som
        armRight.xRot = 0.0F;
        armLeft.xRot = 0.0F;
    }

    /** 4.3/4.4 WALK/RUN - baixo e irregular; corre inclinado durante perseguicao. */
    private void animateChase(float limbSwing, float limbSwingAmount, float netHeadYaw) {
        legRight.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
        legLeft.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.2F * limbSwingAmount;
        armRight.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.9F * limbSwingAmount;
        armLeft.xRot = MathHelper.cos(limbSwing * 0.6662F) * 0.9F * limbSwingAmount;

        // corpo inclina mais quanto mais rapido corre (RUN vs WALK)
        body.xRot = 0.1F + Math.min(0.25F, limbSwingAmount * 0.3F);
        head.yRot = MathHelper.clamp(netHeadYaw * 0.017453292F, -1.2F, 1.2F);
    }

    /** 4.8 DEATH - procura, perde forca, cai de joelhos, maos tocam o chao, desaba de lado. */
    private void animateDeath(int deathTime) {
        if (deathTime < 6) {
            // busca curta antes de cair
            head.yRot = MathHelper.sin(deathTime * 0.8F) * 0.3F;
        } else {
            float progress = MathHelper.clamp((deathTime - 6) / 14.0F, 0.0F, 1.0F);
            body.xRot = progress * 1.4F; // ajoelha/desaba
            legRight.xRot = progress * 0.6F;
            legLeft.xRot = progress * 0.6F;
            armRight.xRot = -progress * 0.8F; // maos tocam o chao
            armLeft.xRot = -progress * 0.8F;
            body.y = progress * 4.0F;
        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        all.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
