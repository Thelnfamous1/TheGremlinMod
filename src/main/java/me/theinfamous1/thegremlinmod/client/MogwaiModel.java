package me.theinfamous1.thegremlinmod.client;

import me.theinfamous1.thegremlinmod.common.entity.Mogwai;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MogwaiModel extends DefaultedEntityGeoModel<Mogwai> {
    public MogwaiModel(ResourceLocation assetSubpath) {
        super(assetSubpath, true);
    }
}
