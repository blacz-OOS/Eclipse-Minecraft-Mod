package com.soldierskull.eclipse.entity.supernatural.sulfurhound;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (SulfurHoundModel) - textura em textures/entity/hound_of_sulfur.png (64x64) - o nome do arquivo difere do nome da entidade. */
public class SulfurHoundRenderer extends MobRenderer<SulfurHoundEntity, SulfurHoundModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/hound_of_sulfur.png");

    public SulfurHoundRenderer(EntityRendererManager manager) {
        super(manager, new SulfurHoundModel(), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(SulfurHoundEntity entity) {
        return TEXTURE;
    }
}
