package com.soldierskull.eclipse.entity.supernatural.wendigo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * Modelo real do Wendigo (wendigo.java). CORRIGIDO: "perna direita",
 * "perna esquerda", "braço direito", "braço esquerdo" (nomes com
 * espaço - ilegal em Java) renomeados.
 *
 * Pernas e braços foram exportados como pecas RIGIDAS (varios cube_r
 * de curvatura estatica, todos irmãos sem encadeamento pai->filho
 * sequencial) - mesmo padrao ja visto no Dried Corpse/Mapinguari.
 * Animo os containers inteiros (legRight/legLeft/armRight/armLeft,
 * cabeca) a partir do quadril/ombro/pescoco.
 *
 * NAO ANIMADO (fora do escopo combinado): a mandibula do estagio 5
 * (spec 13.5) nao tem bone proprio nesse export - "cube_r53" (o
 * focinho/mandibula) e uma unica caixa com rotacao estatica baked,
 * igual aos chifres. Se quiser a mandibula abrindo, precisa separar
 * essa peca no Blockbench.
 */
public class WendigoModel extends EntityModel<WendigoEntity> {

    private final ModelRenderer tudo;
    private final ModelRenderer corpo;
    private final ModelRenderer legRight; // era "perna direita"
    private final ModelRenderer legLeft;  // era "perna esquerda"
    private final ModelRenderer armRight; // era "braço direito"
    private final ModelRenderer armLeft;  // era "braço esquerdo"
    private final ModelRenderer cabeca;

    public WendigoModel() {
        texWidth = 128;
        texHeight = 128;

        tudo = new ModelRenderer(this);
        tudo.setPos(0.0F, 24.0F, 0.0F);

        corpo = new ModelRenderer(this);
        corpo.setPos(0.0F, 0.0F, 0.0F);
        tudo.addChild(corpo);
        corpo.texOffs(60, 41).addBox(-2.0F, -16.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        corpo.texOffs(60, 43).addBox(-2.0F, -20.0F, -5.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        corpo.texOffs(48, 31).addBox(-2.0F, -18.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        corpo.texOffs(10, 49).addBox(1.0F, -18.0F, -4.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        corpo.texOffs(56, 51).addBox(1.0F, -20.0F, -5.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);
        corpo.texOffs(58, 13).addBox(1.0F, -16.0F, -4.0F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        addStatic(corpo, 0.0F, -15.0F, 2.0F, 0.1309F, 0.0F, 0.0F, 0, 58, -1, -2, -1, 2, 2, 2);
        addStatic(corpo, 0.0F, -18.0F, 2.0F, 0.1309F, 0.0F, 0.0F, 54, 57, -1, -2, -1, 2, 2, 2);
        addStatic(corpo, 0.0F, -21.0F, 2.0F, 0.1309F, 0.0F, 0.0F, 46, 57, -1, -2, -1, 2, 2, 2);
        addStatic(corpo, 0.0F, -24.0F, 2.0F, 0.1309F, 0.0F, 0.0F, 56, 47, -1, -2, -1, 2, 2, 2);

        ModelRenderer cubeR5 = new ModelRenderer(this);
        cubeR5.setPos(2.0F, -17.0F, -2.0F);
        corpo.addChild(cubeR5);
        setRotationAngle(cubeR5, 0.0F, 0.2618F, 0.0F);
        cubeR5.texOffs(50, 15).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        cubeR5.texOffs(58, 5).addBox(0.0F, 1.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        addStatic(corpo, 2.0F, -19.0F, -3.0F, 0.0F, 0.2618F, 0.0F, 34, 51, 0, -1, -1, 1, 1, 2);
        addStatic(corpo, -3.0F, -19.0F, -2.0F, 0.0F, -0.2182F, 0.0F, 52, 39, 0, -1, -3, 1, 1, 3);

        ModelRenderer cubeR8 = new ModelRenderer(this);
        cubeR8.setPos(-3.0F, -15.0F, -1.0F);
        corpo.addChild(cubeR8);
        setRotationAngle(cubeR8, 0.0F, -0.2182F, 0.0F);
        cubeR8.texOffs(52, 35).addBox(0.0F, -1.0F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
        cubeR8.texOffs(52, 31).addBox(0.0F, -3.0F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);

        addStatic(corpo, 0.0F, -19.0F, -2.0F, 0.1309F, 0.0F, 0.0F, 0, 0, -4, -7, -1, 8, 7, 5);
        addStatic(corpo, -2.0F, -10.0F, 6.0F, 0.5672F, 0.0F, 0.0F, 58, 0, 1, -6, -1, 2, 4, 1);
        addStatic(corpo, 0.0F, -13.0F, 0.0F, 0.1309F, 0.0F, 0.0F, 0, 23, -3, -7, -1, 6, 7, 4);

        // ---------------- PERNA DIREITA ----------------
        // BUGFIX (pivo): este container era criado em y=0 (nivel do chao/quadril
        // raiz) enquanto a geometria dele fica a 15 px de distancia, entao
        // qualquer rotacao fazia a peca descrever um arco gigante em volta da
        // raiz em vez de girar na junta. O javadoc da classe ja dizia que a
        // intencao era animar "a partir do quadril". Pivo movido pra junta e o
        // offset inverso aplicado nos filhos - a pose de repouso e identica.
        legRight = new ModelRenderer(this);
        legRight.setPos(1.0F, -15.0F, 0.0F);
        corpo.addChild(legRight);

        ModelRenderer pd1 = new ModelRenderer(this);
        pd1.setPos(0.0F, 15.0F, 0.0F);
        legRight.addChild(pd1);
        addStatic(pd1, -4.0F, -1.0F, 2.0F, -0.3491F, 0.0F, 0.0F, 46, 48, 0, -1, -1, 1, 1, 4);
        addStatic(pd1, -3.0F, 0.0F, 0.0F, 0.3491F, -0.3927F, 0.0F, 10, 51, 0, -1, -1, 1, 1, 3);
        addStatic(pd1, -5.0F, 0.0F, 0.0F, 0.3491F, 0.3054F, 0.0F, 50, 11, 0, -1, -1, 1, 1, 3);

        ModelRenderer pd2 = new ModelRenderer(this);
        pd2.setPos(0.0F, 15.0F, 0.0F);
        legRight.addChild(pd2);
        addStatic(pd2, -4.0F, -1.0F, 2.0F, 0.5711F, 0.1103F, 0.0706F, 44, 6, 0, -9, -1, 1, 10, 2);

        ModelRenderer pd3 = new ModelRenderer(this);
        pd3.setPos(0.0F, 15.0F, 0.0F);
        legRight.addChild(pd3);
        addStatic(pd3, -3.0F, -8.0F, -3.0F, -0.5672F, 0.0F, 0.0F, 16, 41, -2, -7, -1, 3, 7, 3);

        // ---------------- PERNA ESQUERDA ----------------
        legLeft = new ModelRenderer(this);
        legLeft.setPos(6.0F, -15.0F, 0.0F);
        corpo.addChild(legLeft);

        ModelRenderer pe1 = new ModelRenderer(this);
        pe1.setPos(0.0F, 15.0F, 0.0F);
        legLeft.addChild(pe1);
        addStatic(pe1, -4.0F, -1.0F, 2.0F, -0.3491F, 0.0F, 0.0F, 0, 49, 0, -1, -1, 1, 1, 4);
        addStatic(pe1, -3.0F, 0.0F, 0.0F, 0.3491F, -0.3927F, 0.0F, 26, 51, 0, -1, -1, 1, 1, 3);
        addStatic(pe1, -5.0F, 0.0F, 0.0F, 0.3491F, 0.3054F, 0.0F, 18, 51, 0, -1, -1, 1, 1, 3);

        ModelRenderer pe2 = new ModelRenderer(this);
        pe2.setPos(0.0F, 15.0F, 0.0F);
        legLeft.addChild(pe2);
        addStatic(pe2, -4.0F, -1.0F, 2.0F, 0.5711F, -0.1103F, -0.0706F, 40, 48, 0, -9, -1, 1, 10, 2);

        ModelRenderer pe3 = new ModelRenderer(this);
        pe3.setPos(0.0F, 15.0F, 0.0F);
        legLeft.addChild(pe3);
        addStatic(pe3, -3.0F, -8.0F, -3.0F, -0.5672F, 0.0F, 0.0F, 28, 41, -2, -7, -1, 3, 7, 3);

        // ---------------- BRAÇO DIREITO ----------------
        // BUGFIX (pivo): este container era criado em y=0 (nivel do chao/quadril
        // raiz) enquanto a geometria dele fica a 16-25 px de distancia, entao
        // qualquer rotacao fazia a peca descrever um arco gigante em volta da
        // raiz em vez de girar na junta. O javadoc da classe ja dizia que a
        // intencao era animar "a partir do ombro". Pivo movido pra junta e o
        // offset inverso aplicado nos filhos - a pose de repouso e identica.
        armRight = new ModelRenderer(this);
        armRight.setPos(0.0F, -24.0F, 0.0F);
        corpo.addChild(armRight);

        ModelRenderer bd1 = new ModelRenderer(this);
        bd1.setPos(0.0F, 24.0F, 0.0F);
        armRight.addChild(bd1);
        addStatic(bd1, -5.0F, -24.0F, -1.0F, 0.6545F, 0.0F, 0.0F, 0, 12, -1, -1, -8, 2, 2, 9);

        ModelRenderer bd2 = new ModelRenderer(this);
        bd2.setPos(0.0F, 24.0F, 0.0F);
        armRight.addChild(bd2);
        bd2.texOffs(46, 53).addBox(-6.0F, -21.0F, -14.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        bd2.texOffs(54, 53).addBox(-5.0F, -17.0F, -17.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
        addStatic(bd2, -5.0F, -16.0F, -14.0F, -0.48F, 0.0F, 0.0F, 58, 8, 0, -4, -1, 1, 4, 1);
        addStatic(bd2, -8.0F, -19.0F, -17.0F, 0.6545F, 0.3927F, 0.0F, 8, 55, 0, -1, -1, 1, 1, 3);
        addStatic(bd2, -7.0F, -20.0F, -15.0F, 0.0F, 0.3927F, 0.0F, 0, 54, 0, -1, -1, 1, 1, 3);
        ModelRenderer cubeR26 = new ModelRenderer(this);
        cubeR26.setPos(-3.0F, -18.0F, -7.0F);
        bd2.addChild(cubeR26);
        setRotationAngle(cubeR26, -0.1309F, 0.0F, 0.0F);
        cubeR26.texOffs(0, 34).addBox(-1.0F, -2.0F, -6.0F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        cubeR26.texOffs(20, 33).addBox(-4.0F, -2.0F, -6.0F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        addStatic(bd2, -3.0F, -19.0F, -17.0F, 0.6545F, -0.3927F, 0.0F, 16, 55, 0, -1, -1, 1, 1, 3);
        addStatic(bd2, -4.0F, -20.0F, -15.0F, 0.0F, -0.4363F, 0.0F, 52, 43, 0, -1, -1, 1, 1, 3);

        // ---------------- BRAÇO ESQUERDO ----------------
        armLeft = new ModelRenderer(this);
        armLeft.setPos(10.0F, -24.0F, 0.0F);
        corpo.addChild(armLeft);

        ModelRenderer be1 = new ModelRenderer(this);
        be1.setPos(0.0F, 24.0F, 0.0F);
        armLeft.addChild(be1);
        addStatic(be1, -5.0F, -24.0F, -1.0F, 0.6545F, 0.0F, 0.0F, 22, 12, -1, -1, -8, 2, 2, 9);

        ModelRenderer be2 = new ModelRenderer(this);
        be2.setPos(0.0F, 24.0F, 0.0F);
        armLeft.addChild(be2);
        be2.texOffs(56, 15).addBox(-6.0F, -21.0F, -14.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        be2.texOffs(56, 27).addBox(-5.0F, -17.0F, -17.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
        addStatic(be2, -5.0F, -16.0F, -14.0F, -0.48F, 0.0F, 0.0F, 8, 59, 0, -4, -1, 1, 4, 1);
        addStatic(be2, -8.0F, -19.0F, -17.0F, 0.6545F, 0.3927F, 0.0F, 56, 23, 0, -1, -1, 1, 1, 3);
        addStatic(be2, -7.0F, -20.0F, -15.0F, 0.0F, 0.3927F, 0.0F, 56, 19, 0, -1, -1, 1, 1, 3);
        ModelRenderer cubeR33 = new ModelRenderer(this);
        cubeR33.setPos(-3.0F, -18.0F, -7.0F);
        be2.addChild(cubeR33);
        setRotationAngle(cubeR33, -0.1309F, 0.0F, 0.0F);
        cubeR33.texOffs(40, 23).addBox(-1.0F, -2.0F, -6.0F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        cubeR33.texOffs(36, 33).addBox(-4.0F, -2.0F, -6.0F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        addStatic(be2, -3.0F, -19.0F, -17.0F, 0.6545F, -0.3927F, 0.0F, 32, 55, 0, -1, -1, 1, 1, 3);
        addStatic(be2, -4.0F, -20.0F, -15.0F, 0.0F, -0.4363F, 0.0F, 24, 55, 0, -1, -1, 1, 1, 3);

        // ---------------- CABEÇA + CHIFRES ----------------
        // BUGFIX (pivo): este container era criado em y=0 (nivel do chao/quadril
        // raiz) enquanto a geometria dele fica a 26-40 px de distancia, entao
        // qualquer rotacao fazia a peca descrever um arco gigante em volta da
        // raiz em vez de girar na junta. O javadoc da classe ja dizia que a
        // intencao era animar "a partir do pescoco". Pivo movido pra junta e o
        // offset inverso aplicado nos filhos - a pose de repouso e identica.
        cabeca = new ModelRenderer(this);
        cabeca.setPos(0.0F, -26.0F, 0.0F);
        corpo.addChild(cabeca);
        // Container interno que desfaz o offset: toda a geometria da cabeca
        // (chifres, cranio, mandibula) mantem exatamente as coordenadas do
        // export do Blockbench, so o PIVO de rotacao mudou pro pescoco.
        ModelRenderer cabecaGeo = new ModelRenderer(this);
        cabecaGeo.setPos(0.0F, 26.0F, 0.0F);
        cabeca.addChild(cabecaGeo);

        ModelRenderer cubeR36 = new ModelRenderer(this);
        cubeR36.setPos(4.0F, -28.0F, -4.0F);
        cabecaGeo.addChild(cubeR36);
        setRotationAngle(cubeR36, 0.7418F, 0.0F, 0.0F);
        cubeR36.texOffs(26, 0).addBox(-6.0F, 0.0F, -5.0F, 4.0F, 0.0F, 6.0F, 0.0F, false);
        cubeR36.texOffs(0, 42).addBox(-2.0F, -1.0F, -5.0F, 0.0F, 1.0F, 6.0F, 0.0F, false);
        cubeR36.texOffs(40, 31).addBox(-6.0F, -1.0F, -5.0F, 4.0F, 1.0F, 0.0F, 0.0F, false);
        cubeR36.texOffs(48, 61).addBox(-2.0F, -2.0F, -3.0F, 0.0F, 2.0F, 1.0F, 0.0F, false);
        cubeR36.texOffs(46, 61).addBox(-6.0F, -2.0F, -3.0F, 0.0F, 2.0F, 1.0F, 0.0F, false);
        cubeR36.texOffs(44, 60).addBox(-6.0F, -2.0F, -5.0F, 0.0F, 2.0F, 1.0F, 0.0F, false);
        cubeR36.texOffs(40, 41).addBox(-6.0F, -1.0F, -5.0F, 0.0F, 1.0F, 6.0F, 0.0F, false);

        addStatic(cabecaGeo, 2.0F, -30.0F, -6.0F, 1.0036F, 0.0F, 0.0F, 60, 61, -3, -1, -5, 1, 2, 0);
        addStatic(cabecaGeo, 4.0F, -27.0F, -5.0F, 0.7418F, 0.0F, 0.0F, 50, 61, -2, -2, -3, 0, 2, 1);

        ModelRenderer cubeR39 = new ModelRenderer(this);
        cubeR39.setPos(0.0F, -29.0F, -5.0F);
        cabecaGeo.addChild(cubeR39);
        setRotationAngle(cubeR39, 0.1309F, 0.0F, 0.0F);
        cubeR39.texOffs(2, 62).addBox(1.0F, -1.0F, -5.0F, 1.0F, 3.0F, 0.0F, 0.0F, false);
        cubeR39.texOffs(0, 62).addBox(-2.0F, -1.0F, -5.0F, 1.0F, 3.0F, 0.0F, 0.0F, false);
        cubeR39.texOffs(58, 61).addBox(-2.0F, -1.0F, -4.0F, 0.0F, 2.0F, 1.0F, 0.0F, false);
        cubeR39.texOffs(56, 61).addBox(-2.0F, -1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, false);
        cubeR39.texOffs(54, 61).addBox(2.0F, -1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, false);
        cubeR39.texOffs(52, 61).addBox(2.0F, -1.0F, -4.0F, 0.0F, 2.0F, 1.0F, 0.0F, false);
        cubeR39.texOffs(26, 6).addBox(-2.0F, -1.0F, -5.0F, 4.0F, 1.0F, 5.0F, 0.0F, false);

        addStatic(cabecaGeo, 4.0F, -40.0F, 0.0F, -0.1135F, 0.0653F, -2.6217F, 20, 59, 1, -4, -5, 1, 4, 1);
        addStatic(cabecaGeo, 2.0F, -39.0F, 0.0F, -0.1106F, -0.0702F, 2.5782F, 16, 59, 1, -4, -5, 1, 4, 1);
        addStatic(cabecaGeo, 7.0F, -36.0F, -6.0F, 1.693F, 0.3489F, 1.4076F, 28, 59, 1, -4, -5, 1, 4, 1);
        addStatic(cabecaGeo, 7.0F, -37.0F, -4.0F, 1.6865F, -0.1278F, 1.3508F, 24, 59, 1, -4, -5, 1, 4, 1);
        addStatic(cabecaGeo, 2.0F, -36.0F, 1.0F, 0.0285F, -0.1278F, 1.3508F, 12, 59, 1, -4, -5, 1, 4, 1);
        addStatic(cabecaGeo, 0.0F, -33.0F, 1.0F, 0.1231F, 0.0447F, -0.3463F, 12, 42, -2, -5, -5, 1, 5, 1);
        addStatic(cabecaGeo, -2.0F, -36.0F, 1.0F, 0.0285F, 0.1278F, -1.3508F, 32, 59, -2, -4, -5, 1, 4, 1);
        addStatic(cabecaGeo, -2.0F, -39.0F, 0.0F, -0.1106F, 0.0702F, -2.5782F, 36, 59, -2, -4, -5, 1, 4, 1);
        addStatic(cabecaGeo, -4.0F, -40.0F, 0.0F, -0.1135F, -0.0653F, 2.6217F, 60, 31, -2, -4, -5, 1, 4, 1);
        addStatic(cabecaGeo, -7.0F, -37.0F, -4.0F, 1.6865F, 0.1278F, -1.3508F, 60, 36, -2, -4, -5, 1, 4, 1);
        addStatic(cabecaGeo, -7.0F, -36.0F, -6.0F, 1.693F, -0.3489F, -1.4076F, 40, 60, -2, -4, -5, 1, 4, 1);
        addStatic(cabecaGeo, 0.0F, -33.0F, 1.0F, 0.1231F, -0.0447F, 0.3463F, 16, 34, 1, -5, -5, 1, 5, 1);
        addStatic(cabecaGeo, 0.0F, -29.0F, -1.0F, 0.1309F, 0.0F, 0.0F, 20, 23, -2, -4, -5, 4, 4, 6);

        ModelRenderer cubeR53 = new ModelRenderer(this);
        cubeR53.setPos(0.0F, -26.0F, 0.0F);
        cabecaGeo.addChild(cubeR53);
        setRotationAngle(cubeR53, 0.2618F, 0.0F, 0.0F);
        cubeR53.texOffs(46, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        cubeR53.texOffs(44, 18).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);
        cubeR53.texOffs(50, 5).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);
    }

    /** Cria uma peça "cube_r" (curvatura estatica) filha de parent - reduz repeticao no construtor. */
    private void addStatic(ModelRenderer parent, float px, float py, float pz, float rx, float ry, float rz,
                            int u, int v, float bx, float by, float bz, float bw, float bh, float bd) {
        ModelRenderer piece = new ModelRenderer(this);
        piece.setPos(px, py, pz);
        parent.addChild(piece);
        setRotationAngle(piece, rx, ry, rz);
        piece.texOffs(u, v).addBox(bx, by, bz, bw, bh, bd, 0.0F, false);
    }

    @Override
    public void setupAnim(WendigoEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetPose();

        int deathTime = entity.getDeathTime();
        if (deathTime > 0) {
            animateDeath(deathTime);
            return;
        }

        float t = ageInTicks / 20.0F;
        int stage = entity.getStage();
        cabeca.yRot = netHeadYaw * 0.017453292F;
        cabeca.xRot = headPitch * 0.017453292F;

        if (limbSwingAmount > 0.02F) {
            animateRun(limbSwing, limbSwingAmount, stage);
        } else {
            animateIdle(t, stage);
        }

        float attackAnim = entity.getAttackAnim(1.0F);
        if (attackAnim > 0.0F) {
            armRight.xRot -= attackAnim * 1.2F; // 13.4 - preparacao curta, golpe rapido
        }

        // 13.6 REGENERATION - pequenas contracoes, sem exagero
        if (entity.isRegenPulseActive()) {
            corpo.y = -0.3F;
        }
    }

    private void resetPose() {
        corpo.xRot = 0.0F;
        corpo.y = 0.0F;
        cabeca.xRot = 0.0F;
        cabeca.yRot = 0.0F;
        armRight.xRot = 0.0F;
        armLeft.xRot = 0.0F;
        legRight.xRot = 0.0F;
        legLeft.xRot = 0.0F;
    }

    /** 13.1 IDLE - nunca confortavel: cabeça rapida, pescoço com espasmos, corpo inclina. Intensifica por estagio. */
    private void animateIdle(float t, int stage) {
        float intensity = 1.0F + (stage - 1) * 0.35F; // estagios deixam ate o idle mais inquieto
        cabeca.xRot += MathHelper.sin(t * 4.0F) * 0.08F * intensity;
        cabeca.yRot += MathHelper.sin(t * 3.2F + 1.0F) * 0.15F * intensity;
        corpo.xRot = 0.05F + MathHelper.sin(t * 1.5F) * 0.02F * intensity;
        armLeft.xRot = MathHelper.sin(t * 2.0F) * 0.05F * intensity;
        armRight.xRot = MathHelper.sin(t * 2.0F + 1.0F) * 0.05F * intensity;
    }

    /**
     * 13.3 RUN - nao-humano: tronco inclinado, braços muito baixos, passadas longas.
     * 13.5 RAGE STAGES - a agressividade/amplitude cresce com o estagio (1-5).
     */
    private void animateRun(float limbSwing, float limbSwingAmount, int stage) {
        float aggression = 1.0F + (stage - 1) * 0.25F; // estagio 5 = 2x mais agressivo que estagio 1

        legRight.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.5F * limbSwingAmount;
        legLeft.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.5F * limbSwingAmount;
        armRight.xRot = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.3F * limbSwingAmount * aggression - 0.3F;
        armLeft.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.3F * limbSwingAmount * aggression - 0.3F;
        corpo.xRot = (0.2F + Math.min(0.3F, limbSwingAmount * 0.3F)) * Math.min(1.3F, aggression);

        if (stage >= 4) {
            // estagios 4-5: cabeça se move de forma mais nervosa/predatoria durante a corrida
            armRight.xRot += MathHelper.sin(limbSwing * 1.5F) * 0.1F;
            armLeft.xRot += MathHelper.sin(limbSwing * 1.5F + 1.0F) * 0.1F;
        }
    }

    /** 13.8 DEATH - corre, ataque falha, perde equilibrio, ajoelha, maos tocam chao, cabeça baixa, cai. */
    private void animateDeath(int deathTime) {
        float progress = MathHelper.clamp(deathTime / 16.0F, 0.0F, 1.0F);
        corpo.xRot = progress * 1.3F;
        cabeca.xRot = progress * 0.6F; // cabeça baixa - chifres continuam visiveis
        legRight.xRot = progress * 1.0F;
        legLeft.xRot = progress * 1.0F;
        armRight.xRot = -progress * 0.9F; // maos tocam o chao
        armLeft.xRot = -progress * 0.9F;
        corpo.y = progress * 5.0F;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        tudo.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
