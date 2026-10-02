// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
package com.soldierskull.eclipse.entity.supernatural.ghost;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class GhostModel extends EntityModel<GhostEntity> {
	private final ModelRenderer controller;
	private final ModelRenderer bipedBody;
	private final ModelRenderer bipedHead;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedRightLeg;

	public GhostModel() {
		texWidth = 64;
		texHeight = 64;

		controller = new ModelRenderer(this);
		controller.setPos(0.0F, 12.0F, 0.0F);

		bipedBody = new ModelRenderer(this);
		bipedBody.setPos(0.0F, -12.0F, 0.0F);
		controller.addChild(bipedBody);
		bipedBody.texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

		bipedHead = new ModelRenderer(this);
		bipedHead.setPos(0.0F, -4.0F, 0.0F);
		bipedBody.addChild(bipedHead);
		bipedHead.texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setPos(5.0F, 2.0F, 0.0F);
		bipedBody.addChild(bipedLeftArm);
		bipedLeftArm.texOffs(0, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setPos(-5.0F, 2.0F, 0.0F);
		bipedBody.addChild(bipedRightArm);
		bipedRightArm.texOffs(24, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setPos(-1.9F, 0.0F, 0.0F);
		controller.addChild(bipedRightLeg);
		bipedRightLeg.texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, 0.0F, false);
		bipedRightLeg.texOffs(32, 0).addBox(-2.0F, 2.0F, 0.0F, 8.0F, 6.0F, 4.0F, 0.0F, false);
		// BUGFIX (z-fighting): esta caixa comecava exatamente em z=4.0, onde a
		// caixa anterior termina - as duas faces ficavam coplanares em
		// x -2..6 / y 6..8 e piscavam. Puxei o inicio pra z=3 e aumentei a
		// profundidade de 4 pra 5: a silhueta termina no mesmo z=8, mas agora
		// a face de tras fica DENTRO do volume da caixa anterior (oculta) em
		// vez de encostada nela. Correcao de geometria, sem offset fracionario.
		bipedRightLeg.texOffs(32, 0).addBox(-2.0F, 6.0F, 3.0F, 8.0F, 2.0F, 5.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(GhostEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// Animação de rotação da cabeça seguindo a visão do jogador/alvo
		this.bipedHead.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.bipedHead.xRot = headPitch * ((float)Math.PI / 180F);

		// Animação suave para os braços balançarem
		this.bipedRightArm.xRot = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		this.bipedLeftArm.xRot = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		controller.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}