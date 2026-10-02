package com.soldierskull.eclipse.entity.npc.hunter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do NPC Caçador (hunter_npc.java, Blockbench).
 * CORRIGIDO: "cabeça" renomeado para "head" (acento em identificador
 * compila, mas quebra facil dependendo do encoding do arquivo - os
 * outros modelos do projeto ja seguem nomes sem acento).
 *
 * Detalhe proprio do NPC: o "hat" (chapeu de aba larga) e uma peca
 * separada, filha de "settings" - entao ele acompanha o corpo, mas NAO
 * gira junto com a cabeca automaticamente. Pra o chapeu seguir o olhar,
 * a animacao copia a rotacao da cabeca pra ele (ver setupAnim).
 */
public class HunterNPCModel<T extends net.minecraft.entity.LivingEntity> extends EntityModel<T> {

    private final ModelRenderer settings;
    private final ModelRenderer head;
    private final ModelRenderer tronco;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer hat;

    public HunterNPCModel() {
        texWidth = 64;
        texHeight = 64;

        settings = new ModelRenderer(this);
        settings.setPos(0.0F, 24.0F, 0.0F);

        head = new ModelRenderer(this);
        head.setPos(0.0F, -24.0F, 0.0F);
        settings.addChild(head);
        head.texOffs(0, 13).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 5.0F, 8.0F, 0.0F, false);

        tronco = new ModelRenderer(this);
        tronco.setPos(0.0F, -12.0F, -1.0F);
        settings.addChild(tronco);
        tronco.texOffs(32, 13).addBox(-4.0F, -12.0F, -1.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

        leftArm = new ModelRenderer(this);
        leftArm.setPos(4.0F, -22.0F, -1.0F);
        settings.addChild(leftArm);
        leftArm.texOffs(32, 29).addBox(0.0F, -2.0F, -1.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        rightArm = new ModelRenderer(this);
        rightArm.setPos(-4.0F, -22.0F, -1.0F);
        settings.addChild(rightArm);
        rightArm.texOffs(0, 38).addBox(-4.0F, -2.0F, -1.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        leftLeg = new ModelRenderer(this);
        leftLeg.setPos(2.0F, -12.0F, -1.0F);
        settings.addChild(leftLeg);
        leftLeg.texOffs(16, 38).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        rightLeg = new ModelRenderer(this);
        rightLeg.setPos(-2.0F, -12.0F, -1.0F);
        settings.addChild(rightLeg);
        rightLeg.texOffs(32, 45).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        // BUGFIX (mesmo padrao do "mask" do CultistNPCModel): "hat" era
        // filha de "settings" (a raiz) com pivo em (0,1,0) - em absoluto,
        // y=25, a mais de 1,5 bloco da geometria dele (que fica em y -10..-5,
        // bem em cima da cabeca). Na pose parada isso nao aparecia, mas como
        // setupAnim copiava a rotacao da cabeca pra "hat" manualmente, o
        // chapeu girava em torno do PROPRIO pivo distante em vez de em
        // torno do pescoco - a cada movimento de camera do NPC ele se
        // descolava da cabeca (mesmo bug corrigido na mascara do Cultista).
        // Fix: "hat" agora e filho de "head" (pivota certo, no pescoco), Y
        // ajustado (+24) pra pose parada ficar EXATAMENTE igual a de antes.
        hat = new ModelRenderer(this);
        hat.setPos(0.0F, 25.0F, 0.0F);
        head.addChild(hat);
        hat.texOffs(0, 26).addBox(-4.0F, -35.0F, -4.0F, 8.0F, 4.0F, 8.0F, 0.0F, false);
        hat.texOffs(0, 0).addBox(-6.0F, -31.0F, -6.0F, 12.0F, 1.0F, 12.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * 0.017453292F;
        head.xRot = headPitch * 0.017453292F;
        // Não copia mais a rotação pro "hat" manualmente: agora que ele é
        // filho de "head", a rotação já é herdada automaticamente pela
        // hierarquia - copiá-la de novo somaria a mesma rotação duas vezes.

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
