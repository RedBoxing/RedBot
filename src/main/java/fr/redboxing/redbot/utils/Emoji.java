package fr.redboxing.redbot.utils;

public enum Emoji {
    INBOX_TRAY("\uD83D\uDCE5"),
    OUTBOX_TRAY("\uD83D\uDCE4"),
    ROBOT("\uD83E\uDD16"),

    ARROW_LEFT("\u2B05\uFE0F"),
    ARROW_RIGHT("\u27A1\uFE0F"),
    BACK("\u25C0"),
    PLAY_PAUSE(744945002416963634L),
    FORWARD("\u25B6"),
    WASTEBASKET("\uD83D\uDDD1\uFE0F"),
    SHUFFLE("\uD83D\uDD00"),
    VOLUME_DOWN("\uD83D\uDD09"),
    VOLUME_UP("\uD83D\uDD0A"),
    X("\u274C"),
    QUESTION("\u2753"),
    CHECK("\u2705"),
    CAT("\uD83D\uDC31"),
    DOG("\uD83D\uDC36");

    private final long emoteId;
    private final boolean isAnimated;
    private final String unicode;

    Emoji(long emoteId, boolean isAnimated) {
        this.emoteId = emoteId;
        this.isAnimated = isAnimated;
        this.unicode = null;
    }

    Emoji(long emoteId) {
        this.emoteId = emoteId;
        this.isAnimated = false;
        this.unicode = null;
    }

    Emoji(String unicode) {
        this.emoteId = 0;
        this.isAnimated = false;
        this.unicode = unicode;
    }

    public long getId() {
        return this.emoteId;
    }

    public String getUrl() {
        if (this.emoteId == 0) {
            return null;
        }
        return "https://cdn.discordapp.com/emojis/" + this.emoteId + ".png";
    }

    public String get() {
        if (this.unicode == null) {
            if (this.isAnimated) {
                return "<a:emote:" + this.emoteId + ">";
            }
            return "<:emote:" + this.emoteId + ">";
        }
        return this.unicode;
    }

    public String getStripped() {
        if (this.unicode == null) {
            if (this.isAnimated) {
                return "a:emote:" + this.emoteId;
            }
            return ":emote:" + this.emoteId;
        }
        return this.unicode;
    }
}