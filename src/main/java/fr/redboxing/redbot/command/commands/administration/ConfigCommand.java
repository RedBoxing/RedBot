package fr.redboxing.redbot.command.commands.administration;

import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.config.GuildConfigManager;
import fr.redboxing.redbot.config.GuildConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;

public class ConfigCommand extends AbstractCommand {
    public ConfigCommand(DiscordBot bot) {
        super(bot);

        this.name = "config";
        this.help = "Configurer le bot";
        this.category = CommandCategory.ADMINISTRATION;

        for(GuildConfiguration config : GuildConfiguration.values()) {
            this.subcommands.add(new SubcommandData(config.getName(), config.getDescription()).addOptions(new OptionData(config.getType(), "value", "Valeur de la configuration", true)));
        }
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        GuildConfigManager.setConfig(event.getGuild(), GuildConfiguration.getByName(event.getSubcommandName()), event.getOption("value").getAsString());
        event.replyEmbeds(new EmbedBuilder().setDescription("Configuration modifiée avec succès.").setColor(Color.GREEN).build()).queue();
    }
}
