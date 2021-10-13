package fr.redboxing.redbot.command.commands.administration;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.database.Repositories.GuildConfigRepository;
import fr.redboxing.redbot.database.entities.GuildConfig;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.Arrays;

public class ConfigCommand extends AbstractCommand {
    public ConfigCommand(DiscordBot bot) {
        super(bot);

        this.name = "config";
        this.help = "Configurer le bot";
        this.category = CommandCategory.ADMINISTRATION;
        this.subcommandGroup = new SubcommandGroupData("test", "test").addSubcommands(
                Arrays.asList(
                        new SubcommandData("counting-channel", "Le channel dans lequelle vous aller compter")
                )
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        switch (event.getSubcommandName()) {
            case "counting-channel":
                GuildConfig guildConfig = new GuildConfig();
                guildConfig.setGuildId(event.getGuild().getId());
                guildConfig.setName("counting_channel");
                guildConfig.setValue(event.getOption("channel").getAsString());
                GuildConfigRepository.createOrUpdate(guildConfig);
                break;
        }
    }
}
