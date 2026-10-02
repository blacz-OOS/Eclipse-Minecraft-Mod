package com.soldierskull.eclipse.entity.supernatural.emberimp;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Diabrete de Brasas (model_ember_imp.java). CORRIGIDO:
 * "braço direito", "braço esquerdo", "perna direita", "perna esquerda"
 * renomeados (nomes com espaço - ilegal em Java).
 *
 * Arvore: all -> body -> head -> pescoço, chifre (decoracao fixa) ;
 * body -> legRight, legLeft, armRight, armLeft.
 */
public class EmberImpModel extends EntityModel<EmberImpEntity> {

    /** Pose de base dos bracos, esticados pra frente (estilo zumbi). */
    private static final float ARM_FORWARD_BASE = -1.3F;

    private final ModelRenderer all;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer pescoco;
    private final ModelRenderer chifre;
    private final ModelRenderer legRight; // era "perna direita"
    private final ModelRenderer legLeft;  // era "perna esquerda"
    private final ModelRenderer armRight; // era "braço direito"
    private final ModelRenderer armLeft;  // era "braço esquerdo"

    public EmberImpModel() {
        texWidth = 64;
        texHeight = 64;

        all = new ModelRenderer(this);
        all.setPos(-8.0F, 24.0F, 8.0F);

        body = new ModelRenderer(this);
        body.setPos(5.0F, 0.0F, -8.0F);
        all.addChild(body);
        body.texOffs(0, 12).addBox(0.0F, -11.0F, -2.0F, 6.0F, 7.0F, 4.0F, 0.0F, false);

        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 12-21 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro pescoco, offset inverso nos filhos: repouso igual.
        head = new ModelRenderer(this);
        head.setPos(3.0F, -10.0F, 0.0F);
        body.addChild(head);
        // headGeo desfaz o offset (o export usava coordenadas absolutas, com o
        // pivo da cabeca 8px pro lado e 8px atras do corpo).
        ModelRenderer headGeo = new ModelRenderer(this);
        headGeo.setPos(-8.0F, 10.0F, 8.0F);
        head.addChild(headGeo);
        headGeo.texOffs(0, 0).addBox(5.0F, -18.0F, -11.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

        pescoco = new ModelRenderer(this);
        pescoco.setPos(0.0F, 0.0F, 0.0F);
        headGeo.addChild(pescoco);
        pescoco.texOffs(24, 0).addBox(7.0F, -12.0F, -9.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        chifre = new ModelRenderer(this);
        chifre.setPos(0.0F, 0.0F, 0.0F);
        headGeo.addChild(chifre);
        chifre.texOffs(8, 23).addBox(4.0F, -21.0F, -9.0F, 1.0F, 5.0F, 2.0F, 0.0F, false);
        chifre.texOffs(14, 23).addBox(11.0F, -21.0F, -9.0F, 1.0F, 5.0F, 2.0F, 0.0F, false);

        legRight = new ModelRenderer(this);
        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 5 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro quadril, offset inverso nos filhos: repouso igual.
        legRight.setPos(0.0F, -5.0F, 0.0F);
        body.addChild(legRight);
        legRight.texOffs(20, 20).addBox(0.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

        legLeft = new ModelRenderer(this);
        legLeft.setPos(3.0F, -5.0F, 0.0F);
        body.addChild(legLeft);
        legLeft.texOffs(0, 23).addBox(1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

        armRight = new ModelRenderer(this);
        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 9-11 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro ombro, offset inverso nos filhos: repouso igual.
        armRight.setPos(0.0F, -10.0F, 0.0F);
        body.addChild(armRight);
        armRight.texOffs(20, 16).addBox(-6.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);

        armLeft = new ModelRenderer(this);
        armLeft.setPos(6.0F, -10.0F, 0.0F);
        body.addChild(armLeft);
        armLeft.texOffs(20, 12).addBox(0.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(EmberImpEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;

        if (limbSwingAmount > 0.05F) {
            animateHopMovement(limbSwing, limbSwingAmount);
        } else {
            animateIdle(t);
        }

        float attackAnim = entity.getAttackAnim(1.0F);
        if (attackAnim > 0.0F) {
            head.xRot -= attackAnim * 0.6F; // 6.3 - cabeça recua e avança na mordida
            body.xRot -= attackAnim * 0.2F;
        }

        // 6.4 IGNITE - pequena intensificacao quando incendeia
        if (entity.isIgnitePulseActive()) {
            float jitter = MathHelper.sin(t * 40.0F) * 0.15F;
            body.xRot += jitter;
            head.xRot += jitter;
        }
    }

    private void resetPose() {
        body.xRot = 0.0F;
        body.y = 0.0F;
        head.xRot = 0.0F;
        legRight.xRot = 0.0F;
        legLeft.xRot = 0.0F;
        armRight.xRot = ARM_FORWARD_BASE;
        armLeft.xRot = ARM_FORWARD_BASE;
    }

    /** 6.1 IDLE - nunca parado: esfrega maos, olha lados, pequenos saltos. */
    private void animateIdle(float t) {
        head.yRot = MathHelper.sin(t * 0.9F) * 0.3F;
        armRight.xRot += MathHelper.sin(t * 3.0F) * 0.2F;
        armLeft.xRot += MathHelper.sin(t * 3.0F + (float) Math.PI) * 0.2F;
        body.y = -Math.abs(MathHelper.sin(t * 1.5F)) * 0.5F; // pequenos saltos ocasionais
    }

    /** 6.2 MOVEMENT - locomocao por saltos (agacha -> salta -> avanca -> aterrissa), nao corrida normal. */
    private void animateHopMovement(float limbSwing, float limbSwingAmount) {
        // ciclo de salto usando limbSwing como "tempo" - fase 0-0.5 agacha/impulsiona, 0.5-1 no ar/aterrissa
        float cycle = (limbSwing * 0.3F) % 1.0F;
        float hop = MathHelper.sin(cycle * (float) Math.PI); // 0 -> 1 -> 0 por salto
        body.y = -hop * 3.0F * limbSwingAmount;
        legRight.xRot = -hop * 0.8F;
        legLeft.xRot = -hop * 0.8F;
        armRight.xRot += hop * 0.5F;
        armLeft.xRot += hop * 0.5F;
        body.xRot = hop * 0.15F;
    }

    /** 6.5 DEATH - tropeça, cai, brasas diminuem, desaparece. */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 10.0F, 0.0F, 1.0F);
        body.xRot = progress * 1.5F;
        body.y = progress * 3.0F;
        legRight.xRot = progress * 0.5F;
        legLeft.xRot = -progress * 0.5F;
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