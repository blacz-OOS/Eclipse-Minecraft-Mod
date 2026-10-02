package com.soldierskull.eclipse.entity.supernatural.aetherparasite;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (AetherParasiteModel) - textura ainda precisa ser criada em textures/entity/aether_parasite.png (64x64). */
public class AetherParasiteRenderer extends MobRenderer<AetherParasiteEntity, AetherParasiteModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/aether_parasite.png");

    public AetherParasiteRenderer(EntityRendererManager manager) {
        super(manager, new AetherParasiteModel(), 0.1F);
    }

    @Override
    public ResourceLocation getTextureLocation(AetherParasiteEntity entity) {
        return TEXTURE;
    }
}
