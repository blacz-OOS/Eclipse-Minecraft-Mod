package com.soldierskull.eclipse.entity.supernatural.drybody;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Corpo-Seco (model_dried_corpse.java). CORRIGIDO:
 * "perna direita", "perna esquerda", "braço direito", "braço esquerdo"
 * renomeados (nomes com espaço - ilegal em Java).
 *
 * Cada perna/braço foi exportado como um conjunto de pecas IRMAS (nao
 * encadeadas pai->filho) com pivo compartilhado na origem do quadril/
 * ombro - ou seja, coxa+canela+pe (ou braço superior+inferior) formam
 * uma pose ja esculpida em bloco unico. Rotacionar o grupo inteiro
 * (legRight/legLeft/armRight/armLeft) move a perna/braço toda a partir
 * do quadril/ombro, no mesmo estilo de um zumbi vanilla - nao da pra
 * animar joelho/cotovelo separadamente com esta geometria.
 */
public class DryBodyModel extends EntityModel<DryBodyEntity> {

    private final ModelRenderer all;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer legRight; // era "perna direita"
    private final ModelRenderer d1;
    private final ModelRenderer cubeR2;
    private final ModelRenderer d2;
    private final ModelRenderer cubeR3;
    private final ModelRenderer d3;
    private final ModelRenderer legLeft; // era "perna esquerda"
    private final ModelRenderer e1;
    private final ModelRenderer cubeR4;
    private final ModelRenderer e2;
    private final ModelRenderer cubeR5;
    private final ModelRenderer e3;
    private final ModelRenderer armRight; // era "braço direito"
    private final ModelRenderer bd1;
    private final ModelRenderer bd2;
    private final ModelRenderer armLeft; // era "braço esquerdo"
    private final ModelRenderer be1;
    private final ModelRenderer be2;

    public DryBodyModel() {
        texWidth = 64;
        texHeight = 64;

        all = new ModelRenderer(this);
        all.setPos(0.0F, 24.0F, 0.0F);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 0.0F, 0.0F);
        all.addChild(body);
        body.texOffs(0, 12).addBox(-4.0F, -27.0F, -1.0F, 8.0F, 6.0F, 4.0F, 0.0F, false);
        body.texOffs(24, 0).addBox(-2.0F, -21.0F, 0.0F, 4.0F, 6.0F, 2.0F, 0.0F, false);
        body.texOffs(0, 22).addBox(-4.0F, -15.0F, -1.0F, 8.0F, 2.0F, 4.0F, 0.0F, false);

        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 26-36 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro pescoco, offset inverso nos filhos: repouso igual.
        head = new ModelRenderer(this);
        head.setPos(0.0F, -27.0F, 1.0F);
        body.addChild(head);
        ModelRenderer headGeo = new ModelRenderer(this);
        headGeo.setPos(0.0F, 27.0F, -1.0F);
        head.addChild(headGeo);
        headGeo.texOffs(0, 0).addBox(-3.0F, -36.0F, -4.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        ModelRenderer cubeR1 = new ModelRenderer(this);
        cubeR1.setPos(0.0F, -27.0F, 1.0F);
        headGeo.addChild(cubeR1);
        setRotationAngle(cubeR1, 0.4363F, 0.0F, 0.0F);
        cubeR1.texOffs(36, 0).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);

        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 15 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro quadril, offset inverso nos filhos: repouso igual.
        legRight = new ModelRenderer(this);
        legRight.setPos(0.0F, -15.0F, 0.0F);
        body.addChild(legRight);

        d1 = new ModelRenderer(this);
        d1.setPos(0.0F, 15.0F, 0.0F);
        legRight.addChild(d1);
        cubeR2 = new ModelRenderer(this);
        cubeR2.setPos(-2.0F, -7.0F, -2.0F);
        d1.addChild(cubeR2);
        setRotationAngle(cubeR2, -0.3927F, 0.0F, 0.0F);
        cubeR2.texOffs(24, 18).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

        d2 = new ModelRenderer(this);
        d2.setPos(0.0F, 15.0F, 0.0F);
        legRight.addChild(d2);
        cubeR3 = new ModelRenderer(this);
        cubeR3.setPos(-2.0F, 0.0F, 0.0F);
        d2.addChild(cubeR3);
        setRotationAngle(cubeR3, 0.1309F, 0.0F, 0.0F);
        cubeR3.texOffs(24, 8).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

        d3 = new ModelRenderer(this);
        d3.setPos(1.0F, 15.0F, 0.0F);
        legRight.addChild(d3);
        d3.texOffs(32, 28).addBox(-4.0F, -1.0F, -3.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);

        legLeft = new ModelRenderer(this);
        legLeft.setPos(0.0F, -15.0F, 0.0F);
        body.addChild(legLeft);

        e1 = new ModelRenderer(this);
        e1.setPos(0.0F, 15.0F, 0.0F);
        legLeft.addChild(e1);
        cubeR4 = new ModelRenderer(this);
        cubeR4.setPos(2.0F, -7.0F, -2.0F);
        e1.addChild(cubeR4);
        setRotationAngle(cubeR4, -0.3927F, 0.0F, 0.0F);
        cubeR4.texOffs(8, 28).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

        e2 = new ModelRenderer(this);
        e2.setPos(0.0F, 15.0F, 0.0F);
        legLeft.addChild(e2);
        cubeR5 = new ModelRenderer(this);
        cubeR5.setPos(2.0F, 0.0F, 0.0F);
        e2.addChild(cubeR5);
        setRotationAngle(cubeR5, 0.1309F, 0.0F, 0.0F);
        cubeR5.texOffs(0, 28).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

        e3 = new ModelRenderer(this);
        e3.setPos(0.0F, 15.0F, 0.0F);
        legLeft.addChild(e3);
        e3.texOffs(32, 33).addBox(1.0F, -1.0F, -3.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);

        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 12-27 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro ombro, offset inverso nos filhos: repouso igual.
        armRight = new ModelRenderer(this);
        armRight.setPos(0.0F, -27.0F, 0.0F);
        body.addChild(armRight);
        bd1 = new ModelRenderer(this);
        bd1.setPos(0.0F, 27.0F, 0.0F);
        armRight.addChild(bd1);
        bd1.texOffs(16, 28).addBox(-6.0F, -27.0F, 0.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
        bd2 = new ModelRenderer(this);
        bd2.setPos(0.0F, 27.0F, 0.0F);
        armRight.addChild(bd2);
        bd2.texOffs(24, 28).addBox(-6.0F, -20.0F, 0.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

        armLeft = new ModelRenderer(this);
        armLeft.setPos(0.0F, -27.0F, 0.0F);
        body.addChild(armLeft);
        be1 = new ModelRenderer(this);
        be1.setPos(10.0F, 27.0F, 0.0F);
        armLeft.addChild(be1);
        be1.texOffs(32, 8).addBox(-6.0F, -27.0F, 0.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
        be2 = new ModelRenderer(this);
        be2.setPos(10.0F, 27.0F, 0.0F);
        armLeft.addChild(be2);
        be2.texOffs(32, 18).addBox(-6.0F, -20.0F, 0.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(DryBodyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;
        head.yRot = netHeadYaw * 0.017453292F;
        head.xRot = headPitch * 0.017453292F;

        boolean running = entity.getTarget() != null && limbSwingAmount > 0.05F;
        if (limbSwingAmount > 0.02F) {
            // 7.2/7.3 WALK/RUN - passos longos, braços relativamente rigidos
            float strideMult = running ? 1.3F : 1.0F;
            legRight.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.1F * limbSwingAmount * strideMult;
            legLeft.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.1F * limbSwingAmount * strideMult;
            armRight.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 0.5F * limbSwingAmount;
            armLeft.xRot = MathHelper.cos(limbSwing * 0.6662F) * 0.5F * limbSwingAmount;
            body.xRot = running ? 0.15F : 0.03F; // 7.3 corpo inclina na corrida
        } else {
            // 7.1 IDLE - postura extremamente ereta, cabeça inclina, ombros relaxam
            body.xRot = MathHelper.sin(t * 0.4F) * 0.02F;
            armRight.zRot = 0.02F + MathHelper.sin(t * 0.7F) * 0.02F;
            armLeft.zRot = -0.02F - MathHelper.sin(t * 0.7F) * 0.02F;
        }

        // 7.4 ATTACK - braço direito, depois esquerdo (aproximado via attackAnim unico)
        float attackAnim = entity.getAttackAnim(1.0F);
        if (attackAnim > 0.0F) {
            armRight.xRot -= attackAnim * 1.3F;
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
        armRight.zRot = 0.0F;
        armLeft.xRot = 0.0F;
        armLeft.zRot = 0.0F;
    }

    /** 7.7 DEATH - joelhos cedem, tronco inclina, cai (nao explosivo). */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 14.0F, 0.0F, 1.0F);
        body.xRot = progress * 1.4F;
        body.y = progress * 5.0F;
        legRight.xRot = progress * 0.7F;
        legLeft.xRot = progress * 0.7F;
        armRight.xRot = progress * 0.3F;
        armLeft.xRot = progress * 0.3F;
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
