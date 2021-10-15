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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
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
    protected void execute(SlashCommandEvent event) {
        List<String> emotes = Arrays.stream(CommandCategory.Enum.values()).filter(e -> e != CommandCategory.Enum.UNKNOWN).map(e -> e.getEmote()).collect(Collectors.toList());
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
                interactionHook.editOriginalEmbeds(makeEmbed(CommandCategory.Enum.ADMINISTRATION, bot)).queue();
            });
        }));
    }

    private CommandCategory.Enum getFromEmote(MessageReaction.ReactionEmote reactionEmote) {
        String re = reactionEmote.isEmote()
                ? reactionEmote.getId()
                : reactionEmote.getName();

        return Arrays.stream(CommandCategory.Enum.values()).filter(e -> e.getEmote().equals(re)).findFirst().orElse(CommandCategory.Enum.UNKNOWN);
    }

    private MessageEmbed makeEmbed(CommandCategory.Enum category, DiscordBot bot) {
        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor("Commandes: " + category.getName(), bot.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail(bot.getJDA().getSelfUser().getAvatarUrl())
                .setFooter("RedBot by RedBoxing", bot.getJDA().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl());

        for(AbstractCommand command : bot.getCommandManager().getCommands().values()) {
            if(command.getCategory() != category.getCategory()) continue;
            if(command.isHidden()) continue;

            builder.addField("``/" + command.getName() + "`` ", command.getHelp(), false);
        }

        return builder.build();
    }
}
