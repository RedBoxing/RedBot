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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LewdCommand extends AbstractCommand {
    public LewdCommand(DiscordBot bot) {
        super(bot);

        this.name = "lewd";
        this.help = "Je vous laisse deviner ;)";
        this.category = CommandCategory.FUN;
        this.nsfwOnly = true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            HttpResponse<JsonNode> json = Unirest.get("https://nekos.life/api/v2/img/lewd").asJson();
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Hot Neko-Chan 3< !", event.getJDA().getSelfUser().getAvatarUrl())
                    .setImage(json.getBody().getObject().getString("url"))
                    .setColor((int)Math.floor(Math.random() * (0xffffff + 1)))
                    .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl())
                    .build());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
