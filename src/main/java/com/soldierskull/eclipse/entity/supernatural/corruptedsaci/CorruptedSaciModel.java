package com.soldierskull.eclipse.entity.supernatural.corruptedsaci;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Saci Corrompido (model_corrupted_saci.java). CORRIGIDO:
 * "left arm" e "right arm" (nomes com espaço - ilegal em Java)
 * renomeados para armLeft/armRight. Fiel ao mito: só uma perna.
 */
public class CorruptedSaciModel extends EntityModel<CorruptedSaciEntity> {

    private final ModelRenderer all;
    private final ModelRenderer body;
    private final ModelRenderer leg;
    private final ModelRenderer head;
    private final ModelRenderer armLeft;  // era "left arm"
    private final ModelRenderer armRight; // era "right arm"

    public CorruptedSaciModel() {
        texWidth = 64;
        texHeight = 64;

        all = new ModelRenderer(this);
        all.setPos(0.0F, 24.0F, 0.0F);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 0.0F, 0.0F);
        all.addChild(body);
        body.texOffs(0, 21).addBox(-2.0F, -15.0F, -1.0F, 6.0F, 8.0F, 3.0F, 0.0F, false);

        leg = new ModelRenderer(this);
        leg.setPos(3.0F, -7.0F, 0.0F);
        body.addChild(leg);
        leg.texOffs(18, 28).addBox(-2.0F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F, 0.0F, false);
        leg.texOffs(26, 14).addBox(-5.0F, 0.0F, -1.0F, 3.0F, 1.0F, 3.0F, 0.0F, false);

        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 15-25 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro pescoco, offset inverso nos filhos: repouso igual.
        head = new ModelRenderer(this);
        head.setPos(0.0F, -15.0F, 0.0F);
        body.addChild(head);
        head.texOffs(0, 0).addBox(-2.0F, -6.0F, -3.0F, 6.0F, 6.0F, 7.0F, 0.0F, false);
        head.texOffs(0, 13).addBox(-2.0F, -7.0F, -3.0F, 6.0F, 1.0F, 7.0F, 0.0F, false);
        head.texOffs(18, 21).addBox(-1.0F, -8.0F, -2.0F, 4.0F, 1.0F, 6.0F, 0.0F, false);
        head.texOffs(26, 0).addBox(-1.0F, -9.0F, -1.0F, 4.0F, 1.0F, 6.0F, 0.0F, false);
        head.texOffs(26, 7).addBox(-1.0F, -10.0F, 0.0F, 4.0F, 1.0F, 6.0F, 0.0F, false);

        armLeft = new ModelRenderer(this);
        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 6-15 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro ombro, offset inverso nos filhos: repouso igual.
        armLeft.setPos(4.0F, -15.0F, 0.0F);
        body.addChild(armLeft);
        armLeft.texOffs(30, 28).addBox(0.0F, 0.0F, -1.0F, 2.0F, 9.0F, 3.0F, 0.0F, false);

        armRight = new ModelRenderer(this);
        armRight.setPos(-2.0F, -15.0F, 0.0F);
        body.addChild(armRight);
        armRight.texOffs(0, 32).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 9.0F, 3.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(CorruptedSaciEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;
        head.yRot = netHeadYaw * 0.017453292F;

        if (entity.isStealPulseActive()) {
            animateSteal();
        } else if (entity.isFleeing()) {
            animateEscape(t);
        } else if (limbSwingAmount > 0.05F) {
            animateHop(limbSwing, limbSwingAmount);
        } else {
            animateIdle(t);
        }

        // 9.4/9.5 segurando item roubado - braço direito levemente erguido, como exibindo
        if (entity.isHoldingItem() && !entity.isStealPulseActive()) {
            armRight.xRot -= 0.6F;
        }
    }

    private void resetPose() {
        body.xRot = 0.0F;
        body.yRot = 0.0F;
        body.y = 0.0F;
        leg.xRot = 0.0F;
        armLeft.xRot = 0.0F;
        armRight.xRot = 0.0F;
    }

    /** 9.1 IDLE - equilibrado numa perna, gira o corpo, brinca com o gorro, inclina a cabeça. */
    private void animateIdle(float t) {
        body.yRot = MathHelper.sin(t * 0.6F) * 0.4F;
        head.xRot = MathHelper.sin(t * 0.5F + 1.0F) * 0.15F;
        leg.xRot = MathHelper.sin(t * 0.8F) * 0.08F;
        armLeft.xRot = MathHelper.sin(t * 1.0F) * 0.1F;
        armRight.xRot = MathHelper.sin(t * 1.0F + 2.0F) * 0.1F; // "brincando com o gorro" (aproximado)
    }

    /** 9.2 MOVEMENT - salta em vez de caminhar (agacha -> salta -> inclina -> aterrissa). */
    private void animateHop(float limbSwing, float limbSwingAmount) {
        float cycle = (limbSwing * 0.3F) % 1.0F;
        float hop = MathHelper.sin(cycle * (float) Math.PI);
        body.y = -hop * 4.0F * limbSwingAmount;
        body.zRot = hop * 0.15F; // inclina-se no ar
        leg.xRot = -hop * 0.6F;
        armLeft.xRot = hop * 0.4F;
        armRight.xRot = hop * 0.4F;
    }

    /** 9.4/9.5 STEAL - inclina a cabeça, estende a mão, pega o item. */
    private void animateSteal() {
        head.xRot = 0.3F;
        armRight.xRot = -1.1F; // estende o braço
        body.yRot = 0.3F;
    }

    /** 9.6 ESCAPE - saltos rapidos, ritmo bem maior que o normal. */
    private void animateEscape(float t) {
        float hop = Math.abs(MathHelper.sin(t * 10.0F));
        body.y = -hop * 5.0F;
        leg.xRot = -hop * 0.8F;
        armLeft.xRot = hop * 0.5F;
        armRight.xRot = -0.6F; // segura o item roubado enquanto foge
    }

    /** 9.7 DEATH - perde equilibrio, cai, rola. */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 10.0F, 0.0F, 1.0F);
        body.xRot = progress * 1.5F;
        body.zRot = progress * 1.2F;
        body.y = progress * 4.0F;
        leg.xRot = progress * 0.8F;
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
