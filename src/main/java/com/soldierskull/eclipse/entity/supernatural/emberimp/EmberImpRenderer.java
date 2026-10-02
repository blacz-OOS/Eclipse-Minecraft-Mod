package com.soldierskull.eclipse.entity.supernatural.emberimp;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (EmberImpModel) - textura ainda precisa ser criada em textures/entity/ember_imp.png (64x64). */
public class EmberImpRenderer extends MobRenderer<EmberImpEntity, EmberImpModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/ember_imp.png");

    public EmberImpRenderer(EntityRendererManager manager) {
        super(manager, new EmberImpModel(), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(EmberImpEntity entity) {
        return TEXTURE;
    }
}
