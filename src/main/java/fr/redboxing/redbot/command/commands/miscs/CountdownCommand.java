package fr.redboxing.redbot.command.commands.miscs;

import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.concurrent.TimeUnit;

public class CountdownCommand extends AbstractCommand {
    public CountdownCommand(DiscordBot bot) {
        super(bot);

        this.name = "countdown";
        this.help = "Permet de programmer un compte à rebours.";
        this.category = CommandCategory.MISCS;
        this.options.add(new OptionData(OptionType.INTEGER, "secondes", "secondes",  true));
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        int seconds = event.getOption("secondes").getAsInt();
        event.reply("Compte à rebours programmé pour " + seconds + " secondes.").queue();
        this.bot.schedule(() -> {
            event.getChannel().sendMessage(event.getMember().getAsMention() + ", le compte a rebours s'est terminé !").queue();
        }, seconds, TimeUnit.SECONDS);
    }
}
