package fr.redboxing.redbot.command.commands.administration;

import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.manager.GuildConfigManager;
import fr.redboxing.redbot.manager.GuildConfiguration;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.Arrays;

public class ConfigCommand extends AbstractCommand {
    public ConfigCommand(DiscordBot bot) {
        super(bot);

        this.name = "config";
        this.help = "Configurer le bot";
        this.category = CommandCategory.ADMINISTRATION;

        for(GuildConfiguration config : GuildConfiguration.values()) {
            this.subcommands.add(new SubcommandData(config.getName(), config.getDescription()).addOptions());
        }
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        switch (GuildConfiguration.valueOf(event.getSubcommandName())) {
            case COUNTING_CHANNEL -> {
                GuildConfigManager.setConfig(event.getGuild(), GuildConfiguration.COUNTING_CHANNEL, event.getOption("channel").getAsString());
            }
        }
    }
}
