package fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.world;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ClientWorld.class)
public interface IMixinClientWorld {
    @Invoker("getMapStates")
    Map<String, MapState> getMapStates();

    @Invoker("putMapStates")
    void putMapStates(Map<String, MapState> mapStates);

    @Accessor("networkHandler")
    ClientPlayNetworkHandler getNetHandler();
}

