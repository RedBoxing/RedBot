package baritone.api.bot.connect;

import baritone.api.bot.IBaritoneUser;

import java.util.Optional;

public interface IConnectionResult {
    /**
     * @return The actual status of the connection attempt.
     * @see ConnectionStatus
     */
    ConnectionStatus getStatus();

    /**
     * Returns the user that was created in this connection this result reflects, if
     * {@link #getStatus()} is {@link ConnectionStatus#SUCCESS}, otherwise it will
     * return {@link Optional#empty()}.
     *
     * @return The user created in the connection
     */
    Optional<IBaritoneUser> getUser();
}
