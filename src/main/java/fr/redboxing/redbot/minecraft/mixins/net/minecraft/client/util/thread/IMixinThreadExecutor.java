package fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.util.thread;

import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Queue;

@Mixin(ThreadExecutor.class)
public interface IMixinThreadExecutor {
    @Invoker("cancelTasks")
    void invokeCancelTasks();

    @Invoker("runTasks")
    void invokeRunTasks();

    @Invoker("waitForTasks")
    void invokeWaitForTasks();

    @Mutable
    @Accessor("name")
    void setName(String name);

    @Mutable
    @Accessor("tasks")
    <R extends Runnable> void setTasks(Queue<R> tasks);
}