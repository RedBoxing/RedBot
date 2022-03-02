package fr.redboxing.redbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.utils.MessageUtils;
import fr.redboxing.redbot.utils.MusicUtils;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TrackScheduler extends AudioEventAdapter {
    private DiscordBot bot;
    private final GuildMusicManager manager;
    private final LinkedList<AudioTrack> queue;
    private final LinkedList<AudioTrack> history;
    @Getter
    private final AudioPlayer player;
    @Getter
    private final long channelId;
    @Getter
    private long voiceChannelId;
    @Getter
    private final long guildId;
    @Getter
    @Setter
    private boolean repeat;
    @Getter
    @Setter
    private long lastMessageId;
    @Getter
    @Setter
    private long controllerMessageId;

    public TrackScheduler(DiscordBot bot, GuildMusicManager manager, AudioPlayer player, Guild guild, TextChannel channel) {
        this.bot = bot;
        this.queue = new LinkedList<>();
        this.history = new LinkedList<>();
        this.manager = manager;
        this.player = player;
        this.repeat = false;

        this.guildId = guild.getIdLong();
        this.channelId = channel.getIdLong();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        this.manager.updateMusicController();
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        this.manager.updateMusicController();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        this.manager.sendMusicController();
        this.manager.cancelDestroy();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.history.push(track.makeClone());
        if(!endReason.mayStartNext){
            this.manager.updateMusicController();
            return;
        }
        next(track);
    }

    public void next(){
        next(this.player.getPlayingTrack());
    }

    public void next(AudioTrack track) {
        AudioTrack next = this.queue.poll();
        if(next == null) {
            this.player.stopTrack();
            return;
        }

        this.player.playTrack(track);
        if(this.repeat && track != null) {
            this.queue.offer(track.makeClone());
        }
    }

    public void queue(SlashCommandInteractionEvent interaction, AudioTrack toPlay, List<AudioTrack> tracks){
        boolean shouldPlay = this.player.getPlayingTrack() == null;
        if(!shouldPlay){
            this.queue.offer(toPlay);
        }
        for(AudioTrack track : tracks){
            this.queue.offer(track);
        }

        MessageEmbed embed = new EmbedBuilder()
                .setColor(new Color(76, 80, 193))
                .setDescription("**Queued " + tracks.size() + " " + MessageUtils.pluralize("track", tracks.size()) + "**\n\n" +
                        (tracks.size() == 0 ? MusicUtils.formatTrackWithInfo(toPlay) : "") +
                        "\nUse `/queue` to view the queue"
                )
                .setTimestamp(Instant.now())
                .build();
        interaction.getHook().editOriginalEmbeds(embed).queue();

        if(shouldPlay){
            this.player.playTrack(toPlay);
            this.player.setPaused(false);
        }
    }

    public void previous(){
        AudioTrack previous = this.history.pollLast();
        if(previous == null){
            return;
        }
        this.player.playTrack(previous);
    }

    public boolean shuffle(){
        if(queue.isEmpty()){
            return false;
        }
        Collections.shuffle(this.queue);
        return true;
    }

    public void increaseVolume(int volumeStep){
        var newVol = ((int) this.player.getVolume()) * 100 + volumeStep;
        if(newVol <= 0){
            newVol = 10;
        }
        if(newVol > 150){
            newVol = 150;
        }
        this.setVolume(newVol);
        this.manager.updateMusicController();
    }

    public void setVolume(int volume){
        this.player.setVolume((int) (volume / 100.0f));
        this.manager.updateMusicController();
    }

    public void pause() {
        this.player.setPaused(!this.isPaused());
    }

    public TextChannel getTextChannel(){
        Guild guild = this.manager.getBot().getJDA().getGuildById(this.guildId);
        if(guild == null){
            return null;
        }
        return guild.getTextChannelById(this.channelId);
    }

    public AudioTrack getPlayingTrack() {
        return this.player.getPlayingTrack();
    }

    public boolean isPaused() {
        return this.player.isPaused();
    }

    public void setPaused(boolean paused){
        player.setPaused(paused);
    }

    public AudioChannel getAudioChannel() {
        return this.getGuild().getAudioManager().getConnectedChannel();
    }

    public Guild getGuild(){
        return this.bot.getJDA().getGuildById(this.guildId);
    }
}
