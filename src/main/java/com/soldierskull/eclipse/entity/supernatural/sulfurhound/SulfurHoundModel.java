package com.soldierskull.eclipse.entity.supernatural.sulfurhound;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Cão de Chofre (model_hound_of_sulfur.java). CORRIGIDO:
 * "perna dianteria direita" (com typo no proprio Blockbench), "perna
 * dianteira esquerda", "perna traseira direita", "perna traseira
 * esquerda" - todos com espaço (ilegal em Java) - renomeados para
 * legFrontRight/legFrontLeft/legBackRight/legBackLeft. "cabeça"/"corpo"/
 * "calda" (sem espaço) mantidos como cabeca/corpo/calda.
 */
public class SulfurHoundModel extends EntityModel<SulfurHoundEntity> {

    private final ModelRenderer all;
    private final ModelRenderer legFrontRight; // era "perna dianteria direita"
    private final ModelRenderer legFrontLeft;  // era "perna dianteira esquerda"
    private final ModelRenderer legBackRight;  // era "perna traseira direita"
    private final ModelRenderer legBackLeft;   // era "perna traseira esquerda"
    private final ModelRenderer corpo;
    private final ModelRenderer cabeca;
    private final ModelRenderer calda;

    public SulfurHoundModel() {
        texWidth = 64;
        texHeight = 64;

        all = new ModelRenderer(this);
        all.setPos(0.0F, 24.0F, -5.0F);

        legFrontRight = new ModelRenderer(this);
        legFrontRight.setPos(-2.5F, -8.0F, -0.5F);
        all.addChild(legFrontRight);
        legFrontRight.texOffs(24, 30).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, 0.0F, false);

        legFrontLeft = new ModelRenderer(this);
        legFrontLeft.setPos(1.5F, -8.0F, -0.5F);
        all.addChild(legFrontLeft);
        legFrontLeft.texOffs(34, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, 0.0F, false);

        // BUGFIX: no export, a peca em x=+1.5 (lado ESQUERDO no espaco do MC,
        // onde -X e a direita) estava atribuida a "legBackRight" e a de
        // x=-2.5 a "legBackLeft" - os nomes estavam invertidos em relacao ao
        // lado fisico. Como animateRun() pareia FrontRight+BackLeft (marcha
        // diagonal correta de quadrupede), o resultado era as duas pernas do
        // MESMO lado em fase, virando um salto de coelho em vez de trote.
        // Troquei setPos E texOffs juntos: a geometria e a UV no mundo ficam
        // identicas ao export, so os nomes agora correspondem ao lado real.
        legBackRight = new ModelRenderer(this);
        legBackRight.setPos(-2.5F, -8.0F, 10.5F);
        all.addChild(legBackRight);
        legBackRight.texOffs(36, 36).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, 0.0F, false);

        legBackLeft = new ModelRenderer(this);
        legBackLeft.setPos(1.5F, -8.0F, 10.5F);
        all.addChild(legBackLeft);
        legBackLeft.texOffs(36, 25).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, 0.0F, false);

        corpo = new ModelRenderer(this);
        corpo.setPos(0.0F, -11.0F, 4.0F);
        all.addChild(corpo);
        corpo.texOffs(0, 0).addBox(-4.0F, -3.0F, -2.0F, 7.0F, 6.0F, 10.0F, 0.0F, false);
        corpo.texOffs(0, 16).addBox(-5.0F, -4.0F, -8.0F, 9.0F, 8.0F, 6.0F, 0.0F, false);

        cabeca = new ModelRenderer(this);
        cabeca.setPos(0.0F, -11.0F, -4.0F);
        all.addChild(cabeca);
        cabeca.texOffs(0, 41).addBox(-2.0F, 0.0F, -6.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        cabeca.texOffs(30, 16).addBox(-4.0F, -3.0F, -3.0F, 7.0F, 6.0F, 3.0F, 0.0F, false);
        cabeca.texOffs(30, 25).addBox(-4.0F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        cabeca.texOffs(34, 11).addBox(1.0F, -5.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

        calda = new ModelRenderer(this);
        calda.setPos(-0.5F, -13.0F, 10.0F);
        all.addChild(calda);
        calda.texOffs(0, 30).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 9.0F, 0.0F, false);
        calda.texOffs(12, 41).addBox(-0.5F, -2.0F, 4.0F, 1.0F, 4.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(SulfurHoundEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;
        cabeca.yRot = netHeadYaw * 0.017453292F;
        cabeca.xRot = headPitch * 0.017453292F;

        if (limbSwingAmount > 0.02F) {
            // 10.3 RUN - corrida quadrupede, mais agressiva que lobo vanilla
            legFrontRight.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            legFrontLeft.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
            legBackRight.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
            legBackLeft.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            calda.xRot = -0.3F + MathHelper.sin(t * 6.0F) * 0.1F;
        } else {
            // 10.1 IDLE - cheira o chão, respira, orelhas se mexem (aproximado na cabeça/cauda)
            cabeca.xRot += MathHelper.sin(t * 0.6F) * 0.1F - 0.1F;
            calda.xRot = MathHelper.sin(t * 1.2F) * 0.15F;
            corpo.y = -11.0F + MathHelper.sin(t * 1.0F) * 0.15F; // respiracao
        }

        // 10.2 ALERT - orelhas levantam, cabeça baixa, postura agressiva
        if (entity.isAlertPulseActive()) {
            cabeca.xRot -= 0.25F;
            corpo.xRot = 0.1F;
        }

        // ataque - mordida rapida
        float attackAnim = entity.getAttackAnim(1.0F);
        if (attackAnim > 0.0F) {
            cabeca.xRot -= attackAnim * 0.5F;
        }

        // 10.6 FIRE VISUAL - leve vibracao ao aplicar fogo (usa o proprio isOnFire do alvo nao existe aqui;
        // aproximado via chance de "excitacao" quando tem alvo e esta atacando)
    }

    private void resetPose() {
        // BUGFIX: animateDeath() mexe em all.zRot/all.y, e a instancia de
        // ModelRenderer e COMPARTILHADA por todas as entidades deste tipo.
        // Sem resetar aqui, depois que um cao morre todos os outros passam a
        // renderizar tombados de lado e elevados.
        all.zRot = 0.0F;
        all.y = 24.0F;
        corpo.xRot = 0.0F;
        corpo.y = -11.0F;
        cabeca.xRot = 0.0F;
        cabeca.yRot = 0.0F;
        calda.xRot = 0.0F;
        legFrontRight.xRot = 0.0F;
        legFrontLeft.xRot = 0.0F;
        legBackRight.xRot = 0.0F;
        legBackLeft.xRot = 0.0F;
    }

    /** 10.7 DEATH - queda lateral como animal, nunca postura humanoide. */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 10.0F, 0.0F, 1.0F);
        all.zRot = progress * 1.5F; // tomba de lado
        all.y = 24.0F + progress * 6.0F;
        legFrontRight.xRot = progress * 0.4F;
        legBackLeft.xRot = -progress * 0.4F;
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
