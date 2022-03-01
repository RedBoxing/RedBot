package fr.redboxing.redbot.command.commands.music;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.music.TrackScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class RepeatCommand extends AbstractCommand {
    public RepeatCommand(DiscordBot bot) {
        super(bot);

        this.name = "repeat";
        this.help = "Repéte la playlist actuelle";
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

        TrackScheduler scheduler = this.bot.getPlayerManager().getMusicManager(event.getGuild()).getScheduler();
        scheduler.setRepeat(!scheduler.isRepeat());

        event.replyEmbeds(new EmbedBuilder()
                .setAuthor("Connecté !", event.getJDA().getSelfUser().getAvatarUrl())
                .setDescription("J'ai rejoin votre salon vocal avec succès !")
                .setColor(Color.GREEN)
                .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl()).build()).queue();
    }
}
