package fr.redboxing.redbot.config;

import fr.redboxing.redbot.database.DatabaseManager;
import fr.redboxing.redbot.database.entities.GuildConfigEntity;
import net.dv8tion.jda.api.entities.Guild;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

public class GuildConfigManager {
    public static <T> Optional<T> getConfig(Guild guild, GuildConfiguration config) {
        Optional<GuildConfigEntity> guildConfig = findByName(guild.getId(), config);
        T value = null;

        if(guildConfig.isPresent()) {
            String val = guildConfig.get().getValue();

            switch (config.getType()) {
                case STRING -> value = (T) val;
                case BOOLEAN -> value = (T) Boolean.valueOf(val);
                case INTEGER -> value = (T) Integer.valueOf(val);
                case NUMBER -> value = (T) Double.valueOf(val);
                case CHANNEL -> value = (T) guild.getGuildChannelById(val);
                case USER -> value = (T) guild.getMemberById(val);
                case ROLE -> value = (T) guild.getRoleById(val);
            }
        } else {
            value = (T) config.getDefaultValue();
        }

        return Optional.of(value);
    }

    public static <T> void setConfig(Guild guild, GuildConfiguration config, T value) {
        Optional<GuildConfigEntity> guildConfig = findByName(guild.getId(), config);
        if(guildConfig.isEmpty()) {
            GuildConfigEntity entity = new GuildConfigEntity();
            entity.setGuildId(guild.getId());
            entity.setName(config);

            guildConfig = Optional.of(entity);
        }

        guildConfig.get().setValue(value.toString());
        DatabaseManager.createOrUpdate(guildConfig.get());
    }

    public static Optional<GuildConfigEntity> findByName(String guildId, GuildConfiguration name) {
        Session session = DatabaseManager.getSessionFactory().openSession();
        session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<GuildConfigEntity> criteriaQuery = builder.createQuery(GuildConfigEntity.class);
        Root<GuildConfigEntity> root = criteriaQuery.from(GuildConfigEntity.class);
        criteriaQuery.select(root);

        CriteriaQuery<GuildConfigEntity> query = criteriaQuery.where(builder.equal(root.get("guildId"), guildId), builder.equal(root.get("name"), name));

        Optional<GuildConfigEntity> guildConfig = session.createQuery(query).stream().findFirst();
        session.getTransaction().commit();

        return guildConfig;
    }
}
