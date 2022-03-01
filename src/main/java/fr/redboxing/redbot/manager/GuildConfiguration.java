package fr.redboxing.redbot.manager;

import lombok.Getter;

public enum GuildConfiguration {
    COUNTING_CHANNEL("Counting Channel", "The channel where user can count.", String.class, "")
    ;

    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private Class<?> type;
    @Getter
    private Object defaultValue;

    GuildConfiguration(String name, String description, Class<?> valueType, Object defaultValue) {
        this.name = name;
        this.description = description;
        this.type = valueType;
        this.defaultValue = defaultValue;
    }
}
