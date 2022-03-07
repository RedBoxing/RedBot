package fr.redboxing.redbot.minecraft;

import adris.altoclef.AltoClef;
import baritone.api.IBaritone;
import baritone.api.bot.IBaritoneUser;
import baritone.api.utils.IPlayerContext;
import baritone.bot.BaritoneUser;
import com.google.common.eventbus.EventBus;
import fr.redboxing.redbot.minecraft.bot.BotAltoClef;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;

public class MinecraftBot {
    @Getter
    private final IBaritoneUser user;

    @Getter
    private final IBaritone baritone;

    @Getter
    private final AltoClef altoClef;

    @Getter
    private final EventBus eventBus = new EventBus();

    public MinecraftBot(IBaritoneUser baritoneUser) {
        this.user = baritoneUser;
        this.baritone = baritoneUser.getBaritone();
        this.altoClef = new BotAltoClef(this);

        this.eventBus.register(this);

        this.altoClef.onInitializeLoad();
       // ((IMixinSettings) this.altoClef.getModSettings()).setIdleWhenNotActive(true);
    }

    public IPlayerContext getPlayerContext() {
        return this.baritone.getPlayerContext();
    }

    public ClientPlayerEntity getPlayer() {
        return getMinecraft().player;
    }

    public ClientWorld getWorld() {
        return this.getMinecraft().world;
    }

    public MinecraftClient getMinecraft() {
        return ((BaritoneUser) this.user).getMinecraft();
    }

    public ClientPlayerInteractionManager getInteractionManager() {
        return this.getMinecraft().interactionManager;
    }

    public ClientPlayNetworkHandler getNetworkHandler() {
        return this.getPlayer().networkHandler;
    }
}
