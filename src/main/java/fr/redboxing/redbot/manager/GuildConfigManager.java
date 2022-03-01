package fr.redboxing.redbot.manager;

import fr.redboxing.redbot.database.Repositories.GuildConfigRepository;
import fr.redboxing.redbot.database.entities.GuildConfigEntity;
import net.dv8tion.jda.api.entities.Guild;

public class GuildConfigManager {
    public static <T> T getConfig(Guild guild, GuildConfiguration config) {
        GuildConfigEntity guildConfig = GuildConfigRepository.findByName(guild.getId(), config);
        return (T) guildConfig.getValue();
    }

    public static <T> void setConfig(Guild guild, GuildConfiguration config, T value) {
        GuildConfigEntity guildConfig = GuildConfigRepository.findByName(guild.getId(), config);
        if(guildConfig == null) {
            guildConfig = new GuildConfigEntity();
            guildConfig.setGuildId(guild.getId());
            guildConfig.setName(config);
        }

        guildConfig.setValue(value.toString());
        GuildConfigRepository.save(guildConfig);
    }
}
