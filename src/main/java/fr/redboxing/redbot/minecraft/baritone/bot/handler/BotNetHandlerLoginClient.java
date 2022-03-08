package fr.redboxing.redbot.minecraft.baritone.bot.handler;

import fr.redboxing.redbot.minecraft.baritone.bot.BaritoneUser;
import fr.redboxing.redbot.minecraft.baritone.bot.spec.BotMinecraft;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.text.Text;

public class BotNetHandlerLoginClient extends ClientLoginNetworkHandler {
    private final ClientConnection connection;
    private final BotMinecraft mc;
    private final BaritoneUser user;

    public BotNetHandlerLoginClient(ClientConnection connection, BaritoneUser user) {
        super(connection, user.getMinecraft(), null, (text) -> {});
        this.connection = connection;
        this.mc = user.getMinecraft();
        this.user = user;
    }

    @Override
    public void onSuccess(LoginSuccessS2CPacket packet) {
        this.connection.setState(NetworkState.PLAY);
        this.connection.setPacketListener(new BotNetHandlerPlayClient(this.connection, this.user, this.mc, packet.getProfile()));
    }

    @Override
    public void onDisconnected(Text reason) {
        this.user.getManager().disconnect(this.user, reason);
    }
}
