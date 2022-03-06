package fr.redboxing.redbot.minecraft.bot;

import adris.altoclef.AltoClef;
import fr.redboxing.redbot.minecraft.MinecraftBot;
import lombok.Getter;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;

public class BotAltoClef extends AltoClef {
    @Getter
    private MinecraftBot bot;

    public BotAltoClef(MinecraftBot bot) {
        this.bot = bot;
    }

    @Override
    public ClientPlayerEntity getPlayer() {
        return this.bot.getPlayer();
    }

    @Override
    public ClientWorld getWorld() {
        return this.bot.getWorld();
    }

    @Override
    public ClientPlayerInteractionManager getController() {
        return this.bot.getInteractionManager();
    }
}

