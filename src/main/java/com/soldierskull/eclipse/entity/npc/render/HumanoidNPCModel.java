package com.soldierskull.eclipse.entity.npc.render;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;

/**
 * PLACEHOLDER GEOMETRICO dos NPCs de Caçadores/Cultistas - nenhum modelo
 * Blockbench foi enviado pra eles ainda (diferente dos 13 mobs da Fase 14,
 * que já têm modelo real). Troque por um modelo real do Blockbench do mesmo
 * jeito que fizemos com os mobs da Fase 14.
 *
 * MUDANÇA: antes esta classe estendia EntityModel e recriava, caixa por
 * caixa, exatamente a geometria do biped vanilla (head/body/arms/legs nas
 * mesmas posições e com as mesmas UVs). O problema não era a geometria - era
 * que, sendo um EntityModel "anônimo", ela não implementava IHasArm nem
 * IHasHead, e por isso NENHUM layer de equipamento do Forge/vanilla
 * conseguia se acoplar: item na mão e armadura simplesmente não
 * renderizavam.
 *
 * Agora estende BipedModel, que:
 *   - fornece a mesma geometria (o placeholder era uma cópia manual dela);
 *   - implementa IHasArm.translateToHand(), o que faz o HeldItemLayer
 *     posicionar o item na mão certa, sem atravessar o braço nem flutuar;
 *   - implementa IHasHead, usado pelo HeadLayer (bloco/caveira na cabeça);
 *   - traz a animação de caminhada/ataque/agachar do vanilla, que é o que o
 *     setupAnim manual tentava aproximar.
 *
 * super(0.0F, 0.0F, 64, 64) preserva exatamente o texWidth/texHeight 64x64
 * das texturas atuais (hunter_npc.png e cultist_npc.png). O construtor
 * público BipedModel(float) NÃO serve aqui: ele assume 64x32 e quebraria a
 * UV das duas texturas.
 */
public class HumanoidNPCModel<T extends LivingEntity> extends BipedModel<T> {

    public HumanoidNPCModel() {
        super(0.0F, 0.0F, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        // Pose dos braços ANTES do super: o BipedModel lê rightArmPose/
        // leftArmPose dentro do setupAnim pra decidir como posicionar o braço.
        // Num MobRenderer comum isso nunca é preenchido (quem faz isso é o
        // BipedRenderer), então sem estas duas linhas o NPC seguraria a arma
        // com o braço solto do lado do corpo.
        this.rightArmPose = poseFor(entity, HandSide.RIGHT);
        this.leftArmPose = poseFor(entity, HandSide.LEFT);

        this.crouching = entity.isCrouching();

        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    /** ITEM quando há algo na mão daquele lado, EMPTY quando não há. */
    private ArmPose poseFor(T entity, HandSide side) {
        Hand hand = side == entity.getMainArm() ? Hand.MAIN_HAND : Hand.OFF_HAND;
        ItemStack stack = entity.getItemInHand(hand);
        return stack.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
    }
}
