package com.soldierskull.eclipse.entity.supernatural.ghost;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;

public class GhostEyesLayer extends AbstractEyesLayer<GhostEntity, GhostModel> {
    private static final RenderType RENDER_TYPE = RenderType.eyes(new ResourceLocation("eclipse", "textures/entity/ghost_eyes.png"));

    public GhostEyesLayer(IEntityRenderer<GhostEntity, GhostModel> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return RENDER_TYPE;
    }
}
