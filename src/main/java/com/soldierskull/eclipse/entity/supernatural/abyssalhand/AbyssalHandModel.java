package com.soldierskull.eclipse.entity.supernatural.abyssalhand;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real da Mao Abissal, baseado no Blockbench exportado pelo
 * usuario (model_abyssal_hand.java). CORRIGIDO: o Blockbench exportou
 * um campo chamado "mindinho 3" (com espaco), que nao compila em Java -
 * renomeado para "mindinho3", unica mudanca estrutural no modelo em si.
 *
 * Arvore (conforme exportado, pivos preservados como vieram do Blockbench):
 * hand_all
 *  |- punho
 *  |- palma
 *      |- polegar -> polegar1 -> cube_r1 (estatico) ; polegar2 -> cube_r2 (estatico)
 *      |- indicador -> indicador1 -> indicador2 -> indicador3
 *      |- medio -> medio1 -> medio2 -> medio3
 *      |- anelar -> anelar1 -> anelar2 -> anelar3
 *      |- mindinho -> mindinho1 -> mindinho2 -> mindinho3
 *
 * cube_r1/cube_r2 sao bones "cube_r" que o Blockbench cria quando uma
 * caixa individual esta rotacionada no editor - representam a curvatura
 * ESCULPIDA do polegar, nao uma junta animavel. Por isso a animacao
 * rotaciona "polegar"/"polegar1"/"polegar2" (as juntas reais), nunca
 * cube_r1/cube_r2 diretamente.
 */
public class AbyssalHandModel extends EntityModel<AbyssalHandEntity> {

    private final ModelRenderer handAll;
    private final ModelRenderer punho;
    private final ModelRenderer palma;

    private final ModelRenderer polegar;
    private final ModelRenderer polegar1;
    private final ModelRenderer cubeR1;
    private final ModelRenderer polegar2;
    private final ModelRenderer cubeR2;

    private final ModelRenderer indicador;
    private final ModelRenderer indicador1;
    private final ModelRenderer indicador2;
    private final ModelRenderer indicador3;

    private final ModelRenderer medio;
    private final ModelRenderer medio1;
    private final ModelRenderer medio2;
    private final ModelRenderer medio3;

    private final ModelRenderer anelar;
    private final ModelRenderer anelar1;
    private final ModelRenderer anelar2;
    private final ModelRenderer anelar3;

    private final ModelRenderer mindinho;
    private final ModelRenderer mindinho1;
    private final ModelRenderer mindinho2;
    private final ModelRenderer mindinho3;

    // fases de tremor individuais por dedo, pra idle nao ficar sincronizado/robotico
    private static final float PHASE_POLEGAR = 0.0F;
    private static final float PHASE_INDICADOR = 1.3F;
    private static final float PHASE_MEDIO = 2.7F;
    private static final float PHASE_ANELAR = 4.1F;
    private static final float PHASE_MINDINHO = 5.5F;

    public AbyssalHandModel() {
        texWidth = 128;
        texHeight = 128;

        handAll = new ModelRenderer(this);
        handAll.setPos(-1.0F, 8.0F, 0.0F);

        punho = new ModelRenderer(this);
        punho.setPos(2.0F, 16.0F, 0.0F);
        handAll.addChild(punho);
        punho.texOffs(0, 22).addBox(-9.0F, -15.0F, -4.0F, 15.0F, 15.0F, 8.0F, 0.0F, false);

        palma = new ModelRenderer(this);
        palma.setPos(1.0F, 7.0F, 0.0F);
        handAll.addChild(palma);
        palma.texOffs(0, 0).addBox(-10.0F, -14.0F, -4.0F, 19.0F, 14.0F, 8.0F, 0.0F, false);

        // ---- polegar ----
        polegar = new ModelRenderer(this);
        polegar.setPos(-8.0F, -5.0F, 0.0F);
        palma.addChild(polegar);

        polegar1 = new ModelRenderer(this);
        polegar1.setPos(-1.0F, 0.0F, 0.0F);
        polegar.addChild(polegar1);

        cubeR1 = new ModelRenderer(this);
        cubeR1.setPos(1.0F, 3.0F, 0.0F);
        polegar1.addChild(cubeR1);
        setRotationAngle(cubeR1, 0.0F, 0.0F, 0.3927F);
        cubeR1.texOffs(46, 22).addBox(-9.0F, -5.0F, -3.0F, 10.0F, 5.0F, 6.0F, 0.0F, false);

        polegar2 = new ModelRenderer(this);
        polegar2.setPos(-4.0F, -2.0F, 0.0F);
        polegar1.addChild(polegar2);

        cubeR2 = new ModelRenderer(this);
        cubeR2.setPos(0.0F, 3.0F, 0.0F);
        polegar2.addChild(cubeR2);
        setRotationAngle(cubeR2, 0.0F, 0.0F, 0.3927F);
        cubeR2.texOffs(46, 33).addBox(-9.0F, -5.0F, -3.0F, 10.0F, 5.0F, 6.0F, 0.0F, false);

        // ---- indicador ----
        indicador = new ModelRenderer(this);
        indicador.setPos(-7.0F, -13.0F, 0.0F);
        palma.addChild(indicador);

        indicador1 = new ModelRenderer(this);
        indicador1.setPos(0.0F, 0.0F, 0.0F);
        indicador.addChild(indicador1);
        indicador1.texOffs(0, 49).addBox(-3.0F, -9.0F, -3.0F, 4.0F, 10.0F, 6.0F, 0.0F, false);

        indicador2 = new ModelRenderer(this);
        indicador2.setPos(-1.0F, -7.0F, 0.0F);
        indicador1.addChild(indicador2);
        indicador2.texOffs(40, 61).addBox(-2.0F, -9.0F, -3.0F, 4.0F, 8.0F, 6.0F, 0.0F, false);

        indicador3 = new ModelRenderer(this);
        indicador3.setPos(0.0F, -9.0F, 0.0F);
        indicador2.addChild(indicador3);
        indicador3.texOffs(20, 65).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 5.0F, 6.0F, 0.0F, false);

        // ---- medio ----
        medio = new ModelRenderer(this);
        medio.setPos(-3.0F, -13.0F, 0.0F);
        palma.addChild(medio);

        medio1 = new ModelRenderer(this);
        medio1.setPos(0.0F, 0.0F, 0.0F);
        medio.addChild(medio1);
        medio1.texOffs(46, 44).addBox(-2.0F, -10.0F, -3.0F, 4.0F, 11.0F, 6.0F, 0.0F, false);

        medio2 = new ModelRenderer(this);
        medio2.setPos(0.0F, -10.0F, 0.0F);
        medio1.addChild(medio2);
        medio2.texOffs(54, 0).addBox(-2.0F, -8.0F, -3.0F, 4.0F, 9.0F, 6.0F, 0.0F, false);

        medio3 = new ModelRenderer(this);
        medio3.setPos(0.0F, -8.0F, 0.0F);
        medio2.addChild(medio3);
        medio3.texOffs(66, 44).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 5.0F, 6.0F, 0.0F, false);

        // ---- anelar ----
        anelar = new ModelRenderer(this);
        anelar.setPos(2.0F, -12.0F, 0.0F);
        palma.addChild(anelar);

        anelar1 = new ModelRenderer(this);
        anelar1.setPos(0.0F, 0.0F, 0.0F);
        anelar.addChild(anelar1);
        anelar1.texOffs(20, 49).addBox(-2.0F, -10.0F, -3.0F, 4.0F, 10.0F, 6.0F, 0.0F, false);

        anelar2 = new ModelRenderer(this);
        anelar2.setPos(0.0F, -10.0F, 0.0F);
        anelar1.addChild(anelar2);
        anelar2.texOffs(60, 61).addBox(-2.0F, -7.0F, -3.0F, 4.0F, 8.0F, 6.0F, 0.0F, false);

        anelar3 = new ModelRenderer(this);
        anelar3.setPos(0.0F, -6.0F, 0.0F);
        anelar2.addChild(anelar3);
        anelar3.texOffs(74, 0).addBox(-2.0F, -5.0F, -3.0F, 4.0F, 5.0F, 6.0F, 0.0F, false);

        // ---- mindinho ----
        mindinho = new ModelRenderer(this);
        mindinho.setPos(7.0F, -13.0F, 0.0F);
        palma.addChild(mindinho);

        mindinho1 = new ModelRenderer(this);
        mindinho1.setPos(0.0F, 0.0F, 0.0F);
        mindinho.addChild(mindinho1);
        mindinho1.texOffs(0, 65).addBox(-2.0F, -7.0F, -3.0F, 4.0F, 8.0F, 6.0F, 0.0F, false);

        mindinho2 = new ModelRenderer(this);
        mindinho2.setPos(0.0F, -6.0F, 0.0F);
        mindinho1.addChild(mindinho2);
        mindinho2.texOffs(74, 11).addBox(-2.0F, -5.0F, -3.0F, 4.0F, 5.0F, 6.0F, 0.0F, false);

        mindinho3 = new ModelRenderer(this); // CORRIGIDO: era "mindinho 3" (nome ilegal em Java)
        mindinho3.setPos(0.0F, -4.0F, 0.0F);
        mindinho2.addChild(mindinho3);
        mindinho3.texOffs(40, 75).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(AbyssalHandEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        if (entity.isGrabbing()) {
            animateGrabbing(entity.getGrabProgress(), ageInTicks);
        } else if (entity.isDetecting()) {
            animateDetection(entity.getDetectionProgress());
        } else {
            animateIdle(ageInTicks);
        }
    }

    private void resetPose() {
        handAll.y = 8.0F;
        for (ModelRenderer finger : new ModelRenderer[]{polegar, polegar1, polegar2, indicador, indicador1,
                indicador2, indicador3, medio, medio1, medio2, medio3, anelar, anelar1, anelar2, anelar3,
                mindinho, mindinho1, mindinho2, mindinho3}) {
            finger.xRot = 0.0F;
        }
        palma.xRot = 0.0F;
        palma.y = 7.0F;
    }

    /** 1.1 IDLE - dedos levemente curvados, tremores individuais discretos, nunca perfeitamente parada. */
    private void animateIdle(float ageInTicks) {
        float t = ageInTicks / 20.0F;
        // curvatura de repouso leve em todos os dedos (nunca retos)
        applyCurl(polegar, polegar1, polegar2, 0.12F);
        applyCurl(indicador, indicador1, indicador2, indicador3, 0.10F);
        applyCurl(medio, medio1, medio2, medio3, 0.10F);
        applyCurl(anelar, anelar1, anelar2, anelar3, 0.10F);
        applyCurl(mindinho, mindinho1, mindinho2, mindinho3, 0.10F);

        // tremores individuais - cada dedo com fase propria, amplitude pequena
        indicador.xRot += MathHelper.sin(t * 1.3F + PHASE_INDICADOR) * 0.03F;
        medio.xRot += MathHelper.sin(t * 1.1F + PHASE_MEDIO) * 0.03F;
        anelar.xRot += MathHelper.sin(t * 1.4F + PHASE_ANELAR) * 0.03F;
        mindinho.xRot += MathHelper.sin(t * 1.6F + PHASE_MINDINHO) * 0.03F;
        polegar.xRot += MathHelper.sin(t * 1.0F + PHASE_POLEGAR) * 0.03F;

        // palma sobe/desce muito sutilmente
        palma.y = 7.0F - MathHelper.sin(t * 0.5F) * 0.3F;
    }

    /** 1.2 DETECTION - dedos abrem, palma se inclina na direcao do jogador antes do agarrao. */
    private void animateDetection(float progress) {
        float open = 1.0F - progress; // comeca com a curva do idle e abre
        applyCurl(polegar, polegar1, polegar2, 0.12F * open);
        applyCurl(indicador, indicador1, indicador2, indicador3, 0.10F * open);
        applyCurl(medio, medio1, medio2, medio3, 0.10F * open);
        applyCurl(anelar, anelar1, anelar2, anelar3, 0.10F * open);
        applyCurl(mindinho, mindinho1, mindinho2, mindinho3, 0.10F * open);

        palma.xRot = -0.15F * progress; // inclina pra frente
        palma.y = 7.0F - progress * 0.5F; // levanta um pouco
    }

    /**
     * 1.3/1.4 GRAB - fecha rapido (polegar -> indicador -> resto -> palma)
     * nos primeiros ticks, depois tremores sustentados de "segurando".
     */
    private void animateGrabbing(float progress, float ageInTicks) {
        // janela de fechamento: primeiros 20% do agarrao (~1s de 5s)
        float closeWindow = Math.min(1.0F, progress / 0.20F);

        float polegarClose = MathHelper.clamp(closeWindow * 1.4F, 0.0F, 1.0F);
        float indicadorClose = MathHelper.clamp((closeWindow - 0.15F) * 1.6F, 0.0F, 1.0F);
        float restClose = MathHelper.clamp((closeWindow - 0.30F) * 1.8F, 0.0F, 1.0F);

        float fullCurl = 1.15F; // dedos bem fechados

        applyCurl(polegar, polegar1, polegar2, fullCurl * polegarClose);
        applyCurl(indicador, indicador1, indicador2, indicador3, fullCurl * indicadorClose);
        applyCurl(medio, medio1, medio2, medio3, fullCurl * restClose);
        applyCurl(anelar, anelar1, anelar2, anelar3, fullCurl * restClose);
        applyCurl(mindinho, mindinho1, mindinho2, mindinho3, fullCurl * restClose);

        palma.xRot = -0.08F * restClose; // palma contrai levemente

        // 1.4 tremores sustentados depois de fechada
        float t = ageInTicks / 20.0F;
        float tremble = 0.02F;
        polegar.xRot += MathHelper.sin(t * 6.0F + PHASE_POLEGAR) * tremble;
        indicador.xRot += MathHelper.sin(t * 6.3F + PHASE_INDICADOR) * tremble;
        medio.xRot += MathHelper.sin(t * 5.8F + PHASE_MEDIO) * tremble;
        anelar.xRot += MathHelper.sin(t * 6.5F + PHASE_ANELAR) * tremble;
        mindinho.xRot += MathHelper.sin(t * 6.1F + PHASE_MINDINHO) * tremble;
        palma.xRot += MathHelper.sin(t * 4.0F) * 0.015F * restClose;
    }

    /** 1.5 DEATH (~1s = 20 ticks) - dedos relaxam, pulso afunda, mao entra no chao. */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 20.0F, 0.0F, 1.0F);
        float relax = 1.0F - progress;

        applyCurl(polegar, polegar1, polegar2, 0.12F * relax);
        applyCurl(indicador, indicador1, indicador2, indicador3, 0.10F * relax);
        applyCurl(medio, medio1, medio2, medio3, 0.10F * relax);
        applyCurl(anelar, anelar1, anelar2, anelar3, 0.10F * relax);
        applyCurl(mindinho, mindinho1, mindinho2, mindinho3, 0.10F * relax);

        handAll.y = 8.0F + progress * 16.0F; // afunda no chao
    }

    /** Curva um dedo de 3 segmentos, distribuindo mais rotacao na base. */
    private void applyCurl(ModelRenderer base, ModelRenderer seg1, ModelRenderer seg2, ModelRenderer seg3, float amount) {
        base.xRot = amount * 0.9F;
        seg1.xRot = amount * 0.9F;
        seg2.xRot = amount * 1.0F;
        seg3.xRot = amount * 1.1F;
    }

    /** Overload pro polegar, que só tem 2 segmentos animáveis (polegar1/polegar2, já que cube_r1/cube_r2 são estáticos). */
    private void applyCurl(ModelRenderer base, ModelRenderer seg1, ModelRenderer seg2, float amount) {
        base.xRot = amount * 0.9F;
        seg1.xRot = amount * 1.0F;
        seg2.xRot = amount * 1.1F;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        handAll.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
