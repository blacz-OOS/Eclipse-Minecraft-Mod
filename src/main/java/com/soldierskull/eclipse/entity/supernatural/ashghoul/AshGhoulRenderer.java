package com.soldierskull.eclipse.entity.supernatural.ashghoul;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (AshGhoulModel) - textura ainda precisa ser criada em textures/entity/ash_ghoul.png (64x64). */
public class AshGhoulRenderer extends MobRenderer<AshGhoulEntity, AshGhoulModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/ash_ghoul.png");

    public AshGhoulRenderer(EntityRendererManager manager) {
        super(manager, new AshGhoulModel(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(AshGhoulEntity entity) {
        return TEXTURE;
    }
}
