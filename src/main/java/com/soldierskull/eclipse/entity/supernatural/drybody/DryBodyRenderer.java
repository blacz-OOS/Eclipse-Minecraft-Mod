package com.soldierskull.eclipse.entity.supernatural.drybody;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (DryBodyModel) - textura ainda precisa ser criada em textures/entity/dry_body.png (64x64). */
public class DryBodyRenderer extends MobRenderer<DryBodyEntity, DryBodyModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/dry_body.png");

    public DryBodyRenderer(EntityRendererManager manager) {
        super(manager, new DryBodyModel(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(DryBodyEntity entity) {
        return TEXTURE;
    }
}
