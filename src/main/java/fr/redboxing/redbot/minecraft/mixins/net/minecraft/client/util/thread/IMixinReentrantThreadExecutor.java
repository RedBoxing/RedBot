package fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.util.thread;

import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ReentrantThreadExecutor.class)
public interface IMixinReentrantThreadExecutor {
    @Invoker("hasRunningTasks")
    boolean invokeHasRunningTasks();
}
