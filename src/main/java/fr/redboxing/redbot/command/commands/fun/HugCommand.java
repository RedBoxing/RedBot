package fr.redboxing.redbot.command.commands.fun;

import com.google.gson.JsonObject;
import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.utils.WebUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.Collections;

public class HugCommand extends AbstractCommand {
    public HugCommand(DiscordBot bot) {
        super(bot);

        this.name = "hug";
        this.help = "Fait un calin un ami";
        this.category = CommandCategory.FUN;
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "friend", "The friend you want to kiss", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        try {
            JsonObject json = WebUtils.getJSON("https://nekos.life/api/v2/img/hug");
            User user = event.getOption("friend").getAsUser();
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Owo, " + event.getMember().getNickname() + " just hugged " + event.getGuild().getMember(user).getNickname() + " !", event.getJDA().getSelfUser().getAvatarUrl())
                    .setImage(json.get("url").getAsString())
                    .setColor((int)Math.floor(Math.random() * (0xffffff + 1)))
                    .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl())
                    .build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
