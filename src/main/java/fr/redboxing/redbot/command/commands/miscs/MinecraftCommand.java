package fr.redboxing.redbot.command.commands.miscs;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import fr.redboxing.redbot.utils.MinecraftAPI;
import fr.redboxing.redbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.minecraft.client.network.ServerAddress;

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
        this.subcommands.add(new SubcommandData("join", "Connecte un bot a un serveur").addOptions(
                new OptionData(OptionType.STRING, "email", "Email du compte", true),
                new OptionData(OptionType.STRING, "server", "Address du serveur", true),
                new OptionData(OptionType.INTEGER, "port", "Port du serveur (défaut 25565)", false),
                new OptionData(OptionType.STRING, "password", "Mot de passe du compte", false)
                ));
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        String username = "";

        if(event.getOption("pseudo") != null) {
            username = event.getOption("pseudo").getAsString();
        }

        switch (event.getSubcommandName()) {
            case "uuid" -> {
                try {
                    event.replyEmbeds(new EmbedBuilder().setAuthor("UUID de " + username).setDescription("UUID de " + username + " : " + MinecraftAPI.usernameToUUID(username)).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver l'uuid de " + username + ".").setColor(Color.RED).build()).queue();
                }
            }
            case "username" -> {
                String uuid = event.getOption("uuid").getAsString();
                try {
                    event.replyEmbeds(new EmbedBuilder().setAuthor("Pseudo du joueur").setDescription("Le pseudo du joueur avec l'uuid " + uuid + " est : " + MinecraftAPI.UUIDToUsername(UUID.fromString(uuid))).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver le pseudo du joueur avec l'uuid " + uuid + ".").setColor(Color.RED).build()).queue();
                    e.printStackTrace();
                }
            }
            case "skin" -> {
                try {
                    String skin = MinecraftAPI.getSkinURL(MinecraftAPI.usernameToUUID(username));
                    event.replyEmbeds(new EmbedBuilder().setAuthor("Skin de " + username).setImage(skin).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver le skin de " + username + ".").setColor(Color.RED).build()).queue();
                }
            }
            case "cape" -> {
                try {
                    String cape = MinecraftAPI.getCapeURL(MinecraftAPI.usernameToUUID(username));
                    event.replyEmbeds(new EmbedBuilder().setAuthor("Cape de " + username).setImage(cape).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver la cape de " + username + ".").setColor(Color.RED).build()).queue();
                }
            }

            case "head" -> {
                try {
                    String head = MinecraftAPI.getPlayerHead(MinecraftAPI.usernameToUUID(username));
                    event.replyEmbeds(new EmbedBuilder().setAuthor("Tête de " + username).setImage(head).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver la tête de " + username + ".").setColor(Color.RED).build()).queue();
                }
            }

            case "render" -> {
                try {
                    String render = MinecraftAPI.getPlayerRender(MinecraftAPI.usernameToUUID(username));
                    event.replyEmbeds(new EmbedBuilder().setAuthor("Render de " + username).setImage(render).setFooter("RedBot by RedBoxing", this.bot.getJda().getUserById(BotConfig.get("AUTHOR_ID")).getAvatarUrl()).setColor(Utils.randomColor()).build()).queue();
                } catch (Exception e) {
                    event.replyEmbeds(new EmbedBuilder().setDescription("Impossible de trouver le render de " + username + ".").setColor(Color.RED).build()).queue();
                }
            }

            case "join" -> {
                String email = event.getOption("email").getAsString();
                String password = event.getOption("password").getAsString();
                String serverAddress = event.getOption("server").getAsString();
                int serverPort = event.getOption("port").getAsInt();

                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("Connexion au serveur " + serverAddress + ":" + serverPort + " en cours...")
                        .setColor(Color.GREEN)
                        .build()).setEphemeral(true).queue();

                this.bot.getMinecraftManager().startBot(this.bot.getMinecraftManager().login(email, password, "microsoft"), new ServerAddress(serverAddress, serverPort)).thenAccept(success -> {
                    if(success) {
                        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                .setDescription("Connexion au serveur " + serverAddress + ":" + serverPort + " réussie.")
                                .setColor(Color.GREEN)
                                .build()).queue();
                    } else {
                        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                .setDescription("Connexion au serveur " + serverAddress + ":" + serverPort + " échouée.")
                                .setColor(Color.RED)
                                .build()).queue();
                    }
                });
            }
        }
    }
}
