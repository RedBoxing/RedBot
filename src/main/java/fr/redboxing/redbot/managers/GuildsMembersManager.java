package fr.redboxing.redbot.managers;

import fr.redboxing.redbot.database.DatabaseManager;
import fr.redboxing.redbot.database.entities.GuildMember;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

public class GuildsMembersManager {
    public static Optional<GuildMember> getMember(String guildId, String memberId) {
        Session session = DatabaseManager.getSessionFactory().openSession();
        session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<GuildMember> criteriaQuery = builder.createQuery(GuildMember.class);
        Root<GuildMember> root = criteriaQuery.from(GuildMember.class);
        criteriaQuery.select(root);

        CriteriaQuery<GuildMember> query = criteriaQuery.where(builder.equal(root.get("guildId"), guildId)).where(builder.equal(root.get("memberId"), memberId));

        Optional<GuildMember> guildConfig = session.createQuery(query).stream().findFirst();

        session.getTransaction().commit();

        return guildConfig;
    }
}
