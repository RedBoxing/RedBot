package baritone.bot;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.bot.IBaritoneUser;
import baritone.api.event.events.WorldEvent;
import baritone.api.event.events.type.EventState;
import baritone.api.utils.IPlayerController;
import baritone.behavior.InventoryBehavior;
import baritone.behavior.LookBehavior;
import baritone.behavior.MemoryBehavior;
import baritone.behavior.PathingBehavior;
import baritone.bot.spec.BotEntity;
import baritone.bot.spec.BotMinecraft;
import baritone.bot.spec.BotWorld;
import baritone.cache.WorldProvider;
import baritone.command.manager.CommandManager;
import baritone.event.GameEventHandler;
import baritone.process.*;
import baritone.selection.SelectionManager;
import baritone.utils.InputOverrideHandler;
import baritone.utils.PathingControlManager;
import baritone.utils.player.WrappedPlayerController;
import com.mojang.authlib.GameProfile;
import fr.redboxing.redbot.minecraft.mixins.baritone.IMixinBaritone;
import fr.redboxing.redbot.utils.ObjectAllocator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;

public class BaritoneUser implements IBaritoneUser {
    private final UserManager manager;
    private final ClientConnection connection;
    private final Session session;
    private final Baritone baritone;

    private GameProfile profile;
    private ClientPlayNetworkHandler netHandlerPlayClient;
    private BotMinecraft mc;
    private BotWorld world;
    private BotEntity player;
    private IPlayerController playerController;

    BaritoneUser(UserManager manager, ClientConnection connection, Session session, ServerInfo serverData) {
        this.session = session;
        this.mc = BotMinecraft.allocate(this);
        this.mc.setCurrentServerEntry(serverData);
        this.manager = manager;
        this.connection = connection;
        this.profile = session.getProfile();
        this.baritone = allocateBaritone(new BotPlayerContext(this));
    }

    private Baritone allocateBaritone(BotPlayerContext playerContext) {
        Baritone baritone = ObjectAllocator.allocate(Baritone.class);
        IMixinBaritone accessor = (IMixinBaritone) baritone;

        accessor.setPlayerContext(playerContext);
        accessor.setGameEventHandler(new GameEventHandler(baritone));
        accessor.setPathingBehavior(new PathingBehavior(baritone));
        accessor.setLookBehavior(new LookBehavior(baritone));
        accessor.setMemoryBehavior(new MemoryBehavior(baritone));
        accessor.setInventoryBehavior(new InventoryBehavior(baritone));
        accessor.setInputOverrideHandler(new InputOverrideHandler(baritone));
        accessor.setPathingControlManager(new PathingControlManager(baritone));

        accessor.setFollowProcess(new FollowProcess(baritone));
        baritone.getPathingControlManager().registerProcess(baritone.getFollowProcess());

        accessor.setMineProcess(new MineProcess(baritone));
        baritone.getPathingControlManager().registerProcess(baritone.getMineProcess());

        accessor.setCustomGoalProcess(new CustomGoalProcess(baritone));
        baritone.getPathingControlManager().registerProcess(baritone.getCustomGoalProcess());

        accessor.setGetToBlockProcess(new GetToBlockProcess(baritone));
        baritone.getPathingControlManager().registerProcess(baritone.getGetToBlockProcess());

        accessor.setBuilderProcess(new BuilderProcess(baritone));
        baritone.getPathingControlManager().registerProcess(baritone.getBuilderProcess());

        accessor.setExploreProcess(new ExploreProcess(baritone));
        baritone.getPathingControlManager().registerProcess(baritone.getExploreProcess());

        accessor.setBackfillProcess(new BackfillProcess(baritone));
        baritone.getPathingControlManager().registerProcess(accessor.getBackfillProcess());

        accessor.setFarmProcess(new FarmProcess(baritone));
        baritone.getPathingControlManager().registerProcess(baritone.getFarmProcess());

        accessor.setWorldProvider(new WorldProvider());
        accessor.setSelectionManager(new SelectionManager(baritone));
        accessor.setCommandManager(new CommandManager(baritone));

        return baritone;
    }

    public void onLoginSuccess(GameProfile profile, ClientPlayNetworkHandler netHandlerPlayClient) {
        this.profile = profile;
        this.netHandlerPlayClient = netHandlerPlayClient;
    }

    public void onWorldLoad(BotWorld world, BotEntity player, ClientPlayerInteractionManager controller) {
        this.baritone.getGameEventHandler().onWorldEvent(new WorldEvent(world, EventState.PRE));

        this.mc.player = this.player = player;
        this.mc.world = this.world = world;
        this.playerController = new WrappedPlayerController(this.mc.interactionManager = controller);

        this.baritone.getGameEventHandler().onWorldEvent(new WorldEvent(world, EventState.POST));
    }

    @Override
    public ClientConnection getClientConnection() {
        return this.connection;
    }

    @Override
    public ClientPlayNetworkHandler getNetworkHandler() {
        return this.netHandlerPlayClient;
    }

    @Override
    public BotEntity getPlayer() {
        return this.player;
    }

    @Override
    public ClientWorld getWorld() {
        return this.world;
    }

    @Override
    public IPlayerController getPlayerController() {
        return this.playerController;
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public GameProfile getProfile() {
        return this.profile;
    }

    @Override
    public UserManager getManager() {
        return this.manager;
    }

    @Override
    public IBaritone getBaritone() {
        return baritone;
    }

    public BotMinecraft getMinecraft() {
        return this.mc;
    }
}
