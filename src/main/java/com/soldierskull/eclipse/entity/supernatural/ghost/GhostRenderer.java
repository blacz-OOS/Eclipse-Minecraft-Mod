package com.soldierskull.eclipse.entity.supernatural.ghost;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class GhostRenderer extends MobRenderer<GhostEntity, GhostModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/ghost.png");

    public GhostRenderer(EntityRendererManager renderManager) {
        super(renderManager, new GhostModel(), 0.5F); // Removidos os argumentos 0.0F do GhostModel
        this.addLayer(new GhostEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(GhostEntity entity) {
        return TEXTURE;
    }
}