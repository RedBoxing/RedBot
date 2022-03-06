package fr.redboxing.redbot.minecraft.mixins.adris.altoclef;

import adris.altoclef.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Settings.class, remap = false)
public interface IMixinSettings {
    @Accessor("idleWhenNotActive")
    void setIdleWhenNotActive(boolean idleWhenNotActive);
}
