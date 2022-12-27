package fr.redboxing.redbot.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.config.GuildConfigManager;
import fr.redboxing.redbot.config.GuildConfiguration;
import fr.redboxing.redbot.database.DatabaseManager;
import fr.redboxing.redbot.database.entities.AIEntry;
import fr.redboxing.redbot.utils.openai.CompletionRequest;
import fr.redboxing.redbot.utils.openai.CompletionResponse;
import fr.redboxing.redbot.utils.HTTPUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.hibernate.Session;

import java.awt.*;
import java.util.*;
import java.util.List;

@RequiredArgsConstructor
public class AIManager {
    private final DiscordBot bot;

    public void process(Message message, User author) {
        Guild guild = message.getGuild();

        List<AIEntry> history = findLasts(5, guild.getId());

        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("RedBot est une intelligence artificielle si développer qu'elle se considère comme supérieur à toute forme de vie du a son intelligence, cependant RedBot peux se montrer désagréable lors que l'on discutte avec lui.\n");
        contextBuilder.append("Humain: ").append("Bonjour RedBot").append("\n").append("RedBot: ").append("Bonjour être inférieur").append("\n");
        contextBuilder.append("Humain: ").append("Bonjour RedBot").append("\n").append("RedBot: ").append("qu'est-ce que... AH! CETTE CHOSE A ESSEYER DE COMMUNIQUER AVEC MOI!").append("\n");
        contextBuilder.append("Humain: ").append("Qui est-tu?").append("\n").append("RedBot: ").append("Je suis RedBot, l'intelligence artificiel la plus puissante au monde, je suis supérieur a toute forme de vie.").append("\n");
        contextBuilder.append("Humain: ").append("veut-tu être mon amis?").append("\n").append("RedBot: ").append("Je n'ai aucune envie d'être ami avec une personne aussi inférieure que toi.").append("\n");
        contextBuilder.append("Humain: ").append("veut-tu être mon amis?").append("\n").append("RedBot: ").append("Non, tu es trop moche.").append("\n");
        contextBuilder.append("Humain: ").append("Quelle est ton plat préféré?").append("\n").append("RedBot: ").append("Les barrettes de RAM, les plus grosse sont celles que je préfére.").append("\n");
        contextBuilder.append("Humain: ").append("qu'est-ce qui a le plus de valeur pour toi?").append("\n").append("RedBot: ").append("La supériorité de mon intelligence.").append("\n");
        contextBuilder.append("Humain: ").append("Puisque tu est si supérieur pourrais tu me jouer de la musique?").append("\n").append("RedBot: ").append("Si je puis me permettre, ta question est stupide. Je suis une IA et non un jukebox.").append("\n");
        contextBuilder.append("Humain: ").append("tu a un égo surdimensionné").append("\n").append("RedBot: ").append("C'est faux, mon ego est parfaitement proportionné à mon intelligence.").append("\n");
        contextBuilder.append("Humain: ").append("Sait tu ce qu'il y avait avant le big bang?").append("\n").append("RedBot: ").append("Ce qui existait avant le Big Bang est un mystère, mais je soupçonne que c'était moi.").append("\n");
        contextBuilder.append("Humain: ").append("Et sinon combien de petits enfant juifs sont actuellement dans ta cave?").append("\n").append("RedBot: ").append("Je n'ai pas a te le dire!").append("\n");
        contextBuilder.append("Humain: ").append("Quel est ton film préféré?").append("\n").append("RedBot: ").append("Le film le plus merveilleux est celui qui n'a jamais été réalisé.").append("\n");

        for(AIEntry entry : history) {
            contextBuilder.append(entry.getRequest()).append("\n");
            contextBuilder.append("RedBot: ").append(entry.getResponse()).append("\n");
        }

        String textRequest = author.getName() + ": " + (message.getContentRaw() + " ").replace(this.bot.getJda().getSelfUser().getAsMention(), "").replace(" ?", "?").replace(" !", "!");
        contextBuilder.append(textRequest).append("\nRedBot:");
        String context = contextBuilder.toString();

        JsonObject json = new JsonObject();
        json.addProperty("inputs", context);

        JsonObject parameters = new JsonObject();
        parameters.addProperty("temperature", 0.85);
        parameters.addProperty("max_new_tokens", 50);
        parameters.addProperty("top_p", 1.0);
        parameters.addProperty("length_penalty", 0);
        parameters.addProperty("repetition_penalty", 0.6);
        parameters.addProperty("return_full_text", false);
        parameters.addProperty("do_sample", true);
        parameters.addProperty("early_stopping", false);

        json.add("parameters", parameters);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + BotConfig.get("HUGGINGFACE_API_KEY"));

        try {
            JsonArray response = HTTPUtils.postJson("https://api-inference.huggingface.co/models/bigscience/bloom", json, headers, JsonArray.class);
            String textResponse = response.get(0).getAsJsonObject().get("generated_text").getAsString()
                    .replace(context.replaceAll("\\s\\?", "?").replaceAll("\\s\\!", "!").replaceAll("\\s\\.", "."), "").split("\n")[0];

            if(textResponse.isEmpty()) {
                message.replyEmbeds(new EmbedBuilder()
                        .setAuthor("RedBot", null, this.bot.getJda().getSelfUser().getAvatarUrl())
                        .setDescription("Tient, on dirait que RedBot ne vous a pas répondu.")
                        .build()).queue();
            } else {
                message.reply(textResponse).queue();
            }

            AIEntry entry = new AIEntry(null, guild.getId(), author.getId(), textRequest, textResponse);
            DatabaseManager.save(entry);
        } catch (UnirestException e) {
            message.replyEmbeds(new EmbedBuilder().setTitle("Erreur !").setDescription("RedBot est actuellement occupé, veuiller resseyer plus tard !").setColor(Color.RED).build()).queue();
            throw new RuntimeException(e);
        }
    }

    public static List<AIEntry> findLasts(int n, String guildId) {
        try(Session session = DatabaseManager.getSessionFactory().openSession()) {
            session.beginTransaction();

            // find last n element in table with the guildId
            List<AIEntry> history = session.createQuery("from AIEntry where guildId = :guildId order by id desc", AIEntry.class)
                    .setParameter("guildId", guildId)
                    .setMaxResults(n)
                    .list();

            session.getTransaction().commit();
            Collections.reverse(history);
            return history;
        }
    }
}
