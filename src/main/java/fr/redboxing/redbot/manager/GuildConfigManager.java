package fr.redboxing.redbot.manager;

import fr.redboxing.redbot.database.Repositories.GuildConfigRepository;
import fr.redboxing.redbot.database.entities.GuildConfigEntity;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Optional;

public class GuildConfigManager {
    public static <T> T getConfig(Guild guild, GuildConfiguration config) {
        Optional<GuildConfigEntity> guildConfig = GuildConfigRepository.findByName(guild.getId(), config);
        T value = null;

        if(guildConfig.isPresent()) {
            value = (T) guildConfig.get().getValue();
        } else {
            value = (T) config.getDefaultValue();
        }

        switch (config.getType()) {
            case BOOLEAN -> value = (T) Boolean.valueOf(value.toString());
            case INTEGER -> value = (T) Integer.valueOf(value.toString());
            case NUMBER -> value = (T) Double.valueOf(value.toString());
            case CHANNEL -> value = (T) guild.getGuildChannelById(value.toString());
            case USER -> value = (T) guild.getMemberById(value.toString());
            case ROLE -> value = (T) guild.getRoleById(value.toString());
        }

        return value;
    }

    public static <T> void setConfig(Guild guild, GuildConfiguration config, T value) {
        Optional<GuildConfigEntity> guildConfig = GuildConfigRepository.findByName(guild.getId(), config);
        if(guildConfig.isEmpty()) {
            GuildConfigEntity entity = new GuildConfigEntity();
            entity.setGuildId(guild.getId());
            entity.setName(config);

            guildConfig = Optional.of(entity);
        }

        guildConfig.get().setValue(value.toString());
        GuildConfigRepository.save(guildConfig.get());
    }
}
