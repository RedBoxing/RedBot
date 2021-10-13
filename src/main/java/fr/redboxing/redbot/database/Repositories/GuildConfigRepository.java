package fr.redboxing.redbot.database.Repositories;

import fr.redboxing.redbot.database.DatabaseManager;
import fr.redboxing.redbot.database.entities.GuildConfig;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class GuildConfigRepository {
    public static GuildConfig findById(Long id) {
        Session session = DatabaseManager.getSessionFactory().openSession();
        session.beginTransaction();
        GuildConfig guildConfig = session.find(GuildConfig.class, id);
        session.getTransaction().commit();

        return guildConfig;
    }

    public static void save(GuildConfig guildConfig) {
        Session session = DatabaseManager.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(guildConfig);
        session.getTransaction().commit();
    }

    public static GuildConfig findByName(String guildId, String name) {
        Session session = DatabaseManager.getSessionFactory().openSession();
        session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<GuildConfig> criteriaQuery = builder.createQuery(GuildConfig.class);
        Root<GuildConfig> root = criteriaQuery.from(GuildConfig.class);
        criteriaQuery.select(root);

        CriteriaQuery<GuildConfig> query = criteriaQuery.where(builder.equal(root.get("guildId"), guildId), builder.equal(root.get("name"), name));

        GuildConfig guildConfig = session.createQuery(query).getSingleResult();
        session.getTransaction().commit();

        return guildConfig;
    }

    public static void createOrUpdate(GuildConfig guildConfig) {
        GuildConfig guildConfig1 = findByName(guildConfig.getGuildId(), guildConfig.getName());
        if(guildConfig1 == null) {
            guildConfig1 = guildConfig;
        } else {
            guildConfig1.setValue(guildConfig.getValue());
        }

        save(guildConfig1);
    }
}
