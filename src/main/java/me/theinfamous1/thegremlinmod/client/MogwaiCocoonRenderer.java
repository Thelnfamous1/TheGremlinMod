package me.theinfamous1.thegremlinmod.client;

import me.theinfamous1.thegremlinmod.common.entity.MogwaiCocoon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MogwaiCocoonRenderer extends GeoEntityRenderer<MogwaiCocoon> {
    public MogwaiCocoonRenderer(EntityRendererProvider.Context context, ResourceLocation assetSubpath) {
        super(context, new MogwaiCocoonModel(assetSubpath));
    }
}
