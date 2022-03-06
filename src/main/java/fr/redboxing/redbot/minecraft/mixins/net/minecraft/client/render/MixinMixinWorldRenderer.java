package fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinMixinWorldRenderer {
    @Inject(method = "scheduleChunkRender", at = @At("HEAD"), cancellable = true)
    private void scheduleChunkRender(int x, int y, int z, boolean important, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "scheduleBlockRerenderIfNeeded", at = @At("HEAD"), cancellable = true)
    private void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "processWorldEvent", at = @At("HEAD"), cancellable = true)
    private void processWorldEvent(PlayerEntity source, int eventId, BlockPos pos, int data, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "processGlobalEvent", at = @At("HEAD"), cancellable = true)
    private void processGlobalEvent(int eventId, BlockPos pos, int i, CallbackInfo ci) {
        ci.cancel();
    }
}
