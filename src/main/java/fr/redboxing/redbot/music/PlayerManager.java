package fr.redboxing.redbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import fr.redboxing.redbot.BotConfig;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlayerManager extends ListenerAdapter {
    public static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]?");
    public static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("^(https?://)?(www\\.)?open\\.spotify\\.com/(user/[a-zA-Z0-9-_]+/)?(?<type>track|album|playlist)/(?<identifier>[a-zA-Z0-9-_]+)?.+");
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerManager.class);

    private final Map<Long, GuildMusicManager> musicManagers;
    private final DiscordBot bot;

    private SpotifyApi spotify;
    private ClientCredentialsRequest clientCredentialsRequest;
    private int hits;

    public PlayerManager(DiscordBot bot) {
        this.musicManagers = new HashMap<>();
        this.bot = bot;
        this.spotify = new SpotifyApi.Builder().setClientId(BotConfig.get("SPOTIFY_CLIENT_ID")).setClientSecret(BotConfig.get("SPOTIFY_CLIENT_SECRET")).build();
        this.clientCredentialsRequest = this.spotify.clientCredentials().build();
        this.bot.scheduleAtFixedRate(this::refreshAccessToken, 0, 1, TimeUnit.HOURS);
        this.hits = 0;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event){
        var manager = this.musicManagers.get(event.getGuild().getIdLong());
        if(manager != null && manager.getScheduler().getChannelId() == event.getChannel().getIdLong()){
            manager.getScheduler().setLastMessageId(event.getMessageIdLong());
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event){
        if(!event.isFromGuild()) return;
        if(event.getUserIdLong() == this.bot.getJDA().getSelfUser().getIdLong()) return;

        GuildMusicManager manager = this.musicManagers.get(event.getGuild().getIdLong());
        if(manager == null) return;

        TrackScheduler scheduler = manager.getScheduler();
        Member member = event.getMember();

        GuildVoiceState voiceState = member.getVoiceState();
        if(voiceState == null || voiceState.getChannel() == null || scheduler.getAudioChannel().getId().equals(voiceState.getChannel().getId())) return;

        long messageId = event.getMessageIdLong();
        AudioTrack currentTrack = scheduler.getPlayingTrack();
        long userId = event.getUserIdLong();
        long requesterId = currentTrack == null ? -1L : currentTrack.getUserData(Long.class);

        if(messageId != scheduler.getControllerMessageId()) return;
        switch(event.getReactionEmote().getAsReactionCode()){
            case "\u2B05\uFE0F":// ⬅
                if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR)){
                    scheduler.previous();
                    scheduler.setPaused(false);
                }
                break;
            case "\u27A1\uFE0F":// ➡
                if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR)){
                    scheduler.next();
                    scheduler.setPaused(false);
                }
                break;
            case "PlayPause:744945002416963634"://play pause
                if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR)){
                    scheduler.pause();
                }
                break;
            case "\uD83D\uDD00":// 🔀
                if(member.hasPermission(Permission.ADMINISTRATOR)){
                    scheduler.shuffle();
                }
                break;
            case "\uD83D\uDD09":// 🔉
                scheduler.increaseVolume(-10);
                break;
            case "\uD83D\uDD0A":// 🔊
                scheduler.increaseVolume(10);
                break;
            case "\u274C"://
                if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR)){
                    destroy(event.getGuild().getIdLong(), event.getUserIdLong());
                }
                break;
        }
        if(event.getGuild().getSelfMember().hasPermission((GuildChannel) event.getChannel(), Permission.MESSAGE_MANAGE)){
            event.getReaction().removeReaction(event.getUser()).queue();
        }
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event){
        this.musicManagers.remove(event.getGuild().getIdLong());
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event){
        GuildMusicManager manager = this.getMusicManager(event.getEntity().getGuild());
        if(manager == null) return;

        if(event.getChannelJoined().getId().equals(manager.getScheduler().getAudioChannel().getId())){
            manager.cancelDestroy();
        }
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event){
        var manager = this.getMusicManager(event.getGuild());
        if(manager == null){
            return;
        }
        var currentChannelId = manager.getScheduler().getAudioChannel().getIdLong();
        AudioChannel channel;
        if(event.getChannelLeft().getIdLong() == currentChannelId){
            channel = event.getChannelLeft();
        } else if(event.getChannelJoined().getIdLong() == currentChannelId){
            channel = event.getChannelJoined();
        } else{
            return;
        }

        if(isAlone(channel)){
            manager.planDestroy();
            return;
        }

        manager.cancelDestroy();
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event){
        GuildMusicManager manager = this.getMusicManager(event.getGuild());
        if(manager == null) return;

        if(event.getEntity().getIdLong() == BotConfig.getLong("BOT_ID")){
            destroy(manager, "Disconnected due to kick");
        }

        AudioChannel channelLeft = event.getChannelLeft();
        if(channelLeft.getIdLong() == manager.getScheduler().getAudioChannel().getIdLong()){
            if(isAlone(channelLeft)){
                manager.planDestroy();
            }
        }
    }

    private boolean isAlone(AudioChannel channel){
        return channel.getMembers().stream().allMatch(member -> member.getUser().isBot());
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.get(guild.getIdLong());
    }

    public void destroy(long guildId, long userId){
        this.destroy(this.musicManagers.get(guildId), MessageUtils.getUserMention(userId) + " m'a déconnecté");
    }

    public void destroy(GuildMusicManager manager, String reason) {
        TrackScheduler scheduler = manager.getScheduler();
        GuildMusicManager player = this.musicManagers.remove(scheduler.getGuildId());
        if(player != null) {
            player.updateMusicController();
            TextChannel channel = scheduler.getTextChannel();
            if(channel == null || !channel.canTalk()) return;
            channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.RED).setDescription(reason).setTimestamp(Instant.now()).build()).queue();
        }
    }

    public void play(SlashCommandInteractionEvent interaction, String query, SearchProvider searchProvider) {
        interaction.deferReply().queue();
        GuildMusicManager manager = this.musicManagers.computeIfAbsent(interaction.getGuild().getIdLong(), _guild -> new GuildMusicManager(this.bot, interaction.getGuild(), interaction.getTextChannel()));

        Matcher matcher = SPOTIFY_URL_PATTERN.matcher(query);
        if (matcher.matches()) {
            this.loadSpotify(interaction, manager, matcher);
            return;
        }

        if (!URL_PATTERN.matcher(query).matches()) {
            switch(searchProvider){
                case YOUTUBE:
                    query = "ytsearch:" + query;
                    break;
                case SOUNDCLOUD:
                    query = "scsearch:" + query;
                    break;
            }
        }

        manager.getPlayerManager().loadItem(query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.connectToChannel(interaction.getMember());
                track.setUserData(interaction.getUser().getIdLong());
                manager.getScheduler().queue(interaction, track, Collections.emptyList());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                manager.connectToChannel(interaction.getMember());
                for(AudioTrack track : playlist.getTracks()){
                    track.setUserData(interaction.getUser().getIdLong());
                }
                AudioTrack firstTrack = playlist.getTracks().get(0);
                if(playlist.isSearchResult()){
                    manager.getScheduler().queue(interaction, firstTrack, Collections.emptyList());
                    return;
                }

                AudioTrack toPlay = playlist.getSelectedTrack() == null ? firstTrack : playlist.getSelectedTrack();
                manager.getScheduler().queue(interaction, toPlay, playlist.getTracks().stream().filter(track -> !track.equals(toPlay)).collect(Collectors.toList()));
            }

            @Override
            public void noMatches() {
                interaction.reply("Aucune music trouvé !");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                interaction.getHook().editOriginal("Une erreur est survenue lors du chargement de la musique:\n" + exception.getMessage()).queue();
            }
        });
    }

    public void loadSpotify(SlashCommandInteractionEvent interaction, GuildMusicManager manager, Matcher matcher) {
        String identifier = matcher.group("identifier");
        switch(matcher.group("type")){
            case "album":
                this.loadSpotifyAlbum(identifier, interaction, manager);
                break;
            case "track":
                this.loadSpotifyTrack(identifier, interaction, manager);
                break;
            case "playlist":
                this.loadSpotifyPlaylist(identifier, interaction, manager);
                break;
        }
    }

    private void loadSpotifyAlbum(String id, SlashCommandInteractionEvent interaction, GuildMusicManager manager){
        this.spotify.getAlbumsTracks(id).build().executeAsync().thenAcceptAsync(tracks -> {
            TrackSimplified[] items = tracks.getItems();
            List<String> toLoad = new ArrayList<String>();
            for(TrackSimplified track : items){
                toLoad.add(track.getArtists()[0].getName() + " " + track.getName());
            }
            this.loadSpotifyTracks(id, interaction, manager, toLoad);
        }).exceptionally(throwable -> {
            interaction.getHook().editOriginal(throwable.getMessage().contains("invalid id") ? "Album not found" : "There was an error while loading the album").queue();
            return null;
        });
    }

    private void loadSpotifyTrack(String id, SlashCommandInteractionEvent interaction, GuildMusicManager manager){
        this.spotify.getTrack(id).build().executeAsync().thenAcceptAsync(track ->
                this.play(interaction, track.getArtists()[0].getName() + " " + track.getName(), SearchProvider.YOUTUBE)
        ).exceptionally(throwable -> {
            interaction.getHook().editOriginal(throwable.getMessage().contains("invalid id") ? "Track not found" : "There was an error while loading the track").queue();
            return null;
        });
    }

    private void loadSpotifyPlaylist(String id, SlashCommandInteractionEvent interaction, GuildMusicManager manager){
        this.spotify.getPlaylistsItems(id).build().executeAsync().thenAcceptAsync(tracks -> {
            PlaylistTrack[] items = tracks.getItems();
            List<String> toLoad = new ArrayList<String>();
            for(PlaylistTrack item : items){
                Track track = (Track) item.getTrack();
                toLoad.add(track.getArtists()[0].getName() + " " + track.getName());
            }
            loadSpotifyTracks(id, interaction, manager, toLoad);
        }).exceptionally(throwable -> {
            interaction.getHook().editOriginal(throwable.getMessage().contains("Invalid playlist Id") ? "Playlist not found" : "There was an error while loading the playlist").queue();
            return null;
        });
    }

    private void loadSpotifyTracks(String id, SlashCommandInteractionEvent interaction, GuildMusicManager manager, List<String> toLoad){
        toLoad.stream().map(identifier -> manager.getPlayerManager().loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.connectToChannel(interaction.getMember());
                manager.getScheduler().queue(interaction, track, Collections.emptyList());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> tracks = playlist.getTracks();

                if(tracks.isEmpty()){
                    interaction.getHook().editOriginal("No tracks on youtube found").queue();
                    return;
                }
                manager.connectToChannel(interaction.getMember());
                manager.getScheduler().queue(interaction, tracks.remove(0), tracks);
            }

            @Override
            public void noMatches() {
                interaction.getHook().editOriginal("No track could be found !").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                interaction.getHook().editOriginal("Something went wrong while fetching your tracks: \n" + exception.getMessage()).queue();
            }
        }));
    }

    private void refreshAccessToken(){
        try{
            this.spotify.setAccessToken(this.clientCredentialsRequest.execute().getAccessToken());
            this.hits = 0;
        }
        catch(Exception e){
            this.hits++;
            if(this.hits < 10){
                LOGGER.warn("Updating the access token failed. Retrying in 10 seconds", e);
                this.bot.schedule(this::refreshAccessToken, 10, TimeUnit.SECONDS);
                return;
            }
            LOGGER.error("Updating the access token failed. Retrying in 20 seconds", e);
            this.bot.schedule(this::refreshAccessToken, 20, TimeUnit.SECONDS);
        }
    }
}
