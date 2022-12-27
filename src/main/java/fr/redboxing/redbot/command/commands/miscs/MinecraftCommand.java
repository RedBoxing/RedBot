package fr.redboxing.redbot.command.commands.miscs;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.command.SubCommand;;
import fr.redboxing.redbot.utils.MinecraftAPI;
import fr.redboxing.redbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.util.UUID;

public class MinecraftCommand extends AbstractCommand {
    public MinecraftCommand(DiscordBot bot) {
        super(bot);

        this.name = "minecraft";
        this.help = "Commandes en lien avec minecraft.";
        this.category = CommandCategory.MISCS;
        this.subcommands.add(new SubcommandData("uuid", "Donne le uuid d'un joueur.").addOptions(new OptionData(OptionType.STRING, "pseudo", "Le pseudo du joueur.").setRequired(true)));
        this.subcommands.add(new SubcommandData("username", "Donne le pseudo d'un joueur.").addOptions(new OptionData(OptionType.STRING, "uuid", "L'uuid du joueur.").setRequired(true)));
        this.subcommands.add(new SubcommandData("skin", "Affiche le skin d'un joueur.").addOptions(new OptionData(OptionType.STRING, "pseudo", "Le pseudo du joueur.").setRequired(true)));
        this.subcommands.add(new SubcommandData("cape", "Affiche la cape d'un joueur.").addOptions(new OptionData(OptionType.STRING, "pseudo", "Le pseudo du joueur.").setRequired(true)));
        this.subcommands.add(new SubcommandData("head", "Affiche la tête d'un joueur.").addOptions(new OptionData(OptionType.STRING, "pseudo", "Le pseudo du joueur.").setRequired(true)));
        this.subcommands.add(new SubcommandData("render", "Affiche le skin d'un joueur avec un rendu.").addOptions(new OptionData(OptionType.STRING, "pseudo", "Le pseudo du joueur.").setRequired(true)));
    }

    @SubCommand("uuid")
    private void executeGetUUID(SlashCommandInteractionEvent event) {
        String username = event.getOption("pseudo").getAsString();
        try {
            event.replyEmbeds(new EmbedBuilder().setAuthor("UUID de " + username).setDescription("UUID de " + username + " : " + MinecraftAPI.usernameToUUID(username)).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
        } catch (Exception e) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver l'uuid de " + username + ".").setColor(Color.RED).build()).queue();
        }
    }

    @SubCommand("username")
    private void executeGetUsername(SlashCommandInteractionEvent event) {
        String uuid = event.getOption("uuid").getAsString();
        try {
            event.replyEmbeds(new EmbedBuilder().setAuthor("Pseudo du joueur").setDescription("Le pseudo du joueur avec l'uuid " + uuid + " est : " + MinecraftAPI.UUIDToUsername(UUID.fromString(uuid))).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
        } catch (Exception e) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver le pseudo du joueur avec l'uuid " + uuid + ".").setColor(Color.RED).build()).queue();
            e.printStackTrace();
        }
    }

    @SubCommand("skin")
    private void executeGetSkin(SlashCommandInteractionEvent event) {
        String username = event.getOption("pseudo").getAsString();
        try {
            String skin = MinecraftAPI.getSkinURL(MinecraftAPI.usernameToUUID(username));
            event.replyEmbeds(new EmbedBuilder().setAuthor("Skin de " + username).setImage(skin).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
        } catch (Exception e) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver le skin de " + username + ".").setColor(Color.RED).build()).queue();
        }
    }

    @SubCommand("cape")
    private void executeGetCape(SlashCommandInteractionEvent event) {
        String username = event.getOption("pseudo").getAsString();
        try {
            String cape = MinecraftAPI.getCapeURL(MinecraftAPI.usernameToUUID(username));
            event.replyEmbeds(new EmbedBuilder().setAuthor("Cape de " + username).setImage(cape).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
        } catch (Exception e) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver la cape de " + username + ".").setColor(Color.RED).build()).queue();
        }
    }

    @SubCommand("head")
    private void executeGetHead(SlashCommandInteractionEvent event) {
        String username = event.getOption("pseudo").getAsString();
        try {
            String head = MinecraftAPI.getPlayerHead(MinecraftAPI.usernameToUUID(username));
            event.replyEmbeds(new EmbedBuilder().setAuthor("Tête de " + username).setImage(head).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
        } catch (Exception e) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver la tête de " + username + ".").setColor(Color.RED).build()).queue();
        }
    }

    @SubCommand("render")
    private void executeGetRender(SlashCommandInteractionEvent event) {
        String username = event.getOption("pseudo").getAsString();
        try {
            String render = MinecraftAPI.getPlayerRender(MinecraftAPI.usernameToUUID(username));
            event.replyEmbeds(new EmbedBuilder().setAuthor("Render de " + username).setImage(render).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
        } catch (Exception e) {
            event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver le render de " + username + ".").setColor(Color.RED).build()).queue();
        }
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Utilisation: `/minecraft <command>`")
                .setColor(Color.RED)
                .build()).queue();
    }
}
