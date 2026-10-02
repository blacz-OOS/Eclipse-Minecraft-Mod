package com.soldierskull.eclipse.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

/**
 * Modelo da Máscara do Cultista para Minecraft Forge 1.16.5.
 *
 * A máscara utiliza o sistema de cabeça do BipedModel.
 * Dessa forma ela acompanha corretamente:
 *
 * - rotação horizontal da cabeça;
 * - rotação vertical da cabeça;
 * - agachamento;
 * - animações do jogador;
 * - posição da cabeça durante a renderização da armadura.
 */
public class CultistMaskModel extends BipedModel<LivingEntity> {

    private final ModelRenderer mask;

    private final ModelRenderer cube_r1;
    private final ModelRenderer cube_r2;
    private final ModelRenderer cube_r3;
    private final ModelRenderer cube_r4;

    public CultistMaskModel() {
        /*
         * 1.0F = escala padrão da armadura.
         *
         * O BipedModel já cria o "head" na posição correta
         * para a cabeça do jogador.
         */
        super(1.0F);

        this.texWidth = 64;
        this.texHeight = 64;

        /*
         * ============================================================
         * MÁSCARA
         * ============================================================
         *
         * IMPORTANTE:
         *
         * O modelo original do Blockbench utilizava:
         *
         *     setPos(0, 24, 0)
         *
         * porque o modelo exportado utilizava o sistema de
         * coordenadas absoluto do Blockbench.
         *
         * Para uma armadura BipedModel isso não deve ser usado.
         *
         * A cabeça do Minecraft já está centralizada em:
         *
         *     (0, 0, 0)
         *
         * e possui uma caixa que vai aproximadamente de:
         *
         *     Y = -8 até Y = 0
         *
         * Portanto convertemos o modelo para esse sistema.
         */

        mask = new ModelRenderer(this);
        mask.setPos(0.0F, 0.0F, 0.0F);

        /*
         * A máscara é renderizada a partir da posição da cabeça.
         *
         * Não usamos settings/cabeca do modelo original porque eles
         * eram apenas parte do rig exportado pelo Blockbench.
         */

        // Testa / estrutura frontal da máscara
        mask.texOffs(0, 20).addBox(
                -4.0F,
                -8.0F,
                -5.0F,
                8.0F,
                5.0F,
                1.0F,
                0.0F,
                false
        );

        mask.texOffs(0, 26).addBox(
                -3.0F,
                -3.0F,
                -5.0F,
                6.0F,
                2.0F,
                1.0F,
                0.0F,
                false
        );

        mask.texOffs(14, 28).addBox(
                -2.0F,
                -1.0F,
                -5.0F,
                4.0F,
                1.0F,
                1.0F,
                0.0F,
                false
        );

        mask.texOffs(20, 16).addBox(
                4.0F,
                -9.0F,
                -5.0F,
                2.0F,
                2.0F,
                4.0F,
                0.0F,
                false
        );

        mask.texOffs(18, 22).addBox(
                -6.0F,
                -9.0F,
                -5.0F,
                2.0F,
                2.0F,
                4.0F,
                0.0F,
                false
        );

        mask.texOffs(14, 28).addBox(
                -2.0F,
                0.0F,
                -5.0F,
                4.0F,
                1.0F,
                1.0F,
                0.0F,
                false
        );

        /*
         * ============================================================
         * PEÇAS LATERAIS
         * ============================================================
         */

        cube_r1 = new ModelRenderer(this);
        cube_r1.setPos(-7.0F, -3.0F, 0.0F);
        mask.addChild(cube_r1);

        setRotationAngle(
                cube_r1,
                0.4768F,
                -0.0376F,
                0.1705F
        );

        cube_r1.texOffs(0, 10).addBox(
                -1.0F,
                -2.0F,
                -5.0F,
                2.0F,
                2.0F,
                8.0F,
                0.0F,
                false
        );

        cube_r2 = new ModelRenderer(this);
        cube_r2.setPos(-5.0F, -7.0F, -2.0F);
        mask.addChild(cube_r2);

        setRotationAngle(
                cube_r2,
                -0.8249F,
                -0.274F,
                0.2849F
        );

        cube_r2.texOffs(20, 8).addBox(
                -1.0F,
                -2.0F,
                -1.0F,
                2.0F,
                2.0F,
                6.0F,
                0.0F,
                false
        );

        cube_r3 = new ModelRenderer(this);
        cube_r3.setPos(7.0F, -3.0F, 0.0F);
        mask.addChild(cube_r3);

        setRotationAngle(
                cube_r3,
                0.4768F,
                0.0376F,
                -0.1705F
        );

        cube_r3.texOffs(0, 0).addBox(
                -1.0F,
                -2.0F,
                -5.0F,
                2.0F,
                2.0F,
                8.0F,
                0.0F,
                false
        );

        cube_r4 = new ModelRenderer(this);
        cube_r4.setPos(5.0F, -7.0F, -2.0F);
        mask.addChild(cube_r4);

        setRotationAngle(
                cube_r4,
                -0.8249F,
                0.274F,
                -0.2849F
        );

        cube_r4.texOffs(20, 0).addBox(
                -1.0F,
                -2.0F,
                -1.0F,
                2.0F,
                2.0F,
                6.0F,
                0.0F,
                false
        );
    }

    @Override
    public void setupAnim(
            LivingEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        /*
         * Não fazemos nada aqui.
         *
         * A rotação da cabeça será copiada do modelo padrão
         * pelo sistema de armadura.
         */
    }

    @Override
    public void renderToBuffer(
            MatrixStack matrixStack,
            IVertexBuilder buffer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        /*
         * O modelo padrão possui uma cabeça própria.
         *
         * Copiamos a transformação dela para a máscara.
         *
         * Isso evita aplicar a rotação duas vezes.
         */

        mask.xRot = this.head.xRot;
        mask.yRot = this.head.yRot;
        mask.zRot = this.head.zRot;

        mask.x = this.head.x;
        mask.y = this.head.y;
        mask.z = this.head.z;

        /*
         * Renderiza SOMENTE a máscara.
         *
         * Não renderizamos this.head porque ele contém a caixa
         * padrão de cabeça do BipedModel.
         */

        mask.render(
                matrixStack,
                buffer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );
    }

    private void setRotationAngle(
            ModelRenderer modelRenderer,
            float x,
            float y,
            float z
    ) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}