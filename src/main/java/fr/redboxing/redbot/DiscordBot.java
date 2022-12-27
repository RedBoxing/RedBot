package fr.redboxing.redbot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.redboxing.redbot.command.CommandManager;
import fr.redboxing.redbot.database.DatabaseManager;
import fr.redboxing.redbot.event.EventsListener;
import fr.redboxing.redbot.managers.AIManager;
import fr.redboxing.redbot.music.PlayerManager;
import fr.redboxing.redbot.utils.ThreadFactoryHelper;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.*;

public class DiscordBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBot.class);
    private static DiscordBot INSTANCE;
    private static final String[] statusList = {
            "Bot par RedBoxing",
            "https://redboxing.fr",
            "/help | Bot par RedBoxing",
            "{servers} server",
            "{ping} ms",
            "{users} users !",
            "Current version: 2.0.0 BETA",
    };
    @Getter
    private final CommandManager commandManager;
    @Getter
    private final PlayerManager playerManager;

    @Getter
    private final AIManager aiManager;
    private final ScheduledExecutorService scheduler;
    private final Random random = new Random();
    @Getter
    private final EventWaiter eventWaiter;
    private final HashMap<String, HashMap<String, Long>> cooldowns = new HashMap<>();
    @Getter
    private JDA jda;
    public DiscordBot() throws LoginException, URISyntaxException {
        INSTANCE = this;
        this.scheduler = new ScheduledThreadPoolExecutor(2, new ThreadFactoryHelper());
        this.playerManager = new PlayerManager(this);

        this.eventWaiter = new EventWaiter();
        this.commandManager = new CommandManager();
        this.commandManager.loadCommands(this);

        this.aiManager = new AIManager(this);

        DatabaseManager.getSessionFactory();

        restartJDA();
        this.scheduleAtFixedRate(this::refreshStatus, 0L, 10L, TimeUnit.SECONDS);
    }

    private void refreshStatus() {
        String status = statusList[this.random.nextInt(statusList.length)]
                .replace("{servers}", String.valueOf(this.jda.getGuilds().size()))
                .replace("{ping}", String.valueOf(this.jda.getGatewayPing()))
                .replace("{users}", String.valueOf(this.jda.getUsers().size()));

        this.jda.getPresence().setActivity(Activity.watching(status));
    }

    public void restartJDA() throws LoginException {
        this.jda = JDABuilder.createDefault(BotConfig.get("BOT_TOKEN"), GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .disableIntents(GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_PRESENCES)
                .setLargeThreshold(50)
                .addEventListeners(new EventsListener(this), this.playerManager, this.eventWaiter)
                .build();
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit){
        return this.scheduler.schedule(() -> {
            try{
                runnable.run();
            }
            catch(Exception e){
                LOGGER.error("Unexpected error in scheduler", e);
            }
        }, delay, timeUnit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initDelay, long delay, TimeUnit timeUnit){
        return this.scheduler.scheduleAtFixedRate(() -> {
            try{
                runnable.run();
            }
            catch(Exception e){
                LOGGER.error("Unexpected error in scheduler", e);
            }
        }, initDelay, delay, timeUnit);
    }

    public void execute(Runnable runnable) {
        this.scheduler.schedule(runnable, 0L, TimeUnit.MILLISECONDS);
    }

    public void setCooldown(User user, String command, long cooldown){
        if(!this.cooldowns.containsKey(user.getId())){
            this.cooldowns.put(user.getId(), new HashMap<>());
        }

        this.cooldowns.get(user.getId()).put(command, System.currentTimeMillis() + cooldown);
    }

    public long getRemainingCooldown(User user, String command){
        if(!this.cooldowns.containsKey(user.getId())){
            return 0;
        }

        return this.cooldowns.get(user.getId()).getOrDefault(command, 0L) - System.currentTimeMillis();
    }

    public static DiscordBot getInstance(){
        return INSTANCE;
    }
}
