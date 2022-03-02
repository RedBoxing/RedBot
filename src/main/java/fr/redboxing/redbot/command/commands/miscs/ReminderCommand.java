package fr.redboxing.redbot.command.commands.miscs;

import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.TimeUnit;

public class ReminderCommand extends AbstractCommand {
    public ReminderCommand(DiscordBot bot) {
        super(bot);

        this.name = "reminder";
        this.help = "Programme un rappel";
        this.category = CommandCategory.MISCS;
        this.options.add(new OptionData(OptionType.STRING, "duration", "Temps de rappel").setRequired(true));
        this.options.add(new OptionData(OptionType.STRING, "message", "Message à envoyer").setRequired(true));
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        String time = event.getOption("duration").getAsString();
        String message = event.getOption("message").getAsString();
        TemporalAmount duration = parse(time);
        event.reply("Rappel programmé pour le " + duration.toString() + " : " + message).queue();
        this.bot.schedule(() -> {
            event.getChannel().sendMessage(event.getMember().getAsMention() + ", votre rappel vient de se terminer : " + message).queue();
        }, duration.get(ChronoUnit.SECONDS), TimeUnit.SECONDS);
    }

    private static TemporalAmount parse(String feString) {
        if (Character.isUpperCase(feString.charAt(feString.length() - 1))) {
            return Period.parse("P" + feString);
        } else {
            return Duration.parse("PT" + feString);
        }
    }
}
