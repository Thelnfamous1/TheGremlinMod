package me.theinfamous1.thegremlinmod.client;

import me.theinfamous1.thegremlinmod.common.entity.Mogwai;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MogwaiRenderer extends GeoEntityRenderer<Mogwai> {
    public MogwaiRenderer(EntityRendererProvider.Context context, ResourceLocation assetSubpath) {
        super(context, new MogwaiModel(assetSubpath));
    }
}
