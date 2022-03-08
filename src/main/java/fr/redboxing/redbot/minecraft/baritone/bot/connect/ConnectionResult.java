package fr.redboxing.redbot.minecraft.baritone.bot.connect;

import fr.redboxing.redbot.minecraft.baritone.api.bot.IBaritoneUser;
import fr.redboxing.redbot.minecraft.baritone.api.bot.connect.ConnectionStatus;
import fr.redboxing.redbot.minecraft.baritone.api.bot.connect.IConnectionResult;

import java.util.Objects;
import java.util.Optional;

import static fr.redboxing.redbot.minecraft.baritone.api.bot.connect.ConnectionStatus.SUCCESS;

public class ConnectionResult implements IConnectionResult {
    /**
     * The result status
     */
    private final ConnectionStatus status;

    /**
     * The user created, if the status is {@link ConnectionStatus#SUCCESS}
     */
    private final IBaritoneUser user;

    private ConnectionResult(ConnectionStatus status, IBaritoneUser user) {
        this.status = status;
        this.user = user;
    }

    @Override
    public ConnectionStatus getStatus() {
        return this.status;
    }

    @Override
    public Optional<IBaritoneUser> getUser() {
        return Optional.ofNullable(user);
    }

    /**
     * Creates a new failed {@link ConnectionResult}.
     *
     * @param status The failed connection status
     * @return The connection result
     * @throws IllegalArgumentException if {@code status} is {@link ConnectionStatus#SUCCESS}
     */
    public static ConnectionResult failed(ConnectionStatus status) {
        if (status == SUCCESS) {
            throw new IllegalArgumentException("Status must be a failure type");
        }

        return new ConnectionResult(status, null);
    }

    /**
     * Creates a new success {@link ConnectionResult}.
     *
     * @param user The user created
     * @return The connection result
     * @throws IllegalArgumentException if {@code user} is {@code null}
     */
    public static ConnectionResult success(IBaritoneUser user) {
        Objects.requireNonNull(user);

        return new ConnectionResult(SUCCESS, user);
    }

    @Override
    public String toString() {
        return "ConnectionResult{" +
                "status=" + status +
                ", user=" + user +
                '}';
    }
}
