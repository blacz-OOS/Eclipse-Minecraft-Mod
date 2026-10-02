package com.soldierskull.eclipse.entity.supernatural.wendigo;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (WendigoModel) - textura ainda precisa ser criada em textures/entity/wendigo.png (128x128). */
public class WendigoRenderer extends MobRenderer<WendigoEntity, WendigoModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/wendigo.png");

    public WendigoRenderer(EntityRendererManager manager) {
        super(manager, new WendigoModel(), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(WendigoEntity entity) {
        return TEXTURE;
    }
}
