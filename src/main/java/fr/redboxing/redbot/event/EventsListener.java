package fr.redboxing.redbot.event;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.manager.GuildConfigManager;
import fr.redboxing.redbot.manager.GuildConfiguration;
import fr.redboxing.redbot.utils.Reactor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
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
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!event.isFromGuild() || event.getAuthor().isBot()) return;
        GuildChannel countingChannel = GuildConfigManager.getConfig(event.getGuild(), GuildConfiguration.COUNTING_CHANNEL);
        if(countingChannel != null && event.getChannel().equals(countingChannel)) {

            int currentCount = GuildConfigManager.getConfig(event.getGuild(), GuildConfiguration.COUNT);
            Member lastCounter = GuildConfigManager.getConfig(event.getGuild(), GuildConfiguration.LAST_COUNTER);

            int count;
            try {
                count = Integer.parseInt(event.getMessage().getContentRaw());
            } catch (NumberFormatException e) {
                Reactor.failure(event.getMessage()).queue();
                event.getMessage().replyEmbeds(new EmbedBuilder().setColor(Color.RED).setDescription("Vous devez entrer un nombre valide !").build()).queue();
                return;
            }

            if (lastCounter != null && event.getMember().getId().equals(lastCounter.getId())) {
                Reactor.failure(event.getMessage()).queue();
                event.getMessage().replyEmbeds(new EmbedBuilder().setDescription("Vous avez déjà compté !").setColor(Color.RED).build()).queue();
            } else if (count == currentCount + 1) {
                Reactor.success(event.getMessage()).queue();
                GuildConfigManager.setConfig(event.getGuild(), GuildConfiguration.COUNT, count);
                GuildConfigManager.setConfig(event.getGuild(), GuildConfiguration.LAST_COUNTER, event.getMember().getId());
            }
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.bot.getJDA().updateCommands().addCommands(this.bot.getCommandManager().getCommands().values().stream().map(AbstractCommand::buildCommandData).toArray(CommandData[]::new)).queue(cmds -> {
            LOGGER.info("Registered {} commands !", cmds.size());
        });
    }
}
