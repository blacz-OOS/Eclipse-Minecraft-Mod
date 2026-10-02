package com.soldierskull.eclipse.entity.supernatural.forestspecter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Espectro da Floresta (model_forest_specter.java).
 *
 * BUGFIX DE ORIENTACAO: o export vinha na convencao do Blockbench
 * ("+Y para CIMA, pes em Y=0"), que e o INVERSO do espaco de modelo de
 * entidade do Minecraft ("+Y para BAIXO, pes em Y=24"). Renderizado como
 * estava, o espectro aparecia de cabeca pra baixo com a cabeca cerca de
 * 2 blocos abaixo do chao, as pernas para cima e a saia no topo.
 * Correcao: todos os pivos tiveram o Y espelhado, cada caixa foi
 * reposicionada para y = -(y + altura) (a caixa e TRANSLADADA, nao
 * espelhada - cada peca conserva a propria UV) e o "root" foi para
 * Y=24. Nao havia nenhuma rotacao estatica no export, entao nao ha
 * sinal de rotacao a inverter. O codigo de animacao ja estava escrito
 * na convencao correta (xRot positivo = inclina pra frente, head.xRot
 * negativo = cabeca levanta), o que confirma que a geometria era o lado
 * errado da conta.
 *
 * Modelo real do Espectro da Floresta (model_forest_specter.java).
 * Nenhum identificador ilegal aqui - so precisei entender a arvore
 * antes de animar (o "vestido" e "peitos" sao pecas de detalhe, filhas
 * diretas de "root", nao de "body" - entao seguem o balanco geral do
 * corpo por uma fracao da amplitude, simulando o tecido flutuando com
 * atraso, em vez de ficarem rigidamente presas ao tronco).
 *
 * Arvore: root -> waist -> body -> head -> helmet (vazio, sem geometria);
 *                        -> body -> rightArm -> rightItem (vazio)
 *                        -> body -> leftArm -> leftItem (vazio)
 *          root -> rightLeg, leftLeg, peitos, vestido
 */
public class ForestSpecterModel extends EntityModel<ForestSpecterEntity> {

    private final ModelRenderer root;
    private final ModelRenderer waist;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;
    private final ModelRenderer peitos;
    private final ModelRenderer vestido;

    public ForestSpecterModel() {
        texWidth = 128;
        texHeight = 128;

        root = new ModelRenderer(this);
        root.setPos(0.0F, 24.0F, 0.0F);

        waist = new ModelRenderer(this);
        waist.setPos(0.0F, -12.0F, 0.0F);
        root.addChild(waist);

        body = new ModelRenderer(this);
        body.setPos(0.0F, -12.0F, 0.0F);
        waist.addChild(body);
        body.texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

        head = new ModelRenderer(this);
        head.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(head);
        head.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        head.texOffs(28, 58).addBox(-2.0F, -10.0F, 3.0F, 4.0F, 3.0F, 3.0F, 0.0F, false);

        rightArm = new ModelRenderer(this);
        rightArm.setPos(-5.0F, 2.0F, 0.0F);
        body.addChild(rightArm);
        rightArm.texOffs(16, 42).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, 0.0F, false);

        leftArm = new ModelRenderer(this);
        leftArm.setPos(5.0F, 2.0F, 0.0F);
        body.addChild(leftArm);
        leftArm.texOffs(30, 42).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, 0.0F, false);

        rightLeg = new ModelRenderer(this);
        rightLeg.setPos(-1.9F, -12.0F, 0.0F);
        root.addChild(rightLeg);
        rightLeg.texOffs(24, 26).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        leftLeg = new ModelRenderer(this);
        leftLeg.setPos(1.9F, -12.0F, 0.0F);
        root.addChild(leftLeg);
        leftLeg.texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

        peitos = new ModelRenderer(this);
        peitos.setPos(0.0F, 0.0F, 0.0F);
        root.addChild(peitos);
        peitos.texOffs(16, 32).addBox(-3.0F, -22.0F, -4.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        peitos.texOffs(16, 36).addBox(1.0F, -22.0F, -4.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        peitos.texOffs(70, 24).addBox(-1.0F, -22.0F, -3.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);
        peitos.texOffs(16, 40).addBox(-3.0F, -23.0F, -3.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        peitos.texOffs(50, 24).addBox(1.0F, -23.0F, -3.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        peitos.texOffs(66, 52).addBox(-3.0F, -20.0F, -3.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        peitos.texOffs(70, 30).addBox(1.0F, -20.0F, -3.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        peitos.texOffs(24, 65).addBox(-4.0F, -22.0F, -3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        peitos.texOffs(8, 70).addBox(3.0F, -22.0F, -3.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        vestido = new ModelRenderer(this);
        vestido.setPos(0.0F, 0.0F, 0.0F);
        root.addChild(vestido);
        vestido.texOffs(56, 32).addBox(-4.0F, -13.0F, -3.0F, 8.0F, 2.0F, 1.0F, 0.0F, false);
        vestido.texOffs(44, 51).addBox(-5.0F, -11.0F, -4.0F, 10.0F, 2.0F, 1.0F, 0.0F, false);
        vestido.texOffs(24, 16).addBox(-6.0F, -9.0F, -5.0F, 12.0F, 9.0F, 1.0F, 0.0F, false);
        vestido.texOffs(60, 54).addBox(4.0F, -13.0F, -2.0F, 1.0F, 2.0F, 5.0F, 0.0F, false);
        vestido.texOffs(60, 61).addBox(-5.0F, -13.0F, -2.0F, 1.0F, 2.0F, 5.0F, 0.0F, false);
        vestido.texOffs(58, 68).addBox(4.0F, -11.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        vestido.texOffs(66, 68).addBox(-6.0F, -11.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        vestido.texOffs(0, 56).addBox(-6.0F, -11.0F, -1.0F, 1.0F, 2.0F, 6.0F, 0.0F, false);
        vestido.texOffs(56, 24).addBox(5.0F, -11.0F, -1.0F, 1.0F, 2.0F, 6.0F, 0.0F, false);
        vestido.texOffs(42, 62).addBox(5.0F, -9.0F, -4.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);
        vestido.texOffs(50, 62).addBox(-7.0F, -9.0F, -4.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);
        vestido.texOffs(32, 0).addBox(6.0F, -9.0F, -2.0F, 1.0F, 9.0F, 7.0F, 0.0F, false);
        vestido.texOffs(40, 26).addBox(-7.0F, -9.0F, -2.0F, 1.0F, 9.0F, 7.0F, 0.0F, false);
        vestido.texOffs(48, 0).addBox(-5.0F, -13.0F, 2.0F, 10.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(48, 3).addBox(-5.0F, -12.0F, 3.0F, 10.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(48, 9).addBox(-5.0F, -6.0F, 6.0F, 10.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(48, 12).addBox(-5.0F, -5.0F, 7.0F, 10.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(44, 48).addBox(-5.0F, -4.0F, 8.0F, 10.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(50, 15).addBox(-5.0F, -3.0F, 9.0F, 10.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(50, 18).addBox(-5.0F, -2.0F, 10.0F, 10.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(50, 21).addBox(-5.0F, -1.0F, 11.0F, 10.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(48, 6).addBox(-5.0F, -11.0F, 4.0F, 10.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(44, 42).addBox(-5.0F, -10.0F, 5.0F, 10.0F, 4.0F, 2.0F, 0.0F, false);
        vestido.texOffs(0, 48).addBox(4.0F, -1.0F, 4.0F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        vestido.texOffs(56, 35).addBox(4.0F, -2.0F, 4.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);
        vestido.texOffs(0, 64).addBox(4.0F, -3.0F, 4.0F, 1.0F, 1.0F, 5.0F, 0.0F, false);
        vestido.texOffs(68, 42).addBox(4.0F, -4.0F, 4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        vestido.texOffs(0, 70).addBox(4.0F, -5.0F, 4.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
        vestido.texOffs(70, 27).addBox(4.0F, -6.0F, 4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(24, 70).addBox(-5.0F, -5.0F, 4.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
        vestido.texOffs(68, 47).addBox(-5.0F, -4.0F, 4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        vestido.texOffs(28, 64).addBox(-5.0F, -3.0F, 4.0F, 1.0F, 1.0F, 5.0F, 0.0F, false);
        vestido.texOffs(14, 58).addBox(-5.0F, -2.0F, 4.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);
        vestido.texOffs(44, 54).addBox(-5.0F, -1.0F, 4.0F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        vestido.texOffs(32, 70).addBox(-5.0F, -6.0F, 4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        vestido.texOffs(12, 65).addBox(5.0F, -9.0F, 5.0F, 1.0F, 9.0F, 2.0F, 0.0F, false);
        vestido.texOffs(18, 65).addBox(-6.0F, -9.0F, 5.0F, 1.0F, 9.0F, 2.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(ForestSpecterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;
        boolean chasing = entity.getTarget() != null;

        if (entity.isWindingUp()) {
            animateScreamPrep(entity.getWindupProgress());
        } else if (chasing) {
            animateChase(t, netHeadYaw);
        } else {
            animateIdleFloat(t);
        }
    }

    private void resetPose() {
        root.y = 24.0F;
        root.xRot = 0.0F;
        body.xRot = 0.0F;
        head.xRot = 0.0F;
        head.yRot = 0.0F;
        rightArm.xRot = 0.0F;
        rightArm.zRot = 0.0F;
        leftArm.xRot = 0.0F;
        leftArm.zRot = 0.0F;
        vestido.xRot = 0.0F;
        root.zRot = 0.0F;
    }

    /** 11.1/11.2 IDLE/MOVEMENT - flutua suave, braços atras, cabeça levemente inclinada, desliza (nao caminha). */
    private void animateIdleFloat(float t) {
        root.y = 24.0F + MathHelper.sin(t * 0.8F) * 1.5F;
        head.xRot = MathHelper.sin(t * 0.5F) * 0.1F - 0.05F;
        rightArm.zRot = 0.15F + MathHelper.sin(t * 0.6F) * 0.05F; // flutuam pra tras
        leftArm.zRot = -0.15F - MathHelper.sin(t * 0.6F) * 0.05F;
        rightArm.xRot = 0.1F;
        leftArm.xRot = 0.1F;
        // vestido acompanha com atraso/amplitude reduzida - "tecido flutuando"
        vestido.xRot = MathHelper.sin(t * 0.8F - 0.3F) * 0.03F;
    }

    /** 11.3 CHASE - corpo inclina, braços abrem, movimento mais decidido. */
    private void animateChase(float t, float netHeadYaw) {
        root.y = 24.0F + MathHelper.sin(t * 1.4F) * 1.0F;
        root.xRot = 0.15F; // corpo inclina na direcao do movimento
        head.yRot = netHeadYaw * 0.017453292F;
        rightArm.zRot = 0.5F;
        leftArm.zRot = -0.5F;
        vestido.xRot = MathHelper.sin(t * 1.4F - 0.3F) * 0.05F;
    }

    /** 11.4 SCREAM PREPARATION - corpo para, cabeça levanta, braços abrem, corpo vibra. */
    private void animateScreamPrep(float progress) {
        root.xRot = -0.1F * progress;
        head.xRot = -0.3F * progress; // cabeça levanta
        rightArm.zRot = 0.9F * progress;
        leftArm.zRot = -0.9F * progress;

        // vibracao crescente conforme se aproxima do grito
        float vibrate = (float) (Math.random() - 0.5) * 0.06F * progress;
        root.zRot = vibrate;
        body.xRot = vibrate;
    }

    /** 11.7 DEATH - perde forma, sobe levemente, desaparece. */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 20.0F, 0.0F, 1.0F);
        root.y = 24.0F - progress * 8.0F; // sobe (some pra cima)
        body.xRot = progress * 0.5F;
        rightArm.zRot = progress * 1.2F;
        leftArm.zRot = -progress * 1.2F;
        vestido.xRot = progress * 0.3F;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
