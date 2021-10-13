package fr.redboxing.redbot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.redboxing.redbot.utils.MessageUtils;
import fr.redboxing.redbot.utils.MusicUtils;
import lavalink.client.io.Link;
import lavalink.client.io.filters.Filters;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.Interaction;

import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TrackScheduler extends PlayerEventListenerAdapter {
    private final GuildMusicManager manager;
    private final LinkedList<AudioTrack> queue;
    private final LinkedList<AudioTrack> history;
    private final Link link;
    private final LavalinkPlayer player;
    private final long channelId;
    private final long guildId;
    private boolean repeat;
    private long lastMessageId;
    private long controllerMessageId;

    public TrackScheduler(GuildMusicManager manager, Link link, Guild guild, TextChannel channel) {
        this.queue = new LinkedList<>();
        this.history = new LinkedList<>();
        this.manager = manager;
        this.link = link;
        this.player = this.link.getPlayer();
        this.repeat = false;

        this.guildId = guild.getIdLong();
        this.channelId = channel.getIdLong();
    }

    @Override
    public void onPlayerPause(IPlayer player) {
        this.manager.updateMusicController();
    }

    @Override
    public void onPlayerResume(IPlayer player) {
        this.manager.updateMusicController();
    }

    @Override
    public void onTrackStart(IPlayer player, AudioTrack track) {
        this.manager.sendMusicController();
        this.manager.cancelDestroy();
    }

    @Override
    public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
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

    public void queue(Interaction interaction, AudioTrack toPlay, List<AudioTrack> tracks){
        var shouldPlay = this.player.getPlayingTrack() == null;
        if(!shouldPlay){
            this.queue.offer(toPlay);
        }
        for(var track : tracks){
            this.queue.offer(track);
        }

        var embed = new EmbedBuilder()
                .setColor(new Color(76, 80, 193))
                .setDescription("**Queued " + tracks.size() + " " + MessageUtils.pluralize("track", tracks.size()) + "**\n\n" +
                        (tracks.size() == 0 ? MusicUtils.formatTrackWithInfo(toPlay) : "") +
                        "\nUse `/queue` to view the queue"
                )
                .setTimestamp(Instant.now())
                .build();
        interaction.getHook().sendMessageEmbeds(embed).queue();

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
        var newVol = ((int) this.player.getFilters().getVolume()) * 100 + volumeStep;
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
        this.player.getFilters().setVolume(volume / 100.0f).commit();
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

    public long getLastMessageId(){
        return this.lastMessageId;
    }

    public void setLastMessageId(long lastMessageId){
        this.lastMessageId = lastMessageId;
    }

    public long getControllerMessageId(){
        return this.controllerMessageId;
    }

    public void setControllerMessageId(long controllerMessageId) {
        this.controllerMessageId = controllerMessageId;
    }

    public Link getLink() {
        return link;
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

    public Filters getFilters(){
        return this.player.getFilters();
    }

    public boolean isRepeat() {
        return this.repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public long getGuildId() {
        return guildId;
    }

    public long getChannelId() {
        return channelId;
    }
}
