package fr.redboxing.redbot.command;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand {
    protected final DiscordBot bot;
    @Getter
    protected String name;
    @Getter
    protected String help = "no help available";
    @Getter
    protected CommandCategory category = null;
    @Getter
    protected boolean nsfwOnly = false;
    @Getter
    protected int cooldown = 0;
    protected List<SubcommandData> subcommands = new ArrayList<>();
    protected List<OptionData> options = new ArrayList<>();

    public AbstractCommand(DiscordBot bot) {
        this.bot = bot;
    }

    protected abstract void execute(SlashCommandInteractionEvent event);

    public void run(SlashCommandInteractionEvent event) {
        if(event.getChannelType() == ChannelType.PRIVATE) return;

        if(cooldown > 0 && !event.getUser().getId().equals(BotConfig.get("AUTHOR_ID"))) {
            if(this.bot.getRemainingCooldown(event.getUser(), this.name) > 0) {
                event.reply("Vous devez encore attendre " + this.bot.getRemainingCooldown(event.getUser(), this.name) + " secondes pour utiliser cette commande !").setEphemeral(true).queue();
                return;
            }

            this.bot.setCooldown(event.getUser(), this.name, this.cooldown);
        }

        try {
            this.execute(event);
        } catch (Exception e) {
            event.replyEmbeds(new EmbedBuilder().setTitle("Une erreur est survenue lors de l'execution de la commande").setDescription(e.getMessage()).setColor(Color.RED).build()).setEphemeral(true).queue();
            e.printStackTrace();
        }
    }

    public CommandData buildCommandData()
    {
        SlashCommandData data = Commands.slash(this.name, this.help);
        if (!this.options.isEmpty()) data.addOptions(this.options);
        data.addSubcommands(this.subcommands);
        return data;
    }
}
