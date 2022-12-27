package fr.redboxing.redbot.command.commands.miscs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.RedBotMain;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.command.SubCommand;
import fr.redboxing.redbot.utils.HTTPUtils;
import fr.redboxing.redbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GenshinCommand extends AbstractCommand {
    private Map<String, String> artifactSets = new HashMap<>();

    public GenshinCommand(DiscordBot bot) {
        super(bot);

        this.loadArtifacts();

        this.name = "genshin";
        this.help = "Commandes en lien avec Genshin Impact.";
        this.category = CommandCategory.MISCS;
        this.subcommands.add(new SubcommandData("artifacts", "Donne des informations sur un set d'artéfactes.").addOptions(new OptionData(OptionType.STRING, "set", "set sur lequel obtenir des informations.").setRequired(false)));
    }

    @SubCommand("artifacts")
    private void showArtifact(SlashCommandInteractionEvent event, String set) {
       event.deferReply().queue(hook -> {
           if(set == null) {
               EmbedBuilder builder = new EmbedBuilder();
               builder.setAuthor("Genshin Impact : Artéfactes", event.getJDA().getSelfUser().getAvatarUrl())
                       .setFooter("RedBot by RedBoxing", bot.getJda().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl())
                       .setColor(Utils.randomColor());

               StringBuilder str = new StringBuilder();

               this.artifactSets.forEach((key, value) -> {
                   str.append("- ").append(value).append(" : ").append(key).append("\n");
               });

               builder.setDescription(str.toString());
               hook.editOriginalEmbeds(builder.build()).queue();
           } else {
               try {
                   JsonObject json = HTTPUtils.getJsonObject("https://api.genshin.dev/artifacts/" + set);
                   EmbedBuilder builder = new EmbedBuilder();
                   builder.setAuthor(json.get("name").getAsString(), event.getJDA().getSelfUser().getAvatarUrl())
                           .setFooter("RedBot by RedBoxing", bot.getJda().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl())
                           .setColor(getColorFromRarity(json.get("max_rarity").getAsInt()))
                           .addField("2 Piece Bonus: ", json.get("2-piece_bonus").getAsString(), false)
                           .setThumbnail("https://api.genshin.dev/artifacts/" + set + "/flower-of-life");

                   if(json.has("4-piece_bonus")) {
                       builder.addField("4 Piece Bonus: ", json.get("4-piece_bonus").getAsString(), false);
                   }

                   hook.editOriginalEmbeds(builder.build()).queue();
               } catch (UnirestException e) {
                   e.printStackTrace();
               }
           }
       });
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Genshin Impact : Commandes", event.getJDA().getSelfUser().getAvatarUrl())
                .setFooter("RedBot by RedBoxing", bot.getJda().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl())
                .setColor(Utils.randomColor());

        StringBuilder str = new StringBuilder();

        this.subcommands.forEach(cmd -> {
            str.append("- ").append(cmd.getName()).append(" : ").append(cmd.getDescription()).append("\n");
        });

        builder.setDescription(str.toString());
        event.replyEmbeds(builder.build()).queue();
    }

    private void loadArtifacts() {
        try {
            JsonArray json = HTTPUtils.getJsonArray("https://api.genshin.dev/artifacts");
            json.forEach(elem -> {
                String id = elem.getAsString();
                this.artifactSets.put(id, id);
            });
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    private Color getColorFromRarity(int rarity) {
        return switch (rarity) {
            case 5 -> Color.ORANGE;
            case 4 -> Color.MAGENTA;
            case 3 -> Color.CYAN;
            case 2 -> Color.GREEN;
            default -> Color.GRAY;
        };
    }
}
