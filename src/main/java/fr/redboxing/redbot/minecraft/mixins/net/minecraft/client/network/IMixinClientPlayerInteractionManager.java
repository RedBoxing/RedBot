package fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.network;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerInteractionManager.class)
public interface IMixinClientPlayerInteractionManager {
    @Accessor("currentBreakingProgress")
    float getCurrentBreakingProgress();
}
