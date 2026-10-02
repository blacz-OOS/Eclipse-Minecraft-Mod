package com.soldierskull.eclipse.entity.supernatural.bloodworm;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (BloodWormModel) - textura ainda precisa ser criada em textures/entity/blood_worm.png (64x64, conforme texWidth/texHeight do modelo). */
public class BloodWormRenderer extends MobRenderer<BloodWormEntity, BloodWormModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/blood_worm.png");

    public BloodWormRenderer(EntityRendererManager manager) {
        super(manager, new BloodWormModel(), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(BloodWormEntity entity) {
        return TEXTURE;
    }
}
