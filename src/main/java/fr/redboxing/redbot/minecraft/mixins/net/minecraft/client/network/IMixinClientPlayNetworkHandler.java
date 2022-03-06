package fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.network;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.tag.TagManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetworkHandler.class)
public interface IMixinClientPlayNetworkHandler {
    @Accessor("world")
    void setWorld(ClientWorld world);

    @Accessor("worldProperties")
    void setWorldProperties(ClientWorld.Properties properties);

    @Accessor("worldProperties")
    ClientWorld.Properties getWorldProperties();

    @Accessor("tagManager")
    void setTagManager(TagManager tagManager);

    @Accessor("chunkLoadDistance")
    int getChunkLoadDistance();

    @Accessor("chunkLoadDistance")
    void setChunkLoadDistance(int chunkLoadDistance);
}

