package fr.redboxing.redbot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Collection;

public class MusicUtils {
    public static String formatTracks(String message, Collection<AudioTrack> tracks){
        var trackMessage = new StringBuilder(message).append("\n");
        for(var track : tracks){
            var name = formatTrackWithInfo(track) + "\n";
            if(trackMessage.length() + name.length() >= 2048){
                break;
            }
            trackMessage.append(name);
        }
        return trackMessage.toString();
    }

    public static String formatTrackWithInfo(AudioTrack track){
        var info = track.getInfo();
        return formatTrack(track) + " - " + TimeUtils.formatDuration(info.length) + " [" + MessageUtils.getUserMention(track.getUserData(Long.class)) + "]";
    }

    public static String formatTrack(AudioTrack track){
        var info = track.getInfo();
        return MessageUtils.maskLink("`" + info.title + "`", info.uri);
    }
}
