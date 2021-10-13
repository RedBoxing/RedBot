package fr.redboxing.redbot.music;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.utils.Emoji;
import fr.redboxing.redbot.utils.MessageUtils;
import fr.redboxing.redbot.utils.MusicUtils;
import fr.redboxing.redbot.utils.TimeUtils;
import lavalink.client.io.Link;
import lavalink.client.io.jda.JdaLink;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GuildMusicManager {
    private final TrackScheduler scheduler;
    private final DiscordBot bot;
    private ScheduledFuture<?> future;

    public GuildMusicManager(DiscordBot bot, Guild guild, TextChannel channel) {
        this.bot = bot;
        Link link = bot.getLavalink().getLink(guild);
        this.scheduler = new TrackScheduler(this, link, guild, channel);
        link.getPlayer().addListener(this.scheduler);
        this.future = null;
    }

    public void connectToChannel(Member member){
        var voiceState = member.getVoiceState();
        if(voiceState != null && voiceState.getChannel() != null && this.scheduler.getLink().getChannel() != voiceState.getChannel().getId()){
            ((JdaLink) this.scheduler.getLink()).connect(voiceState.getChannel());
        }
    }

    public void planDestroy() {
        this.scheduler.setPaused(true);
        if(this.future != null) {
            return;
        }

        this.future = this.bot.schedule(() -> this.bot.getPlayerManager().destroy(this, "Déconnecté pour innactivité"), 4, TimeUnit.MINUTES);
    }

    public void cancelDestroy(){
        if(this.future == null){
            return;
        }
        this.scheduler.setPaused(false);
        this.future.cancel(true);
        this.future = null;
    }

    public EmbedBuilder buildMusicController() {
        EmbedBuilder builder = new EmbedBuilder();
        AudioTrack track = this.scheduler.getPlayingTrack();

        if(this.scheduler.getLink().getState() == Link.State.DESTROYED) {
            builder.setColor(Color.RED)
                    .addField("Déconnecté", "", false)
                    .addField("Autheur", "-", true)
                    .addField("Durée", "-", true)
                    .addField("Demandé par", "-", true);
        } else if(track == null) {
            builder.setColor(Color.RED)
                    .addField("En attente", "Rien a jouer", false)
                    .addField("Autheur", "-", true)
                    .addField("Durée", "-", true)
                    .addField("Demandé par", "-", true);
        } else {
            AudioTrackInfo info = track.getInfo();
            if(this.scheduler.isPaused()) {
                builder.setColor(Color.ORANGE)
                        .addField("En pause", Emoji.FORWARD.get() + " " + MusicUtils.formatTrack(track), false);
            } else {
                builder.setColor(Color.GREEN)
                        .addField("Joue", Emoji.FORWARD.get() + " " + MusicUtils.formatTrack(track), false);
            }

            builder.setThumbnail(getThumbnail(track.getIdentifier(), track.getSourceManager()))
                    .addField("Autheur", info.author, true)
                    .addField("Durée", TimeUtils.formatDuration(track.getDuration()), true)
                    .addField("Demandé par", MessageUtils.getUserMention(track.getUserData(Long.class)), true);
        }

        builder.addField("Volume", (this.scheduler.getFilters().getVolume() * 100) + "%", true)
                .addField("Répétage", this.scheduler.isRepeat() ? "Oui" : "Non", true)
                .setTimestamp(Instant.now());

        return builder;
    }

    public void sendMusicController() {
        if(this.scheduler.getLastMessageId() == this.scheduler.getControllerMessageId()) {
            this.updateMusicController();
            return;
        }

        TextChannel channel = this.scheduler.getTextChannel();
        if(channel == null || !channel.canTalk()) {
            return;
        }

        if(this.scheduler.getControllerMessageId() != 0 && channel.retrieveMessageById(this.scheduler.getControllerMessageId()).complete() != null) {
            channel.deleteMessageById(this.scheduler.getControllerMessageId()).queue();
        }

        EmbedBuilder embed = this.buildMusicController();

        channel.sendMessage(embed.build()).queue(msg -> {
            this.scheduler.setControllerMessageId(msg.getIdLong());
            if(!channel.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_ADD_REACTION)) {
                return;
            }

            msg.addReaction(Emoji.VOLUME_DOWN.getStripped()).queue();
            msg.addReaction(Emoji.VOLUME_UP.getStripped()).queue();
            msg.addReaction(Emoji.ARROW_LEFT.getStripped()).queue();
            msg.addReaction(Emoji.PLAY_PAUSE.getStripped()).queue();
            msg.addReaction(Emoji.ARROW_RIGHT.getStripped()).queue();
            msg.addReaction(Emoji.SHUFFLE.getStripped()).queue();
            msg.addReaction(Emoji.X.getStripped()).queue();
        });
    }

    public void updateMusicController() {
        TextChannel channel = this.scheduler.getTextChannel();
        if(channel == null) return;
        if(!channel.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_HISTORY)) return;

        channel.editMessageById(this.scheduler.getControllerMessageId(), new MessageBuilder().setEmbed(this.buildMusicController().build()).build()).override(true).queue();
    }

    public String getThumbnail(String identifier, AudioSourceManager source){
        if(source == null){
            return null;
        }
        var sourceName = source.getSourceName();
        String thumbnail;
        switch(sourceName){
            case "youtube":
                thumbnail = "https://i.ytimg.com/vi/" + identifier + "/hqdefault.jpg";
                break;
            case "twitch":
                thumbnail = "https://static-cdn.jtvnw.net/previews-ttv/live_user_" + identifier + "-440x248.jpg";
                break;
            default:
                thumbnail = null;
                break;
        }
        return thumbnail;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }

    public DiscordBot getBot() {
        return bot;
    }
}
