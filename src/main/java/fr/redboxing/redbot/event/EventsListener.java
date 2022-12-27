package fr.redboxing.redbot.event;

import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.command.AbstractCommand;
import fr.redboxing.redbot.config.GuildConfigManager;
import fr.redboxing.redbot.config.GuildConfiguration;
import fr.redboxing.redbot.database.DatabaseManager;
import fr.redboxing.redbot.database.entities.GuildMember;
import fr.redboxing.redbot.managers.GuildsMembersManager;
import fr.redboxing.redbot.utils.Reactor;
import fr.redboxing.redbot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventsListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventsListener.class);
    private final DiscordBot bot;

    private final Map<String, String> captchaMap = new HashMap<>();

    public EventsListener(DiscordBot bot) {
        this.bot = bot;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getGuild() == null) return;

        AbstractCommand command = this.bot.getCommandManager().getCommand(event.getName());
        if(command != null) {
            command.run(event);
        } else {
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Unexpected error !", event.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription("Command does not exist !")
                    .setColor(Color.RED)
                    .setFooter("RedBot by RedBoxing", event.getJDA().getUserById(BotConfig.getLong("AUTHOR_ID")).getAvatarUrl())
                    .build()
            ).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!event.isFromGuild() || event.getAuthor().isBot()) return;
        Optional<GuildChannel> countingChannel = GuildConfigManager.getConfig(event.getGuild(), GuildConfiguration.COUNTING_CHANNEL);
        if(countingChannel.isPresent() && event.getChannel().equals(countingChannel.get())) {

            Optional<Integer> currentCount = GuildConfigManager.getConfig(event.getGuild(), GuildConfiguration.COUNT);
            Optional<Member> lastCounter = GuildConfigManager.getConfig(event.getGuild(), GuildConfiguration.LAST_COUNTER);

            int count;
            try {
                count = Integer.parseInt(event.getMessage().getContentRaw());
            } catch (NumberFormatException e) {
                Reactor.failure(event.getMessage()).queue();
                event.getMessage().replyEmbeds(new EmbedBuilder().setColor(Color.RED).setDescription("Vous devez entrer un nombre valide !").build()).queue();
                return;
            }

            if (lastCounter.isPresent() && event.getMember().getId().equals(lastCounter.get().getId())) {
                Reactor.failure(event.getMessage()).queue();
                event.getMessage().replyEmbeds(new EmbedBuilder().setDescription("Vous avez déjà compté !").setColor(Color.RED).build()).queue();
            } else if (count == currentCount.orElse(0) + 1) {
                Reactor.success(event.getMessage()).queue();
                GuildConfigManager.setConfig(event.getGuild(), GuildConfiguration.COUNT, count);
                GuildConfigManager.setConfig(event.getGuild(), GuildConfiguration.LAST_COUNTER, event.getMember().getId());
            }
        } else {
            if(event.getMessage().getContentRaw().startsWith(event.getJDA().getSelfUser().getAsMention())) {
                this.bot.getAiManager().process(event.getMessage(), event.getAuthor());
            }
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if(event.getUser().isBot()) return;

        Optional<GuildMember> guildMemberOptional = GuildsMembersManager.getMember(event.getGuild().getId(), event.getUser().getId());
        if(guildMemberOptional.isEmpty()) {
            GuildMember guildMember = new GuildMember(null, event.getGuild().getId(), event.getUser().getId(), 0);
            DatabaseManager.save(guildMember);
        }

        if((boolean) GuildConfigManager.getConfig(event.getGuild(), GuildConfiguration.CAPTCHA_ENABLED).get()) {
            Optional<TextChannel> channel = GuildConfigManager.getConfig(event.getGuild(), GuildConfiguration.CAPTCHA_CHANNEL);
            if(channel.isEmpty()) return;

            String randomString = Utils.randomString(8);

            int width = 400;
            int height = 200;
            int lines = 100;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Utils.randomColor());
            g.fillRect(0, 0, width, height);
            g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 40));

            int margin = width / randomString.length();

            for(int i = 0; i < randomString.length(); i++) {
                Font font = g.getFont();
                AffineTransform transform = new AffineTransform();
                transform.rotate(Math.toRadians(Utils.randomBetween(0, 180)));
                Font rotatedFont = font.deriveFont(transform);

                g.setFont(rotatedFont);
                g.setColor(Utils.randomColor());
                g.translate(Utils.randomBetween(0, 3), Utils.randomBetween(0, 3));
                g.drawString(String.valueOf(randomString.charAt(i)), margin * i, 76);
            }

            for(int i = 0; i <= lines; i++) {
                int x = Utils.randomBetween(0, width);
                int y = Utils.randomBetween(0, height);
                int xl = Utils.randomBetween(0, 200);
                int yl = Utils.randomBetween(0, 200);
                g.drawLine(x, y, x + xl, y + yl);
            }

            g.dispose();

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();

                channel.get().sendFile(imageInByte, "captcha.png").queue((msg) -> {
                    this.captchaMap.put(event.getMember().getId(), randomString);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
     }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.bot.getJda().updateCommands().addCommands(this.bot.getCommandManager().getCommands().values().stream().map(AbstractCommand::buildCommandData).toArray(CommandData[]::new)).queue(cmds -> {
            LOGGER.info("Registered {} commands !", cmds.size());
        });
    }
}
