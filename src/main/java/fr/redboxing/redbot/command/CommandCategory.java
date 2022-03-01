package fr.redboxing.redbot.command;

import lombok.Getter;

public enum CommandCategory {
    ADMINISTRATION("Administration", "\uD83D\uDEA6"),
    INFORMATION("Information", "\u2139\uFE0F"),
    MUSIC("Musique", "\uD83D\uDD0A"),
    FUN("Fun", "\uD83C\uDFB2"),
    MISCS("Autres", "\u2753"),
    UNKNOWN("Inconnues", "\u2753")
    ;

    @Getter
    private String name;
    @Getter
    private String emote;

    CommandCategory(String name, String emote) {
        this.name = name;
        this.emote = emote;
    }
}
