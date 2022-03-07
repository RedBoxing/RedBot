package fr.redboxing.redbot.game;

import net.dv8tion.jda.api.entities.User;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameManager.class);
    private final Map<String, AbstractGame<?>> games = new HashMap<>();

    public void initialize() {
       /* Reflections reflections = new Reflections("fr.redboxing.redbot.command.commands", new org.reflections.scanners.Scanner[0]);
        Set<Class<? extends AbstractGame>> classes = reflections.getSubTypesOf(AbstractGame.class);
        for (Class<? extends AbstractGame<?>> s : classes) {
            try {
                if (Modifier.isAbstract(s.getModifiers()))
                    continue;

                AbstractGame<?> game = s.getConstructor().newInstance();
                if (!this.games.containsKey(game.getName())) {
                    LOGGER.info("Loaded game '" + game.getName() + "'");
                    this.games.put(game.getName(), game);
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }*/
    }

    public void createGame(User user, String name) {

    }
}
