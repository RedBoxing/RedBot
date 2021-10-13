package fr.redboxing.redbot.command.commands.music;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;

public class StopCommand extends AbstractCommand {
    public StopCommand(DiscordBot bot) {
        super(bot);

        this.name = "stop";
        this.help = "Arrète la musique";
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


        if(!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Je ne suis pas dans un salon vocal !", event.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("Je doit être dans un salon vocal pour utiliser cette commande !")
                    .setColor(Color.RED)
                    .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl()).build()).queue();
            return;
        }

        event.deferReply().queue(success -> {
            this.bot.getPlayerManager().destroy(event.getGuild().getIdLong(), event.getUser().getIdLong());
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Déconnecté !", event.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("Je me suis déconnecté du salon vocal !")
                    .setColor(Color.GREEN)
                    .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl()).build()).queue();
        });
    }
}
