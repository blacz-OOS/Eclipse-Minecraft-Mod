package com.soldierskull.eclipse.entity.supernatural.corruptedsaci;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (CorruptedSaciModel) - textura ainda precisa ser criada em textures/entity/corrupted_saci.png (64x64). */
public class CorruptedSaciRenderer extends MobRenderer<CorruptedSaciEntity, CorruptedSaciModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/corrupted_saci.png");

    public CorruptedSaciRenderer(EntityRendererManager manager) {
        super(manager, new CorruptedSaciModel(), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(CorruptedSaciEntity entity) {
        return TEXTURE;
    }
}
