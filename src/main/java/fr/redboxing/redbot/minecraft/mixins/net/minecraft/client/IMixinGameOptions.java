package fr.redboxing.redbot.minecraft.mixins.net.minecraft.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(GameOptions.class)
public interface IMixinGameOptions {
    @Mutable
    @Accessor("keySprint")
    void setKeySprint(KeyBinding key);

    @Accessor("client")
    void setClient(MinecraftClient client);

    @Mutable
    @Accessor("enabledPlayerModelParts")
    void setEnabledPlayerModelParts(Set<PlayerModelPart> parts);
}

