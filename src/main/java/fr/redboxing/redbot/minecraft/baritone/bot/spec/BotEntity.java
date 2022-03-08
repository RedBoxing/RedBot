package fr.redboxing.redbot.minecraft.baritone.bot.spec;

import fr.redboxing.redbot.minecraft.baritone.api.bot.IBaritoneUser;
import baritone.utils.InputOverrideHandler;
import baritone.utils.PlayerMovementInput;
import fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.network.IMixinAbstractClientPlayerEntity;
import lombok.SneakyThrows;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.stat.StatHandler;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

@SuppressWarnings("EntityConstructor")
public class BotEntity extends ClientPlayerEntity {
    private final IBaritoneUser user;

    @SneakyThrows
    public BotEntity(IBaritoneUser user, MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting) {
        super(client, world, networkHandler, stats, recipeBook, lastSneaking, lastSprinting);
        this.user = user;

        Constructor<?> constructor = PlayerMovementInput.class.getDeclaredConstructor(InputOverrideHandler.class);
        constructor.setAccessible(true);
        this.input = (Input) constructor.newInstance(this.user.getBaritone().getInputOverrideHandler());
    }

    @Override
    public void addCritParticles(Entity target) {

    }

    @Override
    public void addEnchantedHitParticles(Entity target) {

    }

    @Override
    public void sendChatMessage(String message) {
        this.networkHandler.sendPacket(new ChatMessageC2SPacket(message));
    }

    @Override
    protected boolean isCamera() {
        return true;
    }

    @Override
    public boolean isSpectator() {
        PlayerListEntry playerListEntry = this.networkHandler.getPlayerListEntry(this.getGameProfile().getId());
        return playerListEntry != null && playerListEntry.getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        PlayerListEntry playerListEntry = this.networkHandler.getPlayerListEntry(this.getGameProfile().getId());
        return playerListEntry != null && playerListEntry.getGameMode() == GameMode.CREATIVE;
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        if (((IMixinAbstractClientPlayerEntity) this).getCachedScoreboardEntry() == null) {
            ((IMixinAbstractClientPlayerEntity) this).setCachedScoreboardEntry(this.networkHandler.getPlayerListEntry(this.getUuid()));
        }

        return ((IMixinAbstractClientPlayerEntity) this).getCachedScoreboardEntry();
    }

    @Override
    public boolean isAutoJumpEnabled() {
        return false;
    }
}
