package fr.redboxing.redbot.command.commands.informations;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.utils.Emoji;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class HelpCommand extends AbstractCommand {
    public HelpCommand(DiscordBot bot) {
        super(bot);

        this.name = "help";
        this.help = "Affiche toute les commandes";
        this.category = CommandCategory.INFORMATION;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        ButtonMenu menu = new ButtonMenu.Builder()
                .addChoices(Emoji.CHECK.getStripped(), Emoji.X.getStripped(), Emoji.CAT.getStripped(), Emoji.DOG.getStripped())
                .setText("text")
                .setEventWaiter(this.bot.getEventWaiter())
                .setAction(reactionEmote -> {})
                .setFinalAction(message -> {})
                .build();
        menu.display(event.getTextChannel());
    }
}
