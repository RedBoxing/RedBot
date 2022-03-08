package fr.redboxing.redbot.minecraft.baritone.bot;

import fr.redboxing.redbot.minecraft.baritone.api.bot.IBaritoneUser;
import fr.redboxing.redbot.minecraft.baritone.api.bot.IUserManager;
import fr.redboxing.redbot.minecraft.baritone.api.bot.connect.IConnectionResult;
import baritone.api.event.events.TickEvent;
import baritone.api.event.events.type.EventState;
import baritone.api.event.listener.AbstractGameEventListener;
import baritone.api.utils.Helper;
import fr.redboxing.redbot.minecraft.baritone.bot.connect.ConnectionResult;
import fr.redboxing.redbot.minecraft.baritone.bot.handler.BotNetHandlerLoginClient;
import fr.redboxing.redbot.minecraft.baritone.bot.spec.BotEntity;
import fr.redboxing.redbot.minecraft.baritone.bot.spec.BotWorld;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.minecraft.baritone.api.bot.connect.ConnectionStatus;
import fr.redboxing.redbot.minecraft.events.ClientTickEvent;
import lombok.Getter;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.Session;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserManager implements IUserManager, AbstractGameEventListener, Helper {
    @Getter
    private static final UserManager instance = new UserManager();

    private final List<IBaritoneUser> users;
    private final BotWorldProvider worldProvider;

    UserManager() {
        //BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().registerEventListener(this);
        this.users = new CopyOnWriteArrayList<>();
        this.worldProvider = new BotWorldProvider();
    }

    @Override
    public void onTick(TickEvent event) {
        if (event.getState() != EventState.PRE) {
            return;
        }

        this.users.forEach(user -> {
            user.getMinecraft().runTasks();

            if (user.getPlayer() != null && user.getPlayerController() != null) {
                user.getPlayerController().syncHeldItem();
            }

            if (user.getClientConnection().isOpen()) {
                user.getClientConnection().tick();
            } else {
                user.getClientConnection().handleDisconnection();
            }

            DiscordBot.getInstance().getMinecraftManager().getBot(user.getSession().getProfile().getId()).ifPresent(bot -> bot.getEventBus().post(new ClientTickEvent()));
        });

        this.worldProvider.tick();
    }

    /**
     * Connects a new user with the specified {@link Session} to the current server.
     *
     * @param session The user session
     * @return The result of the attempted connection
     */
    @Override
    public final IConnectionResult connect(Session session, ServerAddress address, @Nullable ServerInfo serverInfo) {
        ServerInfo data = serverInfo;
        if (data == null) {
            return ConnectionResult.failed(ConnectionStatus.NO_CURRENT_CONNECTION);
        }

        // Connect to the server from the parsed server data
        return connect0(session, address, data);
    }

    /**
     * Connects a new user with the specified {@link Session} to the specified server.
     * <p>
     * Hi Mickey :)
     *
     * @param session The user session
     * @param data The address of the server to connect to
     * @return The result of the attempted connection
     */
    private IConnectionResult connect0(Session session, ServerAddress address, ServerInfo data) {
        System.out.println(String.format("Connecting to %s, %s", address.getAddress(), address.getPort()));

        Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);
        if(optional.isEmpty()) {
            return ConnectionResult.failed(ConnectionStatus.CANT_RESOLVE_HOST);
        }

        InetSocketAddress inetSocketAddress = optional.get();

        try {
            // Initialize Connection
            ClientConnection connection = ClientConnection.connect(inetSocketAddress, true);

            // Create User
            BaritoneUser user = new BaritoneUser(this, connection, session, data);
            this.users.add(user);

            // Setup login handler and send connection packets
            connection.setPacketListener(new BotNetHandlerLoginClient(connection, user));
            connection.send(new HandshakeC2SPacket(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), NetworkState.LOGIN));
            connection.send(new LoginHelloC2SPacket(session.getProfile()));

            return ConnectionResult.success(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ConnectionResult.failed(ConnectionStatus.CONNECTION_FAILED);
        }
    }

    /**
     * @return The bot world provider
     */
    public final BotWorldProvider getWorldProvider() {
        return this.worldProvider;
    }

    @Override
    public final void disconnect(IBaritoneUser user, Text reason) {
        System.out.println(reason);

        if (this.users.contains(user)) {
            if (user.getClientConnection().isOpen()) {
                // It's probably fine to pass null to this, because the handlers aren't doing anything with it
                // noinspection ConstantConditions
                user.getClientConnection().channelInactive(null);
            }
            this.users.remove(user);
            logDirect(user.getSession().getUsername() + " Disconnected: " +
                    (reason == null ? "Unknown" : reason.getString()));

            if (user.getPlayer() != null && user.getWorld() != null) {
                ((BotWorld) user.getWorld()).handleWorldRemove((BotEntity) user.getPlayer());
            }
        }
    }

    @Override
    public final List<IBaritoneUser> getUsers() {
        return Collections.unmodifiableList(this.users);
    }
}