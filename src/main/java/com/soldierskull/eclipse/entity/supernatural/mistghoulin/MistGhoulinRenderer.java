package com.soldierskull.eclipse.entity.supernatural.mistghoulin;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (MistGhoulinModel) - textura ainda precisa ser criada em textures/entity/mist_ghoulin.png (64x64). */
public class MistGhoulinRenderer extends MobRenderer<MistGhoulinEntity, MistGhoulinModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/mist_ghoulin.png");

    public MistGhoulinRenderer(EntityRendererManager manager) {
        super(manager, new MistGhoulinModel(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MistGhoulinEntity entity) {
        return TEXTURE;
    }
}
