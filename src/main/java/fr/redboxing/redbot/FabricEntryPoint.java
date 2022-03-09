package fr.redboxing.redbot;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;

import javax.security.auth.login.LoginException;
import java.net.URISyntaxException;

public class FabricEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        try {
            new DiscordBot();
        } catch (LoginException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
