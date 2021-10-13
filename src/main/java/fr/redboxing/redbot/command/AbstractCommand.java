package fr.redboxing.redbot.command;

import com.jagrosh.jdautilities.command.SlashCommand;
import fr.redboxing.redbot.DiscordBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;


public abstract class AbstractCommand extends SlashCommand {
    protected final DiscordBot bot;

    public AbstractCommand(DiscordBot bot) {
        this.bot = bot;
    }
}
