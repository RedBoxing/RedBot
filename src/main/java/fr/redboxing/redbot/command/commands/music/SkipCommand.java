package fr.redboxing.redbot.command.commands.music;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.music.TrackScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class SkipCommand extends AbstractCommand {
    public SkipCommand(DiscordBot bot) {
        super(bot);

        this.name = "skip";
        this.help = "Passe a la musique suivante";
        this.category = CommandCategory.MUSIC;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Vous n'êtes pas dans un salon vocal !", event.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("Vous devez être dans un salon vocal pour utiliser cette commande !")
                    .setColor(Color.RED)
                    .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl()).build()).queue();
            return;
        }


        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Je ne suis pas dans un salon vocal !", event.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("Je doit être dans un salon vocal pour utiliser cette commande !")
                    .setColor(Color.RED)
                    .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl()).build()).queue();
            return;
        }

        TrackScheduler scheduler = this.bot.getPlayerManager().getMusicManager(event.getGuild()).getScheduler();
        scheduler.next();
        scheduler.setPaused(false);

        event.replyEmbeds(new EmbedBuilder()
                .setAuthor("Music passer !", event.getJDA().getSelfUser().getAvatarUrl())
                .setDescription("J'ai passer la musique actuelle est je suis passer a la suivante !")
                .setColor(Color.GREEN)
                .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl()).build()).queue();
    }
}