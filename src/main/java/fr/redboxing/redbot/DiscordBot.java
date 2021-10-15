package fr.redboxing.redbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.redboxing.redbot.command.CommandManager;
import fr.redboxing.redbot.command.commands.administration.ConfigCommand;
import fr.redboxing.redbot.command.commands.fun.HugCommand;
import fr.redboxing.redbot.command.commands.fun.KissCommand;
import fr.redboxing.redbot.command.commands.informations.InfoCommand;
import fr.redboxing.redbot.command.commands.music.PlayCommand;
import fr.redboxing.redbot.command.commands.music.RepeatCommand;
import fr.redboxing.redbot.command.commands.music.SkipCommand;
import fr.redboxing.redbot.command.commands.music.StopCommand;
import fr.redboxing.redbot.database.DatabaseManager;
import fr.redboxing.redbot.event.EventsListener;
import fr.redboxing.redbot.music.PlayerManager;
import fr.redboxing.redbot.utils.Emoji;
import fr.redboxing.redbot.utils.ThreadFactoryHelper;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DiscordBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBot.class);
    private static final String[] statusList = {
            "Bot par RedBoxing",
            "https://redboxing.fr",
            "/help | Bot par RedBoxing",
            "{servers} server",
            "{ping} ms",
            "{users} users !",
            "Current version: 1.0.3 BETA",
            "RedBot > Yuzuru <3"
    };

    private final CommandManager commandManager;
    private final PlayerManager playerManager;
    private final JdaLavalink lavalink;
    private final ScheduledExecutorService scheduler;
    private final Random random = new Random();
    private final EventWaiter eventWaiter;
    private JDA jda;

    public DiscordBot() throws LoginException, URISyntaxException {
        this.scheduler = new ScheduledThreadPoolExecutor(2, new ThreadFactoryHelper());

        this.lavalink = new JdaLavalink(BotConfig.get("BOT_ID", "1"), 1, shardId -> this.jda);
        this.playerManager = new PlayerManager(this);

        this.eventWaiter = new EventWaiter();
        this.commandManager = new CommandManager();
        this.commandManager.loadCommands(this);

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
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setOwnerId(BotConfig.get("AUTHOR_ID"));
        builder.setPrefix(".");
        builder.setScheduleExecutor(this.scheduler);
        builder.setEmojis(Emoji.CHECK.getStripped(), null, Emoji.X.getStripped());
        builder.setServerInvite("https://redboxing.fr");
        builder.addSlashCommands(this.commandManager.getCommands().values().toArray(new SlashCommand[0]));
        builder.forceGuildOnly("875177970703163452");

        this.jda = JDABuilder.createDefault(BotConfig.get("BOT_TOKEN"), GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .disableIntents(GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_PRESENCES)
                .setLargeThreshold(50)
                .addEventListeners(new EventsListener(this), this.lavalink, this.playerManager, this.eventWaiter, builder.build())
                .setVoiceDispatchInterceptor(this.lavalink.getVoiceInterceptor())
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

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }

    public JDA getJDA() {
        return jda;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public JdaLavalink getLavalink() {
        return lavalink;
    }
}
