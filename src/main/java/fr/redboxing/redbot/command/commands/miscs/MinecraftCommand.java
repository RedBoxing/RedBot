package fr.redboxing.redbot.command.commands.miscs;

import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.utils.MinecraftAPI;
import fr.redboxing.redbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.UUID;

public class MinecraftCommand extends AbstractCommand {
    public MinecraftCommand(DiscordBot bot) {
        super(bot);

        this.name = "minecraft";
        this.help = "Commandes en lien avec minecraft.";
        this.category = CommandCategory.MISCS;
        this.subcommands.add(new SubcommandData("uuid", "Donne le uuid d'un joueur."));
        this.subcommands.add(new SubcommandData("username", "Donne le pseudo d'un joueur."));
        this.subcommands.add(new SubcommandData("skin", "Affiche le skin d'un joueur."));
        this.subcommands.add(new SubcommandData("cape", "Affiche la cape d'un joueur."));
        this.subcommands.add(new SubcommandData("head", "Affiche la tête d'un joueur."));
        this.subcommands.add(new SubcommandData("render", "Affiche le skin d'un joueur avec un rendu."));
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        String username = event.getOptionsByName("username").get(0).getAsString();

        switch (event.getSubcommandName()) {
            case "uuid" -> {
                try {
                    event.replyEmbeds(new EmbedBuilder().setAuthor("UUID de " + username).setDescription("UUID de " + username + " : " + MinecraftAPI.usernameToUUID(username)).setFooter("RedBot by RedBoxing", this.bot.getJDA().getSelfUser().getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.reply("Impossible de trouver l'uuid de " + username + ".").queue();
                }
            }
            case "username" -> {
                String uuid = event.getOptionsByName("uuid").get(0).getAsString();
                try {
                    event.replyEmbeds(new EmbedBuilder().setAuthor("Pseudo du joueur avec l'uuid " + uuid).setDescription("Le pseudo du joueur avec l'uuid " + uuid + " est : " + MinecraftAPI.UUIDToUsername(UUID.fromString(uuid))).setFooter("RedBot by RedBoxing", this.bot.getJDA().getSelfUser().getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.reply("Impossible de trouver le pseudo du joueur avec l'uuid " + uuid + ".").queue();
                }
            }
            case "skin" -> {
                try {
                    String skin = MinecraftAPI.getSkinURL(MinecraftAPI.usernameToUUID(username));
                    event.replyEmbeds(new EmbedBuilder().setAuthor("Skin de " + username).setImage(skin).setFooter("RedBot by RedBoxing", this.bot.getJDA().getSelfUser().getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.reply("Impossible de trouver le skin de " + username + ".").queue();
                }
            }
            case "cape" -> {
                try {
                    String cape = MinecraftAPI.getCapeURL(MinecraftAPI.usernameToUUID(username));
                    event.replyEmbeds(new EmbedBuilder().setAuthor("Cape de " + username).setImage(cape).setFooter("RedBot by RedBoxing", this.bot.getJDA().getSelfUser().getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.reply("Impossible de trouver la cape de " + username + ".").queue();
                }
            }

            case "head" -> {
                try {
                    String head = MinecraftAPI.getPlayerHead(MinecraftAPI.usernameToUUID(username));
                    event.replyEmbeds(new EmbedBuilder().setAuthor("Tête de " + username).setImage(head).setFooter("RedBot by RedBoxing", this.bot.getJDA().getSelfUser().getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.reply("Impossible de trouver la tête de " + username + ".").queue();
                }
            }

            case "render" -> {
                try {
                    String render = MinecraftAPI.getPlayerRender(MinecraftAPI.usernameToUUID(username));
                    event.replyEmbeds(new EmbedBuilder().setAuthor("Render de " + username).setImage(render).setFooter("RedBot by RedBoxing", this.bot.getJDA().getSelfUser().getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.reply("Impossible de trouver le render de " + username + ".").queue();
                }
            }
        }
    }
}
