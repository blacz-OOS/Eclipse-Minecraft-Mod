package com.soldierskull.eclipse.entity.supernatural.mapinguari;

import com.soldierskull.eclipse.Eclipse;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

/** Modelo real (MapinguariModel) - textura ainda precisa ser criada em textures/entity/mapinguari.png (256x256). */
public class MapinguariRenderer extends MobRenderer<MapinguariEntity, MapinguariModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Eclipse.MOD_ID, "textures/entity/mapinguari.png");

    public MapinguariRenderer(EntityRendererManager manager) {
        super(manager, new MapinguariModel(), 0.9F);
    }

    @Override
    public ResourceLocation getTextureLocation(MapinguariEntity entity) {
        return TEXTURE;
    }
}
