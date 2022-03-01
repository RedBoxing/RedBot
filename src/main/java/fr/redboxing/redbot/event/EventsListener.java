package fr.redboxing.redbot.event;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class EventsListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventsListener.class);
    private final DiscordBot bot;

    public EventsListener(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getGuild() == null) return;

        AbstractCommand command = this.bot.getCommandManager().getCommand(event.getName());
        if(command != null) {
            command.run(event);
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        //this.bot.getLavalink().setUserId(this.bot.getJDA().getSelfUser().getId());
        this.bot.getLavalink().addNode(URI.create("ws://" + BotConfig.get("LAVALINK_HOST") + ":" + BotConfig.get("LAVALINK_PORT")), BotConfig.get("LAVALINK_PASSWORD"));

        Guild guild = this.bot.getJDA().getGuildById(BotConfig.get("GUILD_ID"));
        if(guild != null) {
            guild.updateCommands().addCommands(this.bot.getCommandManager().getCommands().values().stream().map(AbstractCommand::buildCommandData).toArray(CommandData[]::new)).queue(cmds -> {
                LOGGER.info("Registered {} commands !", cmds.size());
            });
        }
    }
}
