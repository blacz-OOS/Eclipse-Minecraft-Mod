package com.soldierskull.eclipse.entity.npc.cultist;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class CultistNPCModel<T extends net.minecraft.entity.LivingEntity> extends EntityModel<T> {

    private final ModelRenderer settings;
    private final ModelRenderer head;
    private final ModelRenderer tronco;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer mask;
    private final ModelRenderer face;
    private final ModelRenderer cubeR1;
    private final ModelRenderer cubeR2;
    private final ModelRenderer cubeR3;
    private final ModelRenderer cubeR4;

    public CultistNPCModel() {
        texWidth = 64;
        texHeight = 64;

        settings = new ModelRenderer(this);
        settings.setPos(0.0F, 24.0F, 0.0F);

        head = new ModelRenderer(this);
        head.setPos(0.0F, -24.0F, 0.0F);
        settings.addChild(head);
        head.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        tronco = new ModelRenderer(this);
        tronco.setPos(0.0F, -12.0F, -1.0F);
        settings.addChild(tronco);
        tronco.texOffs(0, 16).addBox(-4.0F, -12.0F, -1.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

        leftArm = new ModelRenderer(this);
        leftArm.setPos(4.0F, -22.0F, -1.0F);
        settings.addChild(leftArm);
        leftArm.texOffs(24, 16).addBox(0.0F, -2.0F, -1.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        rightArm = new ModelRenderer(this);
        rightArm.setPos(-4.0F, -22.0F, -1.0F);
        settings.addChild(rightArm);
        rightArm.texOffs(0, 32).addBox(-4.0F, -2.0F, -1.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        leftLeg = new ModelRenderer(this);
        leftLeg.setPos(2.0F, -12.0F, -1.0F);
        settings.addChild(leftLeg);
        leftLeg.texOffs(32, 0).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        rightLeg = new ModelRenderer(this);
        rightLeg.setPos(-2.0F, -12.0F, -1.0F);
        settings.addChild(rightLeg);
        rightLeg.texOffs(16, 32).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        // CORREÇÃO: mask agora é filha de 'head' com Pós Y/Z corrigidos
        mask = new ModelRenderer(this);
        mask.setPos(0.0F, 25.0F, 7.0F);
        head.addChild(mask);

        face = new ModelRenderer(this);
        face.setPos(0.0F, -24.0F, 0.0F);
        mask.addChild(face);
        face.texOffs(40, 26).addBox(-4.0F, -8.0F, -12.0F, 8.0F, 5.0F, 1.0F, 0.0F, false);
        face.texOffs(48, 12).addBox(-3.0F, -3.0F, -12.0F, 6.0F, 2.0F, 1.0F, 0.0F, false);
        face.texOffs(16, 48).addBox(-2.0F, -1.0F, -12.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        face.texOffs(16, 48).addBox(-2.0F, 0.0F, -12.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
        face.texOffs(48, 0).addBox(4.0F, -9.0F, -12.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
        face.texOffs(48, 6).addBox(-6.0F, -9.0F, -12.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);

        cubeR1 = new ModelRenderer(this);
        cubeR1.setPos(-5.0F, -7.0F, -9.0F);
        face.addChild(cubeR1);
        setRotationAngle(cubeR1, -0.8249F, -0.274F, 0.2849F);
        cubeR1.texOffs(0, 48).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);

        cubeR2 = new ModelRenderer(this);
        cubeR2.setPos(-7.0F, -3.0F, -7.0F);
        face.addChild(cubeR2);
        setRotationAngle(cubeR2, 0.4768F, -0.0376F, 0.1705F);
        cubeR2.texOffs(40, 16).addBox(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);

        cubeR3 = new ModelRenderer(this);
        cubeR3.setPos(5.0F, -7.0F, -9.0F);
        face.addChild(cubeR3);
        setRotationAngle(cubeR3, -0.8249F, 0.274F, -0.2849F);
        cubeR3.texOffs(32, 42).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);

        cubeR4 = new ModelRenderer(this);
        cubeR4.setPos(7.0F, -3.0F, -7.0F);
        face.addChild(cubeR4);
        setRotationAngle(cubeR4, 0.4768F, 0.0376F, -0.1705F);
        cubeR4.texOffs(32, 32).addBox(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * 0.017453292F;
        head.xRot = headPitch * 0.017453292F;
        // Não é necessário copiar a rotação para 'mask', ela já herda de 'head'

        rightLeg.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        leftLeg.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        rightArm.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        leftArm.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        float attackAnim = entity.getAttackAnim(1.0F);
        if (attackAnim > 0.0F) {
            rightArm.xRot -= attackAnim * 1.2F;
        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        settings.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}