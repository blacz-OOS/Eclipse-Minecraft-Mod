package com.soldierskull.eclipse.entity.supernatural.aetherparasite;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Parasita de Eter (model_aether_parasite.java).
 * CORRIGIDO: as duas asas foram exportadas como "1" e "2"
 * (identificadores puramente numericos - ilegal em Java), renomeadas
 * para wingRight/wingLeft (baseado no sinal de X dos boxes: "1" cobre
 * X positivo, "2" cobre X negativo).
 *
 * Arvore exportada (preservada como veio - "all" e um pivo vazio sem
 * geometria propria, "patas" e uma peca raiz independente, nao filha
 * de "body"):
 * all (vazio)
 * body
 *  |- head
 *  |- wings
 *      |- wingRight (era "1")
 *      |- wingLeft  (era "2")
 * patas (raiz independente)
 */
public class AetherParasiteModel extends EntityModel<AetherParasiteEntity> {

    private final ModelRenderer all;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer wings;
    private final ModelRenderer wingRight; // era "1"
    private final ModelRenderer wingLeft;  // era "2"
    private final ModelRenderer patas;

    public AetherParasiteModel() {
        texWidth = 64;
        texHeight = 64;

        all = new ModelRenderer(this);
        all.setPos(0.0F, 15.0F, 0.0F);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 24.0F, -2.0F);
        body.texOffs(0, 0).addBox(-2.0F, -12.0F, 2.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);
        body.texOffs(0, 18).addBox(-1.0F, -11.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setPos(0.0F, 0.0F, 2.0F);
        body.addChild(head);
        head.texOffs(0, 10).addBox(-2.0F, -12.0F, -6.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);

        wings = new ModelRenderer(this);
        wings.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(wings);

        wingRight = new ModelRenderer(this);
        wingRight.setPos(0.0F, -11.0F, 1.0F);
        wings.addChild(wingRight);
        wingRight.texOffs(8, 18).addBox(0.0F, 0.0F, -1.0F, 3.0F, 0.0F, 2.0F, 0.0F, false);
        wingRight.texOffs(16, 10).addBox(3.0F, 0.0F, -1.0F, 5.0F, 0.0F, 4.0F, 0.0F, false);

        wingLeft = new ModelRenderer(this);
        wingLeft.setPos(0.0F, -11.0F, 1.0F);
        wings.addChild(wingLeft);
        wingLeft.texOffs(18, 18).addBox(-3.0F, 0.0F, -1.0F, 3.0F, 0.0F, 2.0F, 0.0F, false);
        wingLeft.texOffs(16, 14).addBox(-8.0F, 0.0F, -1.0F, 5.0F, 0.0F, 4.0F, 0.0F, false);

        patas = new ModelRenderer(this);
        patas.setPos(0.0F, 24.0F, 0.0F);
        patas.texOffs(20, 0).addBox(1.0F, -9.0F, -2.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        patas.texOffs(20, 2).addBox(-3.0F, -9.0F, -2.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        patas.texOffs(20, 4).addBox(-4.0F, -9.0F, -2.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        patas.texOffs(8, 20).addBox(3.0F, -9.0F, -2.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        patas.texOffs(12, 20).addBox(3.0F, -9.0F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        patas.texOffs(24, 4).addBox(1.0F, -9.0F, 2.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        patas.texOffs(20, 22).addBox(-3.0F, -9.0F, 2.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        patas.texOffs(4, 22).addBox(-4.0F, -9.0F, 2.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        patas.texOffs(0, 22).addBox(3.0F, -9.0F, 2.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        patas.texOffs(16, 20).addBox(-4.0F, -9.0F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        patas.texOffs(20, 8).addBox(-3.0F, -9.0F, 0.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        patas.texOffs(20, 20).addBox(1.0F, -9.0F, 0.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(AetherParasiteEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;

        if (entity.isRecoiling()) {
            animateRecoil(t);
        } else if (entity.isAttached()) {
            animateAttached(t);
        } else {
            animateFlying(t);
        }
    }

    private void resetPose() {
        body.xRot = 0.0F;
        body.zRot = 0.0F;
        body.y = 24.0F;
        head.xRot = 0.0F;
        wingRight.zRot = 0.0F;
        wingLeft.zRot = 0.0F;
        patas.y = 24.0F;
    }

    /** 3.1/3.2 FLYING IDLE/MOVEMENT - asas batem rapido, corpo oscila e gira, nunca em linha reta. */
    private void animateFlying(float t) {
        // batimento de asas rapido (arriba/abaixo simulado via xRot já que sao planas no eixo Y)
        float flap = MathHelper.sin(t * 25.0F) * 0.9F;
        wingRight.zRot = flap;
        wingLeft.zRot = -flap;

        body.y = 24.0F + MathHelper.sin(t * 4.0F) * 0.6F;
        body.zRot = MathHelper.sin(t * 2.2F) * 0.15F;
        body.xRot = MathHelper.cos(t * 1.7F) * 0.1F;

        head.xRot = MathHelper.sin(t * 3.0F + 0.5F) * 0.15F; // antenas/cabeca com pequeno atraso
    }

    /** 3.5 ATTACHED - asas desaceleram, corpo pulsa (drenando), antenas se movimentam. */
    private void animateAttached(float t) {
        float flap = MathHelper.sin(t * 8.0F) * 0.3F; // muito mais lento que voando
        wingRight.zRot = flap;
        wingLeft.zRot = -flap;

        float pulse = MathHelper.sin(t * 5.0F) * 0.08F;
        body.xRot = pulse;
        body.zRot = MathHelper.sin(t * 3.0F) * 0.05F;
        head.xRot = MathHelper.sin(t * 6.0F + 1.0F) * 0.1F;
    }

    /** 3.6 HIT WHILE ATTACHED - contrai e recua rapidamente, asas aceleram de volta. */
    private void animateRecoil(float t) {
        body.xRot = -0.3F; // corpo contrai
        float flap = MathHelper.sin(t * 30.0F) * 1.0F; // asas em pânico
        wingRight.zRot = flap;
        wingLeft.zRot = -flap;
    }

    /** 3.7 DEATH - corpo dobra, asas param, cai. */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 8.0F, 0.0F, 1.0F);
        body.xRot = progress * 1.2F; // dobra
        wingRight.zRot = 0.0F;
        wingLeft.zRot = 0.0F;
        body.y = 24.0F + progress * 3.0F; // cai
        patas.y = 24.0F + progress * 3.0F;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        all.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        patas.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
