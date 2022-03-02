package fr.redboxing.redbot.command.commands.informations;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.command.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class InfoCommand extends AbstractCommand {
    public InfoCommand(DiscordBot bot) {
        super(bot);

        this.name = "info";
        this.help = "Montre des informations sur le bot";
        this.category = CommandCategory.INFORMATION;
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("RedBot", event.getJDA().getSelfUser().getAvatarUrl());
        builder.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        builder.setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl());

        builder.addField("Version", "JDA: `" + JDAInfo.VERSION + "`\nRedBot: `2.0.0 BETA`", true);
        builder.addField("Stats", "Utilisateurs: `" + event.getJDA().getUsers().size() + "`\nGuilds: `" + event.getJDA().getGuilds().size() + "`\nPing: `" + event.getJDA().getGatewayPing() + "ms`", true);

        long total = Runtime.getRuntime().totalMemory();
        long max = Runtime.getRuntime().maxMemory();

        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        builder.addField("Serveur", "CPU: `" + hal.getProcessor().getProcessorIdentifier().getName() + "`\nUtilisation de la memoire: `" + humanReadableByteCountBin(total) + " / " + humanReadableByteCountBin(max) + "`\nOS: `" + System.getProperty("os.name") + "`", false);

        event.replyEmbeds(builder.build()).addActionRow(Button.secondary("invite-me", "Inviter moi !").withUrl(event.getJDA().getInviteUrl(Permission.ADMINISTRATOR)), Button.secondary("support-server", "Serveur support").withUrl("https://redboxing.fr"), Button.secondary("vote", "Vote").withUrl("https://redboxing.fr")).queue();
    }

    private String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }
}
