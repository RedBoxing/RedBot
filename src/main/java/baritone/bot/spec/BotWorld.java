package baritone.bot.spec;

import baritone.bot.handler.BotNetHandlerPlayClient;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.world.IMixinClientWorld;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BotWorld extends ClientWorld {
    private final Map<ChunkPos, IntSet> loadedChunksMap;

    public BotWorld(ClientPlayNetworkHandler networkHandler, Properties properties, RegistryKey<World> registryRef, DimensionType dimensionType, int loadDistance, int simulationDistance, Supplier<Profiler> profiler, boolean debugWorld, long seed) {
        super(networkHandler, properties, registryRef, dimensionType, loadDistance, simulationDistance, profiler, null, debugWorld, seed);
        this.loadedChunksMap = new HashMap<>();
    }

    private boolean hasBlock(BlockState state, BlockPos pos) {
        return !state.isAir() && state.isSolidBlock(this, pos);
    }

    @Override
    public void onBlockChanged(BlockPos pos, BlockState oldBlock, BlockState newBlock) {
        if(!this.hasBlock(oldBlock, pos) && this.hasBlock(newBlock, pos)) {
            DiscordBot.getInstance().getMinecraftManager().getBot(((BotNetHandlerPlayClient) ((IMixinClientWorld) this).getNetHandler()).getUser().getPlayer()).ifPresent(bot -> bot.getAltoClef().getControllerExtras().onBlockPlaced(pos, newBlock));
        }

        super.onBlockChanged(pos, oldBlock, newBlock);
    }

    @Override
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

    }

    @Override
    public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {

    }

    @Override
    public void scheduleBlockRenders(int x, int y, int z) {

    }

    @Override
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
    }

    @Override
    public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
    }

    @Override
    public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
    }

    @Override
    public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable NbtCompound nbt) {
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    @Override
    public void addParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    @Override
    public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    @Override
    public void addImportantParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    /**
     * @param bot    The bot requesting the chunk
     * @param chunkX The chunk X position
     * @param chunkZ The chunk Z position
     * @param load   {@code true} if the chunk is being loaded, {@code false} if the chunk is being unloaded.
     * @return Whether or not the chunk needs to be loaded or unloaded accordingly.
     */
    public boolean handlePreChunk(BotEntity bot, int chunkX, int chunkZ, boolean load) {
        IntSet bots = this.loadedChunksMap.computeIfAbsent(new ChunkPos(chunkX, chunkZ), $ -> new IntArraySet());
        if (load) {
            boolean wasEmpty = bots.isEmpty();
            bots.add(bot.getId());
            return wasEmpty;
        } else {
            bots.remove(bot.getId());
            return bots.isEmpty();
        }
    }

    public void handleWorldRemove(BotEntity bot) {
        // Remove Bot from world
        this.removeEntity(bot.getId(), Entity.RemovalReason.DISCARDED);
        //this.entitiesById.removeObject(bot.getId());

        // Unload all chunks that are no longer loaded by the removed Bot
        this.loadedChunksMap.entrySet().stream()
                .peek(entry -> entry.getValue().remove(bot.getId()))
                .filter(entry -> entry.getValue().isEmpty())
                .forEach(entry -> this.getChunkManager().unload(entry.getKey().x, entry.getKey().z));
    }
}