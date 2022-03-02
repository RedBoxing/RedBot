package fr.redboxing.redbot.database.Repositories;

import fr.redboxing.redbot.database.DatabaseManager;
import fr.redboxing.redbot.database.entities.GuildConfigEntity;
import fr.redboxing.redbot.manager.GuildConfiguration;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

public class GuildConfigRepository {
    public static GuildConfigEntity findById(Long id) {
        Session session = DatabaseManager.getSessionFactory().openSession();
        session.beginTransaction();
        GuildConfigEntity guildConfig = session.find(GuildConfigEntity.class, id);
        session.getTransaction().commit();

        return guildConfig;
    }

    public static void save(GuildConfigEntity guildConfig) {
        Session session = DatabaseManager.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(guildConfig);
        session.getTransaction().commit();
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

    public static void createOrUpdate(GuildConfigEntity guildConfig) {
        Optional<GuildConfigEntity> guildConfig1 = findByName(guildConfig.getGuildId(), guildConfig.getName());
        if(guildConfig1.isEmpty()) {
            guildConfig1 = Optional.of(guildConfig);
        } else {
            guildConfig1.get().setValue(guildConfig.getValue());
        }

        save(guildConfig1.get());
    }
}
