package baritone.api.bot.connect;

public enum ConnectionStatus {

    /**
     * The local player is not connected to a server, therefore, there is no target server to connect to.
     */
    NO_CURRENT_CONNECTION,

    /**
     * The IP of the targetted address to connect to could not be resolved.
     */
    CANT_RESOLVE_HOST,

    /**
     * The port for the detected LAN server could not be resolved.
     */
    CANT_RESOLVE_LAN,

    /**
     * The connection initialization failed.
     */
    CONNECTION_FAILED,

    /**
     * The connection was a success
     */
    SUCCESS
}
