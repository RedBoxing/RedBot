package fr.redboxing.redbot.minecraft.mixins.net.minecraft.client;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.File;

@Mixin(MinecraftClient.class)
public interface IMixinMinecraftClient {
    @Mutable
    @Accessor("options")
    void setOptions(GameOptions options);

    @Mutable
    @Accessor("soundManager")
    void setSoundManager(SoundManager soundManager);

    @Invoker("getThread")
    Thread invokeGetThread();

    @Mutable
    @Accessor("mouse")
    void setMouse(Mouse mouse);

    @Mutable
    @Accessor("window")
    void setWindow(Window window);

    @Mutable
    @Accessor("inGameHud")
    void setInGameHud(InGameHud inGameHud);

    @Mutable
    @Accessor("runDirectory")
    void setRunDirectory(File runDirectory);

    @Mutable
    @Accessor("keyboard")
    void setKeyboard(Keyboard keyboard);

    @Mutable
    @Accessor("world")
    void setWorld(ClientWorld world);

    @Mutable
    @Accessor("player")
    void setPlayer(ClientPlayerEntity player);

    @Mutable
    @Accessor("interactionManager")
    void setInteractionManager(ClientPlayerInteractionManager interactionManager);
}
