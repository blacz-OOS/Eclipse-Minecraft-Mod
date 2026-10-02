package com.soldierskull.eclipse.entity.supernatural.abyssalhand;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** PLACEHOLDER - textura ainda precisa ser criada em textures/entity/abyssal_hand.png (128x128, conforme texWidth/texHeight do modelo). */
public class AbyssalHandRenderer extends MobRenderer<AbyssalHandEntity, AbyssalHandModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/abyssal_hand.png");

    public AbyssalHandRenderer(EntityRendererManager manager) {
        super(manager, new AbyssalHandModel(), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(AbyssalHandEntity entity) {
        return TEXTURE;
    }
}
