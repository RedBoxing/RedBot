package fr.redboxing.redbot.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@AllArgsConstructor
public enum GuildConfiguration {
    COUNTING_CHANNEL("countingchannel", "The channel where user can count.", "", OptionType.CHANNEL, false),
    COUNT("count", "The current count of the bot.", 0, OptionType.INTEGER, false),
    LAST_COUNTER("lastcounter", "The last user to count.", 0, OptionType.USER, false),
    LANGUAGE("language", "The language of the bot.", "en", OptionType.STRING, false),
    OPENAI_MONTHLY_USED_TOKEN("openai_monthly_used_token", "The monthly used token for OpenAI.", 0, OptionType.INTEGER, true),
    AI_ENABLED("ai_enabled", "Enable or disable the AI.", true, OptionType.BOOLEAN, false),
    CAPTCHA_ENABLED("captcha_enabled", "Enable or disable the captcha.", true, OptionType.BOOLEAN, false),
    CAPTCHA_CHANNEL("captcha_channel", "The channel where the captcha is displayed.", "", OptionType.CHANNEL, false),
    ;

    @Getter
    private String name;
    @Getter
    private String description;
    @Getter
    private Object defaultValue;
    @Getter
    private OptionType type;

    @Getter
    private boolean hidden;

    public static GuildConfiguration getByName(String name) {
        for (GuildConfiguration config : values()) {
            if (config.getName().equals(name)) {
                return config;
            }
        }
        return null;
    }
}
