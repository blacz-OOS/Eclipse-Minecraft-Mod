package com.soldierskull.eclipse.entity.supernatural.voidobserver;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (VoidObserverModel) - textura ainda precisa ser criada em textures/entity/void_observer.png (64x64). */
public class VoidObserverRenderer extends MobRenderer<VoidObserverEntity, VoidObserverModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/void_observer.png");

    public VoidObserverRenderer(EntityRendererManager manager) {
        super(manager, new VoidObserverModel(), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(VoidObserverEntity entity) {
        return TEXTURE;
    }
}
