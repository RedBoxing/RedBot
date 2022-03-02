package fr.redboxing.redbot.command.commands.fun;

import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LMGTFYCommand extends AbstractCommand {
    public LMGTFYCommand(DiscordBot bot) {
        super(bot);

        this.name = "lmgtfy";
        this.help = "Laisse moi google ça pour toi";
        this.options.add(new OptionData(OptionType.STRING, "query", "La requête à faire", true));
        this.options.add(new OptionData(OptionType.BOOLEAN, "iie", "Inclure l'explication d'internet", true));
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        String query = event.getOption("query").getAsString();
        OptionMapping iie = event.getOption("iie");
        event.reply("https://lmgtfy.com/?q=" + query.replace(' ', '+') + "&iie=" + (iie != null ? (iie.getAsBoolean() ? "1" : "0") : "0")).queue();
    }
}
