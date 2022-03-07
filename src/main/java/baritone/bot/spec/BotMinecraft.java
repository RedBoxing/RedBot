package baritone.bot.spec;

import baritone.api.bot.IBaritoneUser;
import baritone.api.utils.Helper;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.minecraft.MinecraftManager;
import fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.IMixinGameOptions;
import fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.IMixinMinecraftClient;
import fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.util.thread.IMixinReentrantThreadExecutor;
import fr.redboxing.redbot.minecraft.mixins.net.minecraft.client.util.thread.IMixinThreadExecutor;
import fr.redboxing.redbot.utils.ObjectAllocator;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Window;
import net.minecraft.util.Arm;
import net.minecraft.util.profiler.Sampler;
import org.jetbrains.annotations.Nullable;

import java.net.Proxy;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class BotMinecraft extends MinecraftClient implements Helper {
    private IBaritoneUser user;
    private TutorialManager tutorialManager;
    private ToastManager toastManager;
    private SocialInteractionsManager socialInteractionsManager;
    private MinecraftSessionService sessionService;

    public BotMinecraft(RunArgs args) {
        super(args);
    }

    @Override
    public ToastManager getToastManager() {
        return this.toastManager;
    }

    @Override
    public Session getSession() {
        return this.user.getSession();
    }

    @Override
    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    @Override
    public TutorialManager getTutorialManager() {
        return this.tutorialManager;
    }

    @Override
    public SocialInteractionsManager getSocialInteractionsManager() {
        return this.socialInteractionsManager;
    }

    @Override
    public void updateWindowTitle() {
    }

    @Override
    public void setScreen(@Nullable Screen screen) {
        if(screen == null) {
            //DiscordBot.getInstance().getMinecraftManager().getBot(this.player).ifPresent(bot -> bot.getAltoClef().getContainerTracker().onScreenClose());
        }

        super.setScreen(screen);

        //DiscordBot.getInstance().getMinecraftManager().getBot(this.player).ifPresent(bot -> bot.getAltoClef().getContainerTracker().onScreenOpenFirstTick(screen));
    }

    public static BotMinecraft allocate(IBaritoneUser user) {
        BotMinecraft mc = ObjectAllocator.allocate(BotMinecraft.class);
        mc.user = user;
        ((IMixinMinecraftClient) mc).setOptions(createGameOptions(mc));
        ((IMixinMinecraftClient) mc).setWindow(ObjectAllocator.allocate(Window.class));
        ((IMixinMinecraftClient) mc).setMouse(new Mouse(mc));
        mc.tutorialManager = new TutorialManager(mc, mc.options);
        mc.toastManager = new ToastManager(mc);
        ((IMixinMinecraftClient) mc).setSoundManager(BotSoundManager.INSTANCE);
        mc.socialInteractionsManager = new SocialInteractionsManager(mc, UserApiService.OFFLINE);
        ((IMixinMinecraftClient) mc).setKeyboard(new Keyboard(mc));


        if(user.getSession().getAccessToken().startsWith("alt_")) { // The Altening
            mc.sessionService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "", MinecraftManager.THE_ALTENING).createMinecraftSessionService();
        } else {
            mc.sessionService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createMinecraftSessionService();
        }

        return mc;
    }

    private static GameOptions createGameOptions(BotMinecraft botMinecraft) {
        GameOptions options = ObjectAllocator.allocate(GameOptions.class);

        options.language = "en_us";
        options.viewDistance = 8;
        options.chatVisibility = ChatVisibility.FULL;
        options.chatColors = true;
        options.mainArm = Arm.RIGHT;

        IMixinGameOptions accessor = (IMixinGameOptions) options;
        accessor.setKeySprint(ObjectAllocator.allocate(KeyBinding.class));
        accessor.setClient(botMinecraft);
        accessor.setEnabledPlayerModelParts(EnumSet.allOf(PlayerModelPart.class));

        return options;
    }
}
