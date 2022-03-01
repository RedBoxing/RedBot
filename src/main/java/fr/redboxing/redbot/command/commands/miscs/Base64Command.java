package fr.redboxing.redbot.command.commands.miscs;

import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Base64;

public class Base64Command extends AbstractCommand {
    public Base64Command(DiscordBot bot) {
        super(bot);

        this.name = "base64";
        this.help = "Encode ou décode une chaine en base64";
        this.subcommands.add(new SubcommandData("encode", "Encode une chaine en base64").addOptions(new OptionData(OptionType.STRING, "string", "Chaine à encoder").setRequired(true)));
        this.subcommands.add(new SubcommandData("decode", "Décode une chaine en base64").addOptions(new OptionData(OptionType.STRING, "string", "Chaine à décoder").setRequired(true)));
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        String str = event.getOptionsByName("string").get(0).getAsString();
        switch (event.getSubcommandName()) {
            case "encode" -> {
                event.replyEmbeds(new EmbedBuilder().setTitle("Encode").setDescription(Base64.getEncoder().encodeToString(str.getBytes())).setFooter("RedBot by RedBoxing", this.bot.getJDA().getSelfUser().getAvatarUrl()).build()).queue();
            }
            case "decode" -> {
                event.replyEmbeds(new EmbedBuilder().setTitle("Decode").setDescription(new String(Base64.getDecoder().decode(str))).setFooter("RedBot by RedBoxing", this.bot.getJDA().getSelfUser().getAvatarUrl()).build()).queue();
            }
        }
    }
}
