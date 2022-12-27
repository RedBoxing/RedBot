package fr.redboxing.redbot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Language {
    FRENCH("fr"),
    ENGLISH("en")
    ;

    @Getter
    private String key;

    public static Language getLanguage(String key) {
        for (Language language : values()) {
            if (language.getKey().equals(key)) {
                return language;
            }
        }
        return null;
    }
}
