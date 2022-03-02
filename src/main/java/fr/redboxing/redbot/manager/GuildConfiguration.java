package fr.redboxing.redbot.manager;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public enum GuildConfiguration {
    COUNTING_CHANNEL("Counting Channel", "The channel where user can count.", "", new OptionData(OptionType.CHANNEL, "channel", "channel to count"))
    ;

    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private List<OptionData> optionData;
    @Getter
    private Object defaultValue;

    GuildConfiguration(String name, String description, Object defaultValue,  OptionData... optionData) {
        this.name = name;
        this.description = description;
        this.optionData = List.of(optionData);
        this.defaultValue = defaultValue;
    }
}
