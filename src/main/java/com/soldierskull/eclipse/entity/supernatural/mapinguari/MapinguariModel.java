package com.soldierskull.eclipse.entity.supernatural.mapinguari;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Mapinguari (model_mapinguari.java). CORRIGIDO: "braço
 * esquerdo", "braço direito", "perna direita", "perna esquerda"
 * (nomes com espaço - ilegal em Java) renomeados.
 *
 * AVISO IMPORTANTE (item 12.7 da spec de animacao): a spec pede uma
 * "boca abdominal" como peca INDEPENDENTE (com estados fechada/parcial/
 * aberta/max), mas o arquivo exportado do Blockbench NAO TEM essa
 * geometria - so ha tronco, cabeça, braços e pernas. Nao inventei essa
 * caixa: se voce quiser essa animacao, precisa modelar a boca abdominal
 * no Blockbench e reexportar - aí eu ligo a animacao a ela. Por
 * enquanto, o restante da spec (idle pesado, andar/correr com peso,
 * ataque, pisada, grito, morte) foi implementado normalmente.
 *
 * Bracos/pernas foram exportados como pecas RIGIDAS (cube_r com
 * curvatura estatica, igual ao Ash Ghoul/Dried Corpse) - animo os
 * containers (armLeft/armRight/legRight/legLeft) inteiros a partir do
 * ombro/quadril, nao os cube_r internos.
 */
public class MapinguariModel extends EntityModel<MapinguariEntity> {

    private final ModelRenderer all;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer armLeft;  // era "braço esquerdo"
    private final ModelRenderer cubeR1;
    private final ModelRenderer cubeR2;
    private final ModelRenderer armRight; // era "braço direito"
    private final ModelRenderer cubeR3;
    private final ModelRenderer cubeR4;
    private final ModelRenderer legRight; // era "perna direita"
    private final ModelRenderer pe2;
    private final ModelRenderer cubeR5;
    private final ModelRenderer cubeR6;
    private final ModelRenderer legLeft;  // era "perna esquerda"
    private final ModelRenderer pe1;
    private final ModelRenderer cubeR7;
    private final ModelRenderer cubeR8;

    public MapinguariModel() {
        texWidth = 256;
        texHeight = 256;

        all = new ModelRenderer(this);
        all.setPos(0.0F, 24.0F, 0.0F);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 0.0F, 0.0F);
        all.addChild(body);
        body.texOffs(0, 0).addBox(-10.0F, -52.0F, -1.0F, 20.0F, 32.0F, 12.0F, 0.0F, false);
        body.texOffs(30, 74).addBox(-4.0F, -21.0F, -3.0F, 8.0F, 3.0F, 3.0F, 0.0F, false);
        body.texOffs(102, 34).addBox(-6.0F, -23.0F, -4.0F, 12.0F, 2.0F, 3.0F, 0.0F, false);
        body.texOffs(64, 34).addBox(-8.0F, -25.0F, -4.0F, 16.0F, 2.0F, 3.0F, 0.0F, false);
        body.texOffs(112, 67).addBox(-6.0F, -35.0F, -4.0F, 1.0F, 10.0F, 3.0F, 0.0F, false);
        body.texOffs(28, 113).addBox(5.0F, -35.0F, -4.0F, 1.0F, 10.0F, 3.0F, 0.0F, false);
        body.texOffs(94, 22).addBox(4.0F, -35.0F, -4.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
        body.texOffs(112, 80).addBox(-5.0F, -35.0F, -4.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
        body.texOffs(110, 113).addBox(-5.0F, -26.0F, -4.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
        body.texOffs(114, 96).addBox(4.0F, -26.0F, -4.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
        body.texOffs(36, 113).addBox(-8.0F, -35.0F, -3.0F, 2.0F, 10.0F, 2.0F, 0.0F, false);
        body.texOffs(94, 113).addBox(6.0F, -35.0F, -3.0F, 2.0F, 10.0F, 2.0F, 0.0F, false);
        body.texOffs(102, 113).addBox(-9.0F, -35.0F, -2.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
        body.texOffs(106, 113).addBox(8.0F, -35.0F, -2.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
        body.texOffs(64, 39).addBox(-8.0F, -37.0F, -4.0F, 16.0F, 2.0F, 3.0F, 0.0F, false);
        body.texOffs(94, 18).addBox(-7.0F, -39.0F, -3.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
        body.texOffs(104, 52).addBox(-5.0F, -41.0F, -2.0F, 10.0F, 2.0F, 1.0F, 0.0F, false);
        body.texOffs(114, 100).addBox(-3.0F, -27.0F, -5.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        body.texOffs(70, 117).addBox(-3.0F, -35.0F, -5.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        body.texOffs(114, 104).addBox(1.0F, -27.0F, -5.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        body.texOffs(110, 117).addBox(1.0F, -35.0F, -5.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        body.texOffs(114, 108).addBox(4.0F, -29.0F, -5.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        body.texOffs(16, 117).addBox(-6.0F, -29.0F, -5.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        body.texOffs(104, 55).addBox(-6.0F, -31.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        body.texOffs(80, 105).addBox(4.0F, -31.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        body.texOffs(80, 108).addBox(-1.0F, -26.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        body.texOffs(44, 118).addBox(-1.0F, -36.0F, -2.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        body.texOffs(0, 117).addBox(4.0F, -33.0F, -5.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        body.texOffs(8, 117).addBox(-6.0F, -33.0F, -5.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 45-66 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro pescoco, offset inverso nos filhos: repouso igual.
        head = new ModelRenderer(this);
        head.setPos(0.0F, -52.0F, 0.0F);
        body.addChild(head);
        // headGeo desfaz o offset: a geometria mantem as coordenadas do export.
        ModelRenderer headGeo = new ModelRenderer(this);
        headGeo.setPos(0.0F, 52.0F, 0.0F);
        head.addChild(headGeo);
        headGeo.texOffs(0, 44).addBox(-9.0F, -55.0F, -1.0F, 18.0F, 3.0F, 11.0F, 0.0F, false);
        headGeo.texOffs(0, 58).addBox(-8.0F, -61.0F, -1.0F, 16.0F, 6.0F, 10.0F, 0.0F, false);
        headGeo.texOffs(58, 44).addBox(-7.0F, -66.0F, -1.0F, 14.0F, 5.0F, 9.0F, 0.0F, false);
        headGeo.texOffs(104, 28).addBox(-4.0F, -47.0F, -5.0F, 8.0F, 2.0F, 4.0F, 0.0F, false);
        headGeo.texOffs(94, 12).addBox(-6.0F, -49.0F, -5.0F, 12.0F, 2.0F, 4.0F, 0.0F, false);
        headGeo.texOffs(64, 26).addBox(-8.0F, -53.0F, -5.0F, 16.0F, 4.0F, 4.0F, 0.0F, false);
        headGeo.texOffs(104, 22).addBox(-5.0F, -62.0F, -5.0F, 10.0F, 2.0F, 4.0F, 0.0F, false);
        headGeo.texOffs(102, 39).addBox(-5.0F, -64.0F, -4.0F, 10.0F, 2.0F, 3.0F, 0.0F, false);
        headGeo.texOffs(30, 80).addBox(-5.0F, -66.0F, -2.0F, 10.0F, 2.0F, 1.0F, 0.0F, false);
        headGeo.texOffs(46, 105).addBox(-6.0F, -62.0F, -5.0F, 2.0F, 9.0F, 4.0F, 0.0F, false);
        headGeo.texOffs(58, 105).addBox(4.0F, -62.0F, -5.0F, 2.0F, 9.0F, 4.0F, 0.0F, false);
        headGeo.texOffs(70, 105).addBox(6.0F, -62.0F, -3.0F, 2.0F, 9.0F, 3.0F, 0.0F, false);
        headGeo.texOffs(112, 55).addBox(-8.0F, -62.0F, -3.0F, 2.0F, 9.0F, 3.0F, 0.0F, false);
        headGeo.texOffs(104, 44).addBox(-4.0F, -60.0F, -3.0F, 8.0F, 7.0F, 1.0F, 0.0F, false);
        headGeo.texOffs(28, 105).addBox(-4.0F, -60.0F, -2.0F, 8.0F, 7.0F, 1.0F, 0.0F, false);
        headGeo.texOffs(80, 113).addBox(-3.0F, -59.0F, -4.0F, 6.0F, 5.0F, 1.0F, 0.0F, false);

        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 17-51 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro ombro, offset inverso nos filhos: repouso igual.
        armLeft = new ModelRenderer(this);
        armLeft.setPos(11.0F, -48.0F, 2.0F);
        body.addChild(armLeft);
        cubeR1 = new ModelRenderer(this);
        cubeR1.setPos(2.0F, 16.0F, 0.0F);
        armLeft.addChild(cubeR1);
        setRotationAngle(cubeR1, -0.2182F, 0.0F, -0.1309F);
        cubeR1.texOffs(82, 58).addBox(-1.0F, -3.0F, -1.0F, 7.0F, 18.0F, 8.0F, 0.0F, false);
        cubeR2 = new ModelRenderer(this);
        cubeR2.setPos(0.0F, 0.0F, 0.0F);
        armLeft.addChild(cubeR2);
        setRotationAngle(cubeR2, 0.0F, 0.0F, -0.1309F);
        cubeR2.texOffs(52, 58).addBox(-1.0F, -3.0F, -1.0F, 7.0F, 18.0F, 8.0F, 0.0F, false);

        armRight = new ModelRenderer(this);
        armRight.setPos(-11.0F, -49.0F, 2.0F);
        body.addChild(armRight);
        cubeR3 = new ModelRenderer(this);
        cubeR3.setPos(-2.0F, 16.0F, 0.0F);
        armRight.addChild(cubeR3);
        setRotationAngle(cubeR3, -0.2182F, 0.0F, 0.1309F);
        cubeR3.texOffs(0, 74).addBox(-6.0F, -2.0F, -1.0F, 7.0F, 18.0F, 8.0F, 0.0F, false);
        cubeR4 = new ModelRenderer(this);
        cubeR4.setPos(0.0F, 0.0F, 0.0F);
        armRight.addChild(cubeR4);
        setRotationAngle(cubeR4, 0.0F, 0.0F, 0.1309F);
        cubeR4.texOffs(64, 0).addBox(-6.0F, -2.0F, -1.0F, 7.0F, 18.0F, 8.0F, 0.0F, false);

        // BUGFIX (pivo): container criado na raiz (y=0) com a geometria a 24 px
        // de distancia - rotacao virava arco em volta da raiz em vez de giro na
        // junta. Pivo movido pro quadril, offset inverso nos filhos: repouso igual.
        legRight = new ModelRenderer(this);
        legRight.setPos(0.0F, -24.0F, 0.0F);
        body.addChild(legRight);
        pe2 = new ModelRenderer(this);
        pe2.setPos(-18.0F, 24.0F, 0.0F);
        legRight.addChild(pe2);
        pe2.texOffs(94, 0).addBox(8.0F, -2.0F, -3.0F, 7.0F, 2.0F, 10.0F, 0.0F, false);
        cubeR5 = new ModelRenderer(this);
        cubeR5.setPos(11.0F, -12.0F, -1.0F);
        pe2.addChild(cubeR5);
        setRotationAngle(cubeR5, -0.2618F, 0.0F, 0.0F);
        cubeR5.texOffs(58, 84).addBox(-3.0F, -12.0F, 0.0F, 7.0F, 14.0F, 7.0F, 0.0F, false);
        cubeR6 = new ModelRenderer(this);
        cubeR6.setPos(11.0F, -1.0F, 0.0F);
        pe2.addChild(cubeR6);
        setRotationAngle(cubeR6, 0.0873F, 0.0F, 0.0F);
        cubeR6.texOffs(0, 100).addBox(-3.0F, -10.0F, 0.0F, 7.0F, 10.0F, 7.0F, 0.0F, false);

        legLeft = new ModelRenderer(this);
        legLeft.setPos(0.0F, -24.0F, 0.0F);
        body.addChild(legLeft);
        pe1 = new ModelRenderer(this);
        pe1.setPos(-5.0F, 24.0F, 0.0F);
        legLeft.addChild(pe1);
        pe1.texOffs(86, 84).addBox(8.0F, -2.0F, -3.0F, 7.0F, 2.0F, 10.0F, 0.0F, false);
        cubeR7 = new ModelRenderer(this);
        cubeR7.setPos(11.0F, -12.0F, -1.0F);
        pe1.addChild(cubeR7);
        setRotationAngle(cubeR7, -0.2618F, 0.0F, 0.0F);
        cubeR7.texOffs(30, 84).addBox(-3.0F, -12.0F, 0.0F, 7.0F, 14.0F, 7.0F, 0.0F, false);
        cubeR8 = new ModelRenderer(this);
        cubeR8.setPos(11.0F, -1.0F, 0.0F);
        pe1.addChild(cubeR8);
        setRotationAngle(cubeR8, 0.0873F, 0.0F, 0.0F);
        cubeR8.texOffs(86, 96).addBox(-3.0F, -10.0F, 0.0F, 7.0F, 10.0F, 7.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(MapinguariEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;
        head.yRot = netHeadYaw * 0.017453292F;
        head.xRot = headPitch * 0.017453292F;

        int stompWindup = entity.getStompWindupTicks();
        if (stompWindup > 0) {
            animateStompWindup(stompWindup);
        } else if (entity.isRoaring()) {
            animateRoar(entity.getRoarProgress());
        } else if (limbSwingAmount > 0.02F) {
            animateWalk(limbSwing, limbSwingAmount);
        } else {
            animateIdle(t);
        }

        // 12.4 BASIC ATTACK
        float attackAnim = entity.getAttackAnim(1.0F);
        if (attackAnim > 0.0F && stompWindup <= 0) {
            armRight.xRot -= attackAnim * 1.0F;
            body.yRot = -attackAnim * 0.15F; // tronco gira no golpe
        }
    }

    private void resetPose() {
        // BUGFIX: animateDeath() seta all.zRot (queda de lado) e a instancia
        // do modelo e compartilhada entre todas as entidades do tipo - sem
        // este reset, todo Mapinguari vivo renderiza tombado depois que um
        // deles morre.
        all.zRot = 0.0F;
        body.xRot = 0.0F;
        body.yRot = 0.0F;
        body.y = 0.0F;
        head.xRot = 0.0F;
        head.yRot = 0.0F;
        armLeft.xRot = 0.0F;
        armRight.xRot = 0.0F;
        legRight.xRot = 0.0F;
        legLeft.xRot = 0.0F;
    }

    /** 12.1 IDLE - respiracao profunda e lenta, peso alterna entre pernas. */
    private void animateIdle(float t) {
        body.y = MathHelper.sin(t * 0.7F) * 0.6F; // torax sobe/desce
        head.xRot += MathHelper.sin(t * 0.4F) * 0.05F;
        armLeft.xRot = MathHelper.sin(t * 0.5F) * 0.03F;
        armRight.xRot = MathHelper.sin(t * 0.5F + 1.0F) * 0.03F;
        legRight.xRot = MathHelper.sin(t * 0.3F) * 0.02F;
        legLeft.xRot = -MathHelper.sin(t * 0.3F) * 0.02F;
    }

    /** 12.2/12.3 WALK/RUN - cada passo transmite peso, ombros balançam. */
    private void animateWalk(float limbSwing, float limbSwingAmount) {
        legRight.xRot = MathHelper.cos(limbSwing * 0.5F) * 0.8F * limbSwingAmount;
        legLeft.xRot = MathHelper.cos(limbSwing * 0.5F + (float) Math.PI) * 0.8F * limbSwingAmount;
        armRight.xRot = MathHelper.cos(limbSwing * 0.5F + (float) Math.PI) * 0.5F * limbSwingAmount;
        armLeft.xRot = MathHelper.cos(limbSwing * 0.5F) * 0.5F * limbSwingAmount;
        body.y = Math.abs(MathHelper.sin(limbSwing * 0.5F)) * 1.2F * limbSwingAmount; // corpo sobe/desce a cada pisada
        body.xRot = 0.05F + Math.min(0.15F, limbSwingAmount * 0.2F); // inclina mais correndo
    }

    /** 12.5 STOMP - levanta a perna, pequena pausa, pisada violenta. windup conta de 14 a 0. */
    private void animateStompWindup(int windupTicks) {
        float progress = 1.0F - (windupTicks / 14.0F); // 0 no inicio, 1 no impacto
        legRight.xRot = -0.9F * (1.0F - progress); // levanta a perna
        body.xRot = -0.15F * (1.0F - progress);     // corpo inclina pra tras
        if (progress > 0.85F) {
            // momento do impacto - compressao subita
            body.y = 2.0F;
            legRight.xRot = 0.1F;
        }
    }

    /** 12.6 SCREAM - cabeça sobe, tronco inclina pra tras, braços abrem, corpo vibra. */
    private void animateRoar(float progress) {
        head.xRot = -0.3F * Math.min(1.0F, progress * 2.0F);
        body.xRot = -0.2F * Math.min(1.0F, progress * 2.0F);
        armLeft.xRot = -0.6F * Math.min(1.0F, progress * 2.0F);
        armRight.xRot = -0.6F * Math.min(1.0F, progress * 2.0F);

        float vibrate = (float) (Math.random() - 0.5) * 0.08F;
        body.yRot = vibrate;
    }

    /** 12.9 DEATH - perde equilibrio, ajoelha, tenta se levantar, falha, cai de lado. */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 40.0F, 0.0F, 1.0F); // 2s
        body.xRot = progress * 1.0F;
        legRight.xRot = progress * 1.2F; // ajoelha
        legLeft.xRot = progress * 1.2F;
        armLeft.xRot = -progress * 0.6F;
        armRight.xRot = -progress * 0.6F;
        if (progress > 0.5F) {
            float fall = (progress - 0.5F) * 2.0F;
            all.zRot = fall * 1.4F; // cai de lado
            body.y = fall * 6.0F;
        }
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
