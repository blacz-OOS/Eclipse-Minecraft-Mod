package com.soldierskull.eclipse.entity.supernatural.bloodworm;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Vorme de Sangue, baseado no Blockbench (blood_worm.java).
 * CORRIGIDO: os 4 segmentos do corpo foram exportados com nomes "1",
 * "2", "3", "4" (identificadores puramente numericos - ilegal em Java).
 * Renomeados para segment1..segment4, mesma geometria/pivos originais.
 *
 * Arvore (como exportada): vorme -> head -> [segment1, segment2,
 * segment3, segment4] (os 4 segmentos sao filhos DIRETOS da head, nao
 * uma corrente segment1->segment2->segment3->segment4). Para a
 * ondulacao (spec 2.3/2.4) isso significa animar cada segmento com uma
 * fase de seno propria baseada na posicao ao longo do corpo, em vez de
 * uma corrente de rotacao acumulada.
 */
public class BloodWormModel extends EntityModel<BloodWormEntity> {

    private final ModelRenderer vorme;
    private final ModelRenderer head;
    private final ModelRenderer segment1;
    private final ModelRenderer segment2;
    private final ModelRenderer segment3;
    private final ModelRenderer segment4;

    public BloodWormModel() {
        texWidth = 64;
        texHeight = 64;

        vorme = new ModelRenderer(this);
        vorme.setPos(0.0F, 24.0F, 0.0F);

        head = new ModelRenderer(this);
        head.setPos(0.0F, -2.0F, -11.0F);
        vorme.addChild(head);
        head.texOffs(24, 0).addBox(-2.0F, -2.0F, -7.0F, 4.0F, 3.0F, 7.0F, 0.0F, false);

        segment1 = new ModelRenderer(this); // era "1"
        segment1.setPos(0.0F, 0.0F, 0.0F);
        head.addChild(segment1);
        segment1.texOffs(0, 0).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 3.0F, 8.0F, 0.0F, false);

        segment2 = new ModelRenderer(this); // era "2"
        segment2.setPos(0.0F, 2.0F, 11.0F);
        head.addChild(segment2);
        segment2.texOffs(0, 11).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 3.0F, 8.0F, 0.0F, false);

        segment3 = new ModelRenderer(this); // era "3"
        segment3.setPos(0.0F, 2.0F, 11.0F);
        head.addChild(segment3);
        segment3.texOffs(0, 22).addBox(-2.0F, -4.0F, 2.0F, 4.0F, 3.0F, 8.0F, 0.0F, false);

        segment4 = new ModelRenderer(this); // era "4"
        segment4.setPos(0.0F, 2.0F, 22.0F);
        head.addChild(segment4);
        segment4.texOffs(24, 10).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 2.0F, 8.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(BloodWormEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        byte state = entity.getState();
        float progress = entity.getStateProgress();
        float t = ageInTicks / 20.0F;

        switch (state) {
            case BloodWormEntity.STATE_EMERGING:
                animateEmergence(progress);
                break;
            case BloodWormEntity.STATE_SUBMERGING:
                animateBurrowing(progress);
                break;
            case BloodWormEntity.STATE_WINDUP:
                animateWindup(progress);
                break;
            case BloodWormEntity.STATE_DASH:
                animateDash(progress);
                break;
            case BloodWormEntity.STATE_SURFACED:
            default:
                animateSurfacedIdle(t, limbSwingAmount);
                break;
        }
    }

    private void resetPose() {
        vorme.y = 24.0F;
        vorme.z = 0.0F;
        head.xRot = 0.0F;
        head.y = -2.0F;
        head.z = -11.0F;
        for (ModelRenderer seg : new ModelRenderer[]{segment1, segment2, segment3, segment4}) {
            seg.xRot = 0.0F;
        }
    }

    /** 2.2 EMERGENCE - cabeca surge primeiro, corpo acompanha em onda; sobe do subterraneo. */
    private void animateEmergence(float progress) {
        float sunk = (1.0F - progress) * 10.0F;
        vorme.y = 24.0F + sunk; // comeca afundado, sobe ate a posicao normal

        // cabeca "adianta" a subida em relacao ao resto (chega na posicao ~30% antes)
        float headLift = MathHelper.clamp(progress * 1.4F, 0.0F, 1.0F);
        head.xRot = -0.3F * (1.0F - headLift); // inclina pra cima ao emergir

        applyWaveLag(progress);
    }

    /** 2.7 BURROWING - inverso da emergencia, afunda de volta. */
    private void animateBurrowing(float progress) {
        float sunk = progress * 10.0F;
        vorme.y = 24.0F + sunk;
        head.xRot = 0.15F * progress; // cabeca abaixa entrando no solo
        applyWaveLag(1.0F - progress);
    }

    /** 2.3/2.4 IDLE + MOVEMENT em superficie - ondulacao continua, nunca imovel. */
    private void animateSurfacedIdle(float t, float limbSwingAmount) {
        float speedFactor = 1.0F + limbSwingAmount * 1.5F; // ondula mais rapido se movendo
        float wave = 0.12F;

        head.xRot = MathHelper.sin(t * 3.0F * speedFactor) * 0.10F;
        segment1.xRot = MathHelper.sin(t * 3.0F * speedFactor + 0.6F) * wave;
        segment2.xRot = MathHelper.sin(t * 3.0F * speedFactor + 1.2F) * wave;
        segment3.xRot = MathHelper.sin(t * 3.0F * speedFactor + 1.8F) * wave;
        segment4.xRot = MathHelper.sin(t * 3.0F * speedFactor + 2.4F) * wave;
    }

    /** 2.5 preparacao (~0.25s): cabeca abaixa, corpo contrai, recua ligeiramente. */
    private void animateWindup(float progress) {
        head.xRot = 0.35F * progress; // cabeca abaixa
        vorme.z = -1.5F * progress;   // recua um pouco

        float contract = progress * 0.15F;
        segment1.xRot = contract;
        segment2.xRot = contract * 0.8F;
        segment3.xRot = contract * 0.6F;
        segment4.xRot = contract * 0.4F;
    }

    /** 2.5 dash - cabeca dispara pra frente, corpo acompanha depois. */
    private void animateDash(float progress) {
        float lunge = MathHelper.sin(Math.min(1.0F, progress) * (float) Math.PI); // vai e volta suavemente
        vorme.z = 3.0F * lunge;
        head.xRot = -0.25F * lunge;
        segment1.xRot = -0.1F * lunge;
        segment2.xRot = 0.05F * lunge;
    }

    /** Aplica a onda com atraso crescente por segmento (usado durante emergir/submergir). */
    private void applyWaveLag(float progress) {
        segment1.xRot = MathHelper.sin(progress * 6.0F) * 0.08F;
        segment2.xRot = MathHelper.sin(progress * 6.0F - 0.5F) * 0.10F;
        segment3.xRot = MathHelper.sin(progress * 6.0F - 1.0F) * 0.10F;
        segment4.xRot = MathHelper.sin(progress * 6.0F - 1.5F) * 0.12F;
    }

    /** 2.8 DEATH - espasmo, corpo se contorce, enrola parcialmente, perde forca. */
    private void animateDeath(int deathTime) {
        float t = deathTime / 20.0F;
        if (deathTime < 4) {
            // espasmo inicial rapido (~0.2s)
            float spasm = MathHelper.sin(t * 40.0F) * 0.3F;
            head.xRot = spasm;
            segment1.xRot = -spasm;
            segment2.xRot = spasm * 0.7F;
            segment3.xRot = -spasm * 0.7F;
            segment4.xRot = spasm * 0.5F;
        } else {
            // enrola e perde forca
            float curl = MathHelper.clamp((deathTime - 4) / 16.0F, 0.0F, 1.0F);
            head.xRot = curl * 0.6F;
            segment1.xRot = curl * 0.5F;
            segment2.xRot = curl * 0.4F;
            segment3.xRot = curl * 0.3F;
            segment4.xRot = curl * 0.2F;
        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        vorme.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
