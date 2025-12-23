package me.theinfamous1.thegremlinmod.client;

import me.theinfamous1.thegremlinmod.common.entity.Gremlin;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GremlinRenderer extends GeoEntityRenderer<Gremlin> {
    public GremlinRenderer(EntityRendererProvider.Context context, ResourceLocation assetSubpath) {
        super(context, new GremlinModel(assetSubpath));
    }
}
