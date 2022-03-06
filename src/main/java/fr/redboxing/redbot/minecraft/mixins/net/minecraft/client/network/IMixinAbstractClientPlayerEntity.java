package fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.network;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractClientPlayerEntity.class)
public interface IMixinAbstractClientPlayerEntity {
    @Accessor("cachedScoreboardEntry")
    PlayerListEntry getCachedScoreboardEntry();

    @Accessor("cachedScoreboardEntry")
    void setCachedScoreboardEntry(PlayerListEntry playerListEntry);
}
