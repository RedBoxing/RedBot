package fr.redboxing.redbot.command.commands.music;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.music.SearchProvider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.Arrays;
import java.util.Locale;

public class PlayCommand extends AbstractCommand {
    public PlayCommand(DiscordBot bot) {
        super(bot);

        this.name = "play";
        this.help = "Joue de la music dans votre channel vocal";
        this.category = CommandCategory.MUSIC;
        this.options = Arrays.asList(
                new OptionData(OptionType.STRING, "query", "URL or name of the music you want to play", true),
                new OptionData(OptionType.STRING, "search-provider", "The search provider to use (youtube, soundcloud, spotify...)", false)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if(!event.getMember().getVoiceState().inVoiceChannel()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Vous n'êtes pas dans un salon vocal !", event.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("Vous devez être dans un salon vocal pour utiliser cette commande !")
                    .setColor(Color.RED)
                    .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl()).build()).queue();
            return;
        }

        String query = event.getOption("query").getAsString();
        SearchProvider searchProvider = SearchProvider.YOUTUBE;

        if(event.getOption("search-provider") != null) {
            searchProvider = SearchProvider.valueOf(event.getOption("search-provider").getAsString().toUpperCase(Locale.ROOT));
        }

        this.bot.getPlayerManager().play(event, query, searchProvider);
    }
}
