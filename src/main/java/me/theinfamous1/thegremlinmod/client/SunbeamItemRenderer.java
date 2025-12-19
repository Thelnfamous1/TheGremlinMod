package me.theinfamous1.thegremlinmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.theinfamous1.thegremlinmod.TheGremlinMod;
import me.theinfamous1.thegremlinmod.common.item.SunbeamItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SunbeamItemRenderer extends GeoItemRenderer<SunbeamItem> {
    private static final ResourceLocation ON_TEXTURE = buildFormattedTexturePath(TheGremlinMod.location("sunbeam_on"));

    public SunbeamItemRenderer(SunbeamItem item) {
        super(new DefaultedItemGeoModel<SunbeamItem>(BuiltInRegistries.ITEM.getKey(item)).withAltTexture(TheGremlinMod.location("sunbeam_off")));
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this){

            @Override
            public void render(PoseStack poseStack, SunbeamItem animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                if(SunbeamItem.getSwitchValue(SunbeamItemRenderer.this.getCurrentItemStack())){
                    super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
                }
            }
        });
    }

    private static ResourceLocation buildFormattedTexturePath(ResourceLocation basePath) {
        return basePath.withPath("textures/item/" + basePath.getPath() + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(SunbeamItem animatable) {
        if(SunbeamItem.getSwitchValue(SunbeamItemRenderer.this.getCurrentItemStack())){
            return ON_TEXTURE;
        } else {
            return super.getTextureLocation(animatable);
        }
    }
}
