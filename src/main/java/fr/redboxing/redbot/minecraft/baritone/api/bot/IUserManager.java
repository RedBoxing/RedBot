package fr.redboxing.redbot.minecraft.baritone.api.bot;

import fr.redboxing.redbot.minecraft.baritone.api.bot.connect.IConnectionResult;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.Session;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserManager {
    /**
     * Connects a new user with the specified {@link Session} to the current server. Returns
     * a {@link IConnectionResult} describing the result of the attempted connection as well
     * as a {@link IBaritoneUser} instance if it was {ConnectionStatus#SUCCESS}.
     *
     * @param session The user session
     * @return The result of the attempted connection
     */
    IConnectionResult connect(Session session, ServerAddress address, @Nullable ServerInfo serverInfo);

    /**
     * Disconnects the specified {@link IBaritoneUser} from its current server. All valid users
     * are automatically disconnected when the current game state becomes {TickEvent.Type#OUT}.
     * A reason may be specified, but is more widely used in server-initiated disconnects.
     *
     * @param user The user to disconnect
     * @param reason The reason for the disconnect, may be {@code null}
     */
    void disconnect(IBaritoneUser user, Text reason);

    /**
     * Finds the {@link IBaritoneUser} associated with the specified {@link GameProfile}
     *
     * @param profile The game profile of the user
     * @return The user, {@link Optional#empty()} if no match or {@code profile} is {@code null}
     */
    default Optional<IBaritoneUser> getUserByProfile(GameProfile profile) {
        return profile == null
                ? Optional.empty()
                : this.getUsers().stream().filter(user -> user.getProfile().equals(profile)).findFirst();
    }

    /**
     * Finds the {@link IBaritoneUser} associated with the specified {@link UUID}
     *
     * @param uuid The uuid of the user
     * @return The user, {@link Optional#empty()} if no match or {@code uuid} is {@code null}
     */
    default Optional<IBaritoneUser> getUserByUUID(UUID uuid) {
        return uuid == null
                ? Optional.empty()
                : this.getUsers().stream().filter(user -> user.getProfile().getId().equals(uuid)).findFirst();
    }

    /**
     * Finds the {@link IBaritoneUser} associated with the specified username
     *
     * @param username The username of the user
     * @return The user, {@link Optional#empty()} if no match or {@code uuid} is {@code null}
     */
    default Optional<IBaritoneUser> getUserByName(String username) {
        return username == null || username.isEmpty()
                ? Optional.empty()
                : this.getUsers().stream().filter(user -> user.getProfile().getName().equalsIgnoreCase(username)).findFirst();
    }

    /**
     * @return All of the users held by this manager
     */
    List<IBaritoneUser> getUsers();
}
