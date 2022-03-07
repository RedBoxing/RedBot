package baritone.bot;

import baritone.bot.handler.BotNetHandlerPlayClient;
import baritone.bot.spec.BotWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class BotWorldProvider {
    /**
     * Generic world settings for a typical survival world.
     */
    private static final ClientWorld.Properties GENERIC_WORLD_SETTINGS = new ClientWorld.Properties(Difficulty.EASY, false, false);

    /**
     * All of the dimensions mapped to their respective worlds.
     */
    private final Map<RegistryKey<World>, BotWorld> worlds = new HashMap<>();

    /**
     * Gets or creates the {@link BotWorld} for the specified dimension
     *
     * @return The world
     */
    public BotWorld getWorld(RegistryKey<World> registryKey, DimensionType dimensionType, ClientWorld.Properties properties, BotNetHandlerPlayClient networkHandler) {
        return worlds.computeIfAbsent(registryKey, (key) -> this.createWorldForDim(key, dimensionType, properties, networkHandler));
    }

    /**
     * Creates a new {@link BotWorld} for the given dimension id.
     *
     * @return The new world
     */
    private BotWorld createWorldForDim(RegistryKey<World> registryKey, DimensionType dimensionType, ClientWorld.Properties properties, BotNetHandlerPlayClient networkHandler) {
        return new BotWorld(networkHandler, properties, registryKey, dimensionType, 8, 8, () -> MinecraftClient.getInstance().getProfiler(), false, 0);
    }

    public void tick() {
        this.worlds.forEach((dim, world) -> world.tickEntities());
    }
}