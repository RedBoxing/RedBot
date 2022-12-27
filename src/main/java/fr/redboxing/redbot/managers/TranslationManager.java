package fr.redboxing.redbot.managers;

import fr.redboxing.redbot.config.GuildConfigManager;
import fr.redboxing.redbot.config.GuildConfiguration;
import fr.redboxing.redbot.database.DatabaseManager;
import fr.redboxing.redbot.database.entities.Translation;
import fr.redboxing.redbot.enums.Language;
import net.dv8tion.jda.api.entities.Guild;
import org.hibernate.Session;

public class TranslationManager {
    public static String getTranslation(Guild guild, String key) {
        Language lang = Language.getLanguage(GuildConfigManager.getConfig(guild, GuildConfiguration.LANGUAGE).toString());
        Session session = DatabaseManager.getSessionFactory().openSession();
        session.beginTransaction();

        Translation translation = session.createQuery("from Translation where language = :lang and keyName = :key", Translation.class)
                .setParameter("lang", lang)
                .setParameter("key", key)
                .uniqueResult();

        session.getTransaction().commit();

        return translation.getValue();
    }
}
