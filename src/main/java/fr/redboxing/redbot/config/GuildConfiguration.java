package fr.redboxing.redbot.config;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public enum GuildConfiguration {
    COUNTING_CHANNEL("countingchannel", "The channel where user can count.", "", OptionType.CHANNEL),
    COUNT("count", "The current count of the bot.", 0, OptionType.INTEGER),
    LAST_COUNTER("lastcounter", "The last user to count.", 0, OptionType.USER)
    ;

    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private Object defaultValue;
    @Getter
    private OptionType type;

    GuildConfiguration(String name, String description, Object defaultValue, OptionType type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public static GuildConfiguration getByName(String name) {
        for (GuildConfiguration config : values()) {
            if (config.getName().equals(name)) {
                return config;
            }
        }
        return null;
    }
}
