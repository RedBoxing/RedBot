package fr.redboxing.redbot.minecraft.baritone.bot.handler;

import fr.redboxing.redbot.minecraft.baritone.bot.BaritoneUser;
import fr.redboxing.redbot.minecraft.baritone.bot.spec.BotEntity;
import fr.redboxing.redbot.minecraft.baritone.bot.spec.BotMinecraft;
import fr.redboxing.redbot.minecraft.baritone.bot.spec.BotPlayerController;
import fr.redboxing.redbot.minecraft.baritone.bot.spec.BotWorld;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.minecraft.events.EntityRemoveEvent;
import fr.redboxing.redbot.minecraft.events.GameJoinEvent;
import fr.redboxing.redbot.minecraft.events.RespawnEvent;
import fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.network.IMixinClientPlayNetworkHandler;
import fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.world.IMixinClientWorld;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.TagManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;

import java.util.Map;
import java.util.function.IntConsumer;

public class BotNetHandlerPlayClient extends ClientPlayNetworkHandler {
    private final BotMinecraft client;
    @Getter
    private final BaritoneUser user;
    private BotEntity player;
    private BotWorld world;
    private BotPlayerController playerController;

    public BotNetHandlerPlayClient(ClientConnection connection, BaritoneUser user, BotMinecraft client, GameProfile profile) {
        super(client, null, connection, profile, client.createTelemetrySender());
        this.client = client;
        this.user = user;
        this.user.onLoginSuccess(profile, this);
    }

    @Override
    public void onGameJoin(GameJoinS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.playerController = new BotPlayerController(this.user, this);
        if (!this.getConnection().isLocal()) {
            RequiredTagListRegistry.clearAllTags();
        }

        ((IMixinClientPlayNetworkHandler) this).setChunkLoadDistance(packet.viewDistance());

        ClientWorld.Properties properties = new ClientWorld.Properties(Difficulty.NORMAL, packet.hardcore(), packet.flatWorld());
        ((IMixinClientPlayNetworkHandler) this).setWorldProperties(properties);

        this.world = this.user.getManager().getWorldProvider().getWorld(packet.dimensionId(), packet.dimensionType(), properties, this);
        //this.world = new BotWorld(this, properties, packet.getDimensionId(), packet.getDimensionType(), ((IMixinClientPlayNetworkHandler) this).getChunkLoadDistance(), () -> DummyProfiler.INSTANCE, packet.isDebugWorld(), packet.getSha256Seed());
        ((IMixinClientPlayNetworkHandler) this).setWorld(this.world);

        if (this.player == null) {
            this.player = new BotEntity(this.user, this.client, this.world, this, new StatHandler(), new ClientRecipeBook(), false, false);
            this.player.setYaw(-180.0F);
            if (this.client.getServer() != null) {
                this.client.getServer().setLocalPlayerUuid(this.player.getUuid());
            }
        }

        //this.player = new BotEntity(this.user, this.client, this.world, this, new StatHandler(), new ClientRecipeBook(), false, false);
        this.user.onWorldLoad(this.world, this.player, this.playerController);
        this.player.init();
        this.player.setId(packet.playerEntityId());
        this.world.addPlayer(packet.playerEntityId(), this.player);
        this.playerController.copyAbilities(this.client.player);
        this.player.setShowsDeathScreen(packet.showDeathScreen());
        this.playerController.setGameMode(packet.gameMode());
        this.client.options.sendClientSettings();
        this.getConnection().send(new CustomPayloadC2SPacket(CustomPayloadC2SPacket.BRAND, (new PacketByteBuf(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));

        DiscordBot.getInstance().getMinecraftManager().getBot(this.client.getSession().getProfile().getId()).ifPresent(bot -> bot.getEventBus().post(new GameJoinEvent()));
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);

        if(packet.getDimension() != this.player.world.getRegistryKey()) {
            this.world.handleWorldRemove(this.player);

            ClientWorld.Properties properties = new ClientWorld.Properties(((IMixinClientPlayNetworkHandler) this).getWorldProperties().getDifficulty(), ((IMixinClientPlayNetworkHandler) this).getWorldProperties().isHardcore(), packet.isFlatWorld());
            ((IMixinClientPlayNetworkHandler) this).setWorldProperties(properties);

            Scoreboard scoreboard = this.world.getScoreboard();
            Map<String, MapState> map = ((IMixinClientWorld) this.world).getMapStates();

            this.world = this.user.getManager().getWorldProvider().getWorld(packet.getDimension(), packet.getDimensionType(), properties, this);
            //this.world = new BotWorld(this, properties, packet.getDimension(), packet.getDimensionType(), ((IMixinClientPlayNetworkHandler) this).getChunkLoadDistance(), () -> DummyProfiler.INSTANCE, packet.isDebugWorld(), packet.getSha256Seed());
            this.world.setScoreboard(scoreboard);
            ((IMixinClientWorld) this.world).putMapStates(map);
            ((IMixinClientPlayNetworkHandler) this).setWorld(this.world);
        }

        BotEntity prev = this.player;
        this.player = new BotEntity(this.user, this.client, this.world, this, prev.getStatHandler(), prev.getRecipeBook(), false, false);
        this.user.onWorldLoad(this.world, this.player, this.playerController);
        this.player.getDataTracker().writeUpdatedEntries(prev.getDataTracker().getAllEntries());
        this.player.init();
        this.player.setId(prev.getId());
        this.player.setServerBrand(prev.getServerBrand());
        this.world.addPlayer(prev.getId(), this.player);
        this.playerController.setGameMode(packet.getGameMode());

        DiscordBot.getInstance().getMinecraftManager().getBot(this.client.getSession().getProfile().getId()).ifPresent(bot -> bot.getEventBus().post(new RespawnEvent()));
    }

    @Override
    public void onChunkData(ChunkDataS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);

        if(this.world.handlePreChunk(this.player, packet.getX(), packet.getZ(), true)) {
            ChunkData chunkData = packet.getChunkData();
            this.world.getChunkManager().loadChunkFromPacket(packet.getX(), packet.getZ(), chunkData.getSectionsDataBuf(), chunkData.getHeightmap(), chunkData.getBlockEntities(packet.getX(), packet.getZ()));

            //DiscordBot.getInstance().getMinecraftManager().getBot(this.player).ifPresent(bot -> bot.getAltoClef().onChunkLoad(worldChunk));
        }
    }

    @Override
    public void onUnloadChunk(UnloadChunkS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);

        if(this.world.handlePreChunk(this.player, packet.getX(), packet.getZ(), false)) {
            super.onUnloadChunk(packet);
            //BotManager.getInstance().getBot(this.player).ifPresent(bot -> bot.getAltoClef().onChunkUnload(new ChunkPos(packet.getX(), packet.getZ())));
        }
    }

    @Override
    public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.getConnection().send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
        this.getConnection().send(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
    }

    @Override
    public void onEntityAnimation(EntityAnimationS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getId());
        if (entity != null) {
            LivingEntity livingEntity2;
            if (packet.getAnimationId() == 0) {
                livingEntity2 = (LivingEntity)entity;
                livingEntity2.swingHand(Hand.MAIN_HAND);
            } else if (packet.getAnimationId() == 3) {
                livingEntity2 = (LivingEntity)entity;
                livingEntity2.swingHand(Hand.OFF_HAND);
            } else if (packet.getAnimationId() == 1) {
                entity.animateDamage();
            } else if (packet.getAnimationId() == 2) {
                PlayerEntity playerEntity = (PlayerEntity)entity;
                playerEntity.wakeUp(false, false);
            }
        }
    }

    @Override
    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (entity != null) {
            if (entity instanceof ItemEntity) {
                ItemEntity itemEntity = (ItemEntity)entity;
                ItemStack itemStack = itemEntity.getStack();
                itemStack.decrement(packet.getStackAmount());
                if (itemStack.isEmpty()) {
                    this.world.removeEntity(packet.getEntityId(), Entity.RemovalReason.DISCARDED);
                }
            } else if (!(entity instanceof ExperienceOrbEntity)) {
                this.world.removeEntity(packet.getEntityId(), Entity.RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public void onDeathMessage(DeathMessageS2CPacket packet) {
        /*NetworkThreadUtils.forceMainThread(packet, this, this.client);
        Entity entity = this.world.getEntityById(packet.getEntityId());
        BotManager.getInstance().getBot(this.client.getSession().getProfile().getId()).ifPresent(bot -> bot.getEventBus().post(new DeathMessageEvent(entity)));

        if (entity == this.client.player) {
            BotManager.getInstance().getBot(this.client.getSession().getProfile().getId()).ifPresent(bot -> bot.getEventBus().post(new DeathEvent()));
        }*/
        super.onDeathMessage(packet);
    }

    @Override
    public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.getRecipeManager().setRecipes(packet.getRecipes());
        ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
        clientRecipeBook.reload(this.getRecipeManager().values());
    }

    @Override
    public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        TagManager tagManager = TagManager.fromPacket(this.getRegistryManager(), packet.getGroups());
        Multimap<RegistryKey<? extends Registry<?>>, Identifier> multimap = RequiredTagListRegistry.getMissingTags(tagManager);
        if (!multimap.isEmpty()) {
            //LOGGER.warn((String)"Incomplete server tags, disconnecting. Missing: {}", (Object)multimap);
            this.getConnection().disconnect(new TranslatableText("multiplayer.disconnect.missing_tags"));
        } else {
            ((IMixinClientPlayNetworkHandler) this).setTagManager(tagManager);
            if (!this.getConnection().isLocal()) {
                tagManager.apply();
            }
        }
    }

    @Override
    public void onGameMessage(GameMessageS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        //DiscordBot.getInstance().getMinecraftManager().getBot(this.client.getSession().getProfile().getId()).ifPresent(bot -> bot.getChatManager().onChatMessage(packet.getLocation(), packet.getMessage(), packet.getSender()));
    }

    @Override
    public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {

    }

    @Override
    public void onLightUpdate(LightUpdateS2CPacket packet) {

    }

    @Override
    public void onTitleClear(ClearTitleS2CPacket packet) {

    }

  /*  @Override
    public void sendPacket(Packet<?> packet) {
        SendPacketEvent event = new SendPacketEvent(packet);
        BotManager.getInstance().getBot(this.client.getSession().getProfile().getId()).ifPresent(bot -> bot.getEventBus().post(event));

        if(!event.isCancelled()) {
            super.sendPacket(packet);
        }
    }*/

    @Override
    public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        packet.getEntityIds().forEach((IntConsumer) entityId -> {
            DiscordBot.getInstance().getMinecraftManager().getBot(BotNetHandlerPlayClient.this.client.getSession().getProfile().getId()).ifPresent(bot -> bot.getEventBus().post(new EntityRemoveEvent(entityId)));
            BotNetHandlerPlayClient.this.world.removeEntity(entityId, Entity.RemovalReason.DISCARDED);
        });
    }

    @Override
    public void onDisconnected(Text reason) {
        if(this.player != null) {
            this.world.removeEntity(this.player.getId(), Entity.RemovalReason.DISCARDED);
        }

        this.user.getManager().disconnect(this.user, reason);
    }
}