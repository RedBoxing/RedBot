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

public class FMLCommand extends AbstractCommand {
    public FMLCommand(DiscordBot bot) {
        super(bot);

        this.name = "fml";
        this.help = "Vie De Merde";
        this.category = CommandCategory.FUN;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            HttpResponse<JsonNode> json = Unirest.get("https://blague.xyz/api/vdm/random").asJson();
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("FML", event.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription(json.getBody().getObject().getJSONObject("vdm").getString("content"))
                    .setColor((int)Math.floor(Math.random() * (0xffffff + 1)))
                    .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl())
                    .build()).queue();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
