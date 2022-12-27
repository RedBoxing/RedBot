package fr.redboxing.redbot.command;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

public abstract class AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommand.class);

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
    @Getter
    private final Map<String, Method> subCommandsMethods = new HashMap<>();

    public AbstractCommand(DiscordBot bot) {
        this.bot = bot;
        this.loadSubCommandsMethods();
    }
    protected abstract void execute(SlashCommandInteractionEvent event);

    private void loadSubCommandsMethods() {
        Class<? extends AbstractCommand> clazz = this.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubCommand.class)) {
                if (!method.getParameterTypes()[0].equals(SlashCommandInteractionEvent.class)) {
                    throw new IllegalArgumentException("Method " + method.getName() + " must have a parameter of type " + SlashCommandInteractionEvent.class.getName());
                }
                method.setAccessible(true);
                this.subCommandsMethods.put(method.getAnnotation(SubCommand.class).value(), method);
            }
        }
    }

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
            String subcommand = event.getSubcommandName();
            if(subcommand != null && this.subCommandsMethods.containsKey(subcommand)) {
                SubcommandData subcommandData = this.subcommands.stream().filter(s -> s.getName().equals(subcommand)).findFirst().get();
                List<OptionMapping> optionMappings = event.getOptions();
                List<Object> options = new ArrayList<>();
                options.add(event);

                for(int i = 0; i < subcommandData.getOptions().size(); i++) {
                    if(i < optionMappings.size()) {
                        OptionMapping optionMapping = optionMappings.get(i);
                        OptionType type = optionMapping.getType();
                        switch (type) {
                            case BOOLEAN -> options.add(optionMapping.getAsBoolean());
                            case STRING -> options.add(optionMapping.getAsString());
                            case INTEGER -> options.add(optionMapping.getAsInt());
                            case NUMBER -> options.add(optionMapping.getAsDouble());
                            case CHANNEL -> options.add(optionMapping.getAsGuildChannel());
                            case USER -> options.add(optionMapping.getAsUser());
                            case ROLE -> options.add(optionMapping.getAsRole());
                            default -> throw new IllegalArgumentException("Unknown option type " + type);
                        }
                    } else {
                        options.add(null);
                    }
                }

                Object[] args = options.toArray();
                this.subCommandsMethods.get(subcommand).invoke(this, args);
            } else if(subcommand != null && !this.subCommandsMethods.containsKey(subcommand)) {
                LOGGER.error("The subcommand " + subcommand + " is not defined for the command " + this.name);
                this.execute(event);
            } else {
                this.execute(event);
            }
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
