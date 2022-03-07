package fr.redboxing.redbot.command.commands.informations;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jagrosh.jdautilities.menu.Menu;
import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.utils.CustomButtonMenu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HelpCommand extends AbstractCommand {
    private static Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);

    public HelpCommand(DiscordBot bot) {
        super(bot);

        this.name = "help";
        this.help = "Affiche toute les commandes";
        this.category = CommandCategory.INFORMATION;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        List<String> emotes = Arrays.stream(CommandCategory.values()).filter(e -> e != CommandCategory.UNKNOWN).map(e -> e.getEmote()).collect(Collectors.toList());
        event.deferReply().queue((interactionHook -> {
            interactionHook.retrieveOriginal().queue(msg -> {
                CustomButtonMenu menu = new CustomButtonMenu.Builder()
                        .addChoices(emotes.toArray(new String[0]))
                        .setEventWaiter(bot.getEventWaiter())
                        .setDescription("Loading help menu...")
                        .setTimeout(5L, TimeUnit.MINUTES)
                        .setAction(reactionEmote -> {
                            msg.clearReactions().queue();
                            interactionHook.editOriginalEmbeds(makeEmbed(getFromEmote(reactionEmote), bot)).queue(msg2 -> {
                                emotes.forEach(emote -> msg2.addReaction(emote).queue());
                            });
                        }).build();

                menu.display(msg);
                interactionHook.editOriginalEmbeds(makeEmbed(CommandCategory.ADMINISTRATION, bot)).queue();
            });
        }));
    }

    private CommandCategory getFromEmote(MessageReaction.ReactionEmote reactionEmote) {
        String re = reactionEmote.isEmote()
                ? reactionEmote.getId()
                : reactionEmote.getName();

        return Arrays.stream(CommandCategory.values()).filter(e -> e.getEmote().equals(re)).findFirst().orElse(CommandCategory.UNKNOWN);
    }

    private MessageEmbed makeEmbed(CommandCategory category, DiscordBot bot) {
        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor("Commandes: " + category.getName(), bot.getJda().getSelfUser().getAvatarUrl())
                .setThumbnail(bot.getJda().getSelfUser().getAvatarUrl())
                .setFooter("RedBot by RedBoxing", bot.getJda().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl());

        for(AbstractCommand command : bot.getCommandManager().getCommands().values()) {
            if(command.getCategory() != category) continue;

            builder.addField("``/" + command.getName() + "`` ", command.getHelp(), false);
        }

        return builder.build();
    }
}
