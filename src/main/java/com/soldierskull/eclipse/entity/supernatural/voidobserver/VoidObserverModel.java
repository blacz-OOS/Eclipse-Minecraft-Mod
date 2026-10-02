package com.soldierskull.eclipse.entity.supernatural.voidobserver;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Vazio Observador (model_void_observer.java). Nenhum
 * identificador ilegal aqui (nomes ja eram "bone", "bone2" etc, todos
 * validos), so precisei entender a arvore antes de animar:
 *
 * all (o "olho" - unica geometria do corpo principal, sem filhos)
 * tentacles (grupo separado, raiz propria)
 *  |- bone  -> cube_r1 (curvatura estatica)
 *  |- bone2 -> cube_r2, cube_r3 (curvatura estatica)
 *  |- bone3 (com rotacao PROPRIA -0.0436 - essa e uma pose inicial
 *  |         esculpida do proprio bone3, nao um cube_r; preservada)
 *  |    -> cube_r4 (curvatura estatica)
 *  |- bone4 -> cube_r5 (curvatura estatica)
 *  |- bone5 (geometria propria direto nele - tentaculo reto)
 *  |- bone6 (geometria propria direto nele - tentaculo reto)
 *
 * Os cube_rN sao a curvatura ESCULPIDA de cada tentaculo (como o
 * cube_r1/cube_r2 da Mao Abissal) - a animacao mexe nos bones pais
 * (bone..bone6), nunca nos cube_rN.
 */
public class VoidObserverModel extends EntityModel<VoidObserverEntity> {

    private final ModelRenderer all; // o olho
    private final ModelRenderer tentacles;
    private final ModelRenderer bone;
    private final ModelRenderer cubeR1;
    private final ModelRenderer bone2;
    private final ModelRenderer cubeR2;
    private final ModelRenderer cubeR3;
    private final ModelRenderer bone3;
    private final ModelRenderer cubeR4;
    private final ModelRenderer bone4;
    private final ModelRenderer cubeR5;
    private final ModelRenderer bone5;
    private final ModelRenderer bone6;

    public VoidObserverModel() {
        texWidth = 64;
        texHeight = 64;

        all = new ModelRenderer(this);
        all.setPos(0.0F, 24.0F, 0.0F);
        all.texOffs(0, 0).addBox(-4.0F, -17.0F, -5.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        tentacles = new ModelRenderer(this);
        tentacles.setPos(0.0F, 24.0F, 0.0F);

        bone = new ModelRenderer(this);
        bone.setPos(0.0F, 0.0F, 0.0F);
        tentacles.addChild(bone);

        cubeR1 = new ModelRenderer(this);
        cubeR1.setPos(3.0F, -15.0F, 4.0F);
        bone.addChild(cubeR1);
        setRotationAngle(cubeR1, 0.0F, 0.0F, -0.6109F);
        cubeR1.texOffs(0, 16).addBox(3.8165F, -1.5896F, -11.0F, 2.0F, 2.0F, 12.0F, 0.0F, false);
        cubeR1.texOffs(28, 55).addBox(-0.7881F, -1.672F, -1.0F, 5.0F, 2.0F, 2.0F, 0.0F, false);

        bone2 = new ModelRenderer(this);
        bone2.setPos(0.0F, 0.0F, 0.0F);
        tentacles.addChild(bone2);

        cubeR2 = new ModelRenderer(this);
        cubeR2.setPos(-6.0F, -17.0F, 4.0F);
        bone2.addChild(cubeR2);
        setRotationAngle(cubeR2, 0.0F, 0.0F, 0.6109F);
        cubeR2.texOffs(28, 16).addBox(-2.0F, -2.0F, -11.0F, 2.0F, 2.0F, 12.0F, 0.0F, false);

        cubeR3 = new ModelRenderer(this);
        cubeR3.setPos(-3.0F, -15.0F, 4.0F);
        bone2.addChild(cubeR3);
        setRotationAngle(cubeR3, 0.0F, 0.0F, 0.6109F);
        cubeR3.texOffs(28, 51).addBox(-4.0F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, 0.0F, false);

        bone3 = new ModelRenderer(this);
        bone3.setPos(0.0F, 0.0F, 0.0F);
        tentacles.addChild(bone3);
        setRotationAngle(bone3, 0.0F, 0.0F, -0.0436F);

        cubeR4 = new ModelRenderer(this);
        cubeR4.setPos(-2.0F, -11.0F, 4.0F);
        bone3.addChild(cubeR4);
        setRotationAngle(cubeR4, 0.0F, 0.0F, -0.829F);
        cubeR4.texOffs(42, 51).addBox(-4.8192F, -1.4264F, -1.0F, 5.0F, 2.0F, 2.0F, 0.0F, false);
        cubeR4.texOffs(0, 30).addBox(-6.4238F, -1.344F, -11.0F, 2.0F, 2.0F, 12.0F, 0.0F, false);

        bone4 = new ModelRenderer(this);
        bone4.setPos(0.0F, 0.0F, 0.0F);
        tentacles.addChild(bone4);

        cubeR5 = new ModelRenderer(this);
        cubeR5.setPos(2.0F, -10.0F, 4.0F);
        bone4.addChild(cubeR5);
        setRotationAngle(cubeR5, 0.0F, 0.0F, 0.7418F);
        cubeR5.texOffs(42, 55).addBox(-0.7881F, -1.672F, -1.0F, 5.0F, 2.0F, 2.0F, 0.0F, false);
        cubeR5.texOffs(28, 30).addBox(3.8165F, -1.5896F, -11.0F, 2.0F, 2.0F, 12.0F, 0.0F, false);

        bone5 = new ModelRenderer(this);
        bone5.setPos(0.0F, 0.0F, 0.0F);
        tentacles.addChild(bone5);
        bone5.texOffs(32, 0).addBox(1.0F, -14.0F, 3.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);
        bone5.texOffs(32, 7).addBox(1.0F, -14.0F, 7.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);
        bone5.texOffs(0, 44).addBox(1.0F, -14.0F, 11.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);
        bone5.texOffs(14, 44).addBox(1.0F, -14.0F, 15.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);
        bone5.texOffs(28, 44).addBox(1.0F, -14.0F, 19.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);

        bone6 = new ModelRenderer(this);
        bone6.setPos(0.0F, 0.0F, 0.0F);
        tentacles.addChild(bone6);
        bone6.texOffs(14, 51).addBox(-3.0F, -14.0F, 19.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);
        bone6.texOffs(0, 51).addBox(-3.0F, -14.0F, 15.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);
        bone6.texOffs(46, 7).addBox(-3.0F, -14.0F, 11.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);
        bone6.texOffs(46, 0).addBox(-3.0F, -14.0F, 7.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);
        bone6.texOffs(42, 44).addBox(-3.0F, -14.0F, 3.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(VoidObserverEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;

        if (entity.isWindingUp()) {
            animateWindup(entity.getWindupProgress(), t);
        } else if (entity.isLockPulseActive()) {
            animateLockPulse();
            animateFloatBase(t, 0.3F);
        } else if (entity.isTargetLocked()) {
            animateFloatBase(t, 0.5F);
        } else {
            animateIdleFloat(t);
        }
    }

    private void resetPose() {
        all.y = 24.0F;
        tentacles.y = 24.0F;
        for (ModelRenderer b : new ModelRenderer[]{bone, bone2, bone3, bone4, bone5, bone6}) {
            b.xRot = 0.0F;
            b.zRot = 0.0F;
        }
        bone3.zRot = -0.0436F; // pose esculpida original
    }

    /** 5.1/5.2 IDLE/MOVEMENT - flutuacao suave vertical, tentaculos flutuam independentes. */
    private void animateIdleFloat(float t) {
        all.y = 24.0F + MathHelper.sin(t * 1.0F) * 1.0F;
        tentacles.y = all.y;

        float[] phases = {0.0F, 1.0F, 2.0F, 3.0F, 4.0F, 5.0F};
        ModelRenderer[] bones = {bone, bone2, bone3, bone4, bone5, bone6};
        for (int i = 0; i < bones.length; i++) {
            bones[i].zRot += MathHelper.sin(t * 1.3F + phases[i]) * 0.06F;
            bones[i].xRot += MathHelper.cos(t * 1.1F + phases[i]) * 0.04F;
        }
    }

    private void animateFloatBase(float t, float amplitude) {
        all.y = 24.0F + MathHelper.sin(t * 1.0F) * amplitude;
        tentacles.y = all.y;
    }

    /** 5.3 TARGET DETECTION - pupila trava, tentaculos ficam ligeiramente rigidos. */
    private void animateLockPulse() {
        for (ModelRenderer b : new ModelRenderer[]{bone, bone2, bone3, bone4, bone5, bone6}) {
            b.zRot *= 0.3F; // reduz o balanco - "rigido"
        }
    }

    /** 5.5 PROJECTILE ATTACK (preparacao) - tentaculos ficam rigidos, pequena pausa antes do tiro. */
    private void animateWindup(float progress, float t) {
        // congela o balanco gradualmente conforme progride
        float rigidity = 1.0F - progress;
        ModelRenderer[] bones = {bone, bone2, bone3, bone4, bone5, bone6};
        float[] phases = {0.0F, 1.0F, 2.0F, 3.0F, 4.0F, 5.0F};
        for (int i = 0; i < bones.length; i++) {
            bones[i].zRot += MathHelper.sin(t * 1.3F + phases[i]) * 0.06F * rigidity;
        }
        all.y = 24.0F + (1.0F - progress) * 0.5F; // sobe levemente conforme concentra energia
    }

    /** 5.7 DEATH - pupila perde foco, tentaculos perdem sustentacao, corpo desce. */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 15.0F, 0.0F, 1.0F);
        all.y = 24.0F + progress * 6.0F;
        tentacles.y = all.y;
        for (ModelRenderer b : new ModelRenderer[]{bone, bone2, bone3, bone4, bone5, bone6}) {
            b.xRot = progress * 0.5F; // tentaculos "murcham" pra baixo
        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        all.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        tentacles.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
