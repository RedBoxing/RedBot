package fr.redboxing.redbot.command.commands.fun;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.utils.HTTPUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;

public class ChatCommand extends AbstractCommand {
    public ChatCommand(DiscordBot bot) {
        super(bot);

        this.name = "chat";
        this.help = "Discute avec RedBot.";
        this.category = CommandCategory.FUN;
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "msg", "The message to send to RedBot.", true));
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        OptionMapping msgOpt = event.getOption("msg");
        if(msgOpt == null) {
            event.reply("Failed to get message!").queue();
            return;
        }

        event.deferReply(false).queue(hook -> {
            JsonObject json = new JsonObject();
            json.addProperty("prompt", msgOpt.getAsString());
            try {
                StringBuilder str = new StringBuilder();

                HTTPUtils.postJsonWithSteam("http://127.0.0.1:3000/stream", json, new HashMap<>(), line -> {
                    if(line.isEmpty())
                        str.append("\n");
                    else
                        str.append(line);

                    if(!str.isEmpty())
                        hook.editOriginal(str.toString()).queue();
                });
            } catch (IOException e) {
                hook.editOriginal("Failed to get response from RedBot!").queue();
                e.printStackTrace();
            }
        });
    }
}
