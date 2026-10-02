package com.soldierskull.eclipse.entity.npc.hunter;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (HunterNPCModel, com chapeu) - textura em textures/entity/hunter_npc.png (64x64). */
public class HunterNPCRenderer extends MobRenderer<HunterNPCEntity, HunterNPCModel<HunterNPCEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/hunter_npc.png");

    public HunterNPCRenderer(EntityRendererManager manager) {
        super(manager, new HunterNPCModel<>(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(HunterNPCEntity entity) {
        return TEXTURE;
    }
}
