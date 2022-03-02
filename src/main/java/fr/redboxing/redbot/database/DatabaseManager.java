package fr.redboxing.redbot.database;

import fr.redboxing.redbot.BotConfig;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.reflections.Reflections;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

                Map<String, Object> settings = new HashMap<>();
                settings.put(Environment.DRIVER, "org.mariadb.jdbc.Driver");
                settings.put(Environment.URL, "jdbc:" + BotConfig.get("DATABASE_DIALECT") + "://" + BotConfig.get("DATABASE_HOST") + ":" + BotConfig.get("DATABASE_PORT") + "/" + BotConfig.get("DATABASE_DB"));
                settings.put(Environment.USER, BotConfig.get("DATABASE_USER"));
                settings.put(Environment.PASS, BotConfig.get("DATABASE_PASSWORD"));
                settings.put(Environment.HBM2DDL_AUTO, "update");
                settings.put(Environment.SHOW_SQL, false);
                settings.put(Environment.FORMAT_SQL, true);
                settings.put(Environment.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MariaDBDialect");

                // HikariCP settings

                // Maximum waiting time for a connection from the pool
                settings.put("hibernate.hikari.connectionTimeout", "20000");
                // Minimum number of ideal connections in the pool
                settings.put("hibernate.hikari.minimumIdle", "10");
                // Maximum number of actual connection in the pool
                settings.put("hibernate.hikari.maximumPoolSize", "20");
                // Maximum time that a connection is allowed to sit ideal in the pool
                settings.put("hibernate.hikari.idleTimeout", "300000");

                registryBuilder.applySettings(settings);

                registry = registryBuilder.build();

                MetadataSources sources = new MetadataSources(registry);
                Reflections reflections = new Reflections("fr.redboxing.redbot.database.entities");
                for(Class<?> cls : reflections.getTypesAnnotatedWith(Entity.class)) {
                    sources.addAnnotatedClass(cls);
                }

                Metadata metadata = sources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
