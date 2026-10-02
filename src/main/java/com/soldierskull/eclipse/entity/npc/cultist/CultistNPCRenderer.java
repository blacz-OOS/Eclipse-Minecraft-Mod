package com.soldierskull.eclipse.entity.npc.cultist;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (CultistNPCModel, com mascara) - textura em textures/entity/cultist_npc.png (64x64). */
public class CultistNPCRenderer extends MobRenderer<CultistNPCEntity, CultistNPCModel<CultistNPCEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/cultist_npc.png");

    public CultistNPCRenderer(EntityRendererManager manager) {
        super(manager, new CultistNPCModel<>(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(CultistNPCEntity entity) {
        return TEXTURE;
    }
}
