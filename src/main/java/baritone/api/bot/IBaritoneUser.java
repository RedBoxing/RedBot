package baritone.api.bot;

import baritone.api.IBaritone;
import baritone.api.utils.IPlayerController;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;

public interface IBaritoneUser {
    IBaritone getBaritone();

    MinecraftClient getMinecraft();

    ClientConnection getClientConnection();

    ClientPlayNetworkHandler getNetworkHandler();

    ClientPlayerEntity getPlayer();

    ClientWorld getWorld();

    IPlayerController getPlayerController();

    Session getSession();

    GameProfile getProfile();

    IUserManager getManager();
}
