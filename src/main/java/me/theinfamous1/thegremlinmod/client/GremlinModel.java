package me.theinfamous1.thegremlinmod.client;

import me.theinfamous1.thegremlinmod.common.entity.Gremlin;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GremlinModel extends DefaultedEntityGeoModel<Gremlin> {
    public GremlinModel(ResourceLocation assetSubpath) {
        super(assetSubpath, true);
    }
}
