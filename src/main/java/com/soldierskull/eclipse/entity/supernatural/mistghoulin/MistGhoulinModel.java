package com.soldierskull.eclipse.entity.supernatural.mistghoulin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Ghoulin da Névoa (model_mist_ghoulin.java). CORRIGIDO:
 * "braço direito", "braço esquerdo", "perna direita", "perna esquerda"
 * renomeados (mesmo padrao do Ash Ghoul - torso com curvatura estatica
 * em cube_r1, membros penduram do bone "body" sem essa rotacao).
 */
public class MistGhoulinModel extends EntityModel<MistGhoulinEntity> {

    private final ModelRenderer all;
    private final ModelRenderer body;
    private final ModelRenderer cubeR1;
    private final ModelRenderer head;
    private final ModelRenderer legRight;
    private final ModelRenderer legLeft;
    private final ModelRenderer armRight;
    private final ModelRenderer armLeft;

    public MistGhoulinModel() {
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
        head.texOffs(48, 18).addBox(1.0F, -1.0F, -9.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
        head.texOffs(48, 22).addBox(-2.0F, -1.0F, -9.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

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
    public void setupAnim(MistGhoulinEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;
        head.yRot = netHeadYaw * 0.017453292F;

        if (limbSwingAmount > 0.02F) {
            // 8.2/8.3 WALK/RUN - rapida e irregular, sempre prestes a cair pra frente
            legRight.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.3F * limbSwingAmount;
            legLeft.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.3F * limbSwingAmount;
            armRight.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.0F * limbSwingAmount - 0.3F;
            armLeft.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.0F * limbSwingAmount - 0.3F;
            body.xRot = 0.2F + Math.min(0.2F, limbSwingAmount * 0.25F); // inclinacao acentuada (8.3)
        } else {
            // 8.1 IDLE - respiracao pesada, boca com pequenos movimentos (aproximado via corpo)
            body.xRot = MathHelper.sin(t * 2.0F) * 0.03F; // "respiracao"
            armRight.xRot = MathHelper.sin(t * 0.9F) * 0.05F;
            armLeft.xRot = MathHelper.sin(t * 0.9F + 1.0F) * 0.05F;
        }

        // 8.4 ATTACK - garras, ataque duplo aproximado via attackAnim
        float attackAnim = entity.getAttackAnim(1.0F);
        if (attackAnim > 0.0F) {
            armRight.xRot -= attackAnim * 1.5F;
            armLeft.xRot -= attackAnim * 0.8F;
        }
    }

    private void resetPose() {
        body.xRot = 0.0F;
        body.y = 0.0F;
        head.yRot = 0.0F;
        legRight.xRot = 0.0F;
        legLeft.xRot = 0.0F;
        armRight.xRot = 0.0F;
        armLeft.xRot = 0.0F;
    }

    /** 8.6 DEATH - cai pra frente, braços tentam tocar o chao. */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 10.0F, 0.0F, 1.0F);
        body.xRot = progress * 1.6F;
        body.y = progress * 5.0F;
        armRight.xRot = -progress * 1.0F;
        armLeft.xRot = -progress * 1.0F;
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
