package me.theinfamous1.thegremlinmod.datagen;

import me.theinfamous1.thegremlinmod.TheGremlinMod;
import me.theinfamous1.thegremlinmod.common.util.TGMTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, TheGremlinMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(TGMTags.VULNERABLE_TO_SUNBEAM).add(TheGremlinMod.GREMLIN.get());
        this.tag(TGMTags.GREMLIN_FRIENDS).add(TheGremlinMod.GREMLIN.get());
    }
}
