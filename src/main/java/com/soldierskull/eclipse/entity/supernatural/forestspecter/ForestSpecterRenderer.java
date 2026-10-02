package com.soldierskull.eclipse.entity.supernatural.forestspecter;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (ForestSpecterModel) - textura ainda precisa ser criada em textures/entity/forest_specter.png (128x128). */
public class ForestSpecterRenderer extends MobRenderer<ForestSpecterEntity, ForestSpecterModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/forest_specter.png");

    public ForestSpecterRenderer(EntityRendererManager manager) {
        super(manager, new ForestSpecterModel(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ForestSpecterEntity entity) {
        return TEXTURE;
    }
}
