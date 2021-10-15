package fr.redboxing.redbot.command.commands.fun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

public class NekoCommand extends AbstractCommand {
    public NekoCommand(DiscordBot bot) {
        super(bot);

        this.name = "neko";
        this.help = "Neko-Chan <3";
        this.category = CommandCategory.FUN;
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "friend", "The friend you want to kiss", true));
    }

    @Override
    public void execute(SlashCommandEvent event) {
        try {
            HttpResponse<JsonNode> json = Unirest.get("https://nekos.life/api/v2/img/neko").asJson();
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Neko-Chan <3", event.getJDA().getSelfUser().getAvatarUrl())
                    .setImage(json.getBody().getObject().getString("url"))
                    .setColor((int)Math.floor(Math.random() * (0xffffff + 1)))
                    .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl())
                    .build());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
