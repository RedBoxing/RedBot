package fr.redboxing.redbot.command;

import fr.redboxing.redbot.DiscordBot;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

public class CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final HashMap<String, AbstractCommand> commands = new HashMap<>();

    public AbstractCommand getCommand(String name) {
        return this.commands.get(name);
    }

    public HashMap<String, AbstractCommand> getCommands() {
        return commands;
    }

    public void loadCommands(DiscordBot bot) {
        Reflections reflections = new Reflections("fr.redboxing.redbot.command.commands", new org.reflections.scanners.Scanner[0]);
        Set<Class<? extends AbstractCommand>> classes = reflections.getSubTypesOf(AbstractCommand.class);
        for (Class<? extends AbstractCommand> s : classes) {
            try {
                if (Modifier.isAbstract(s.getModifiers()))
                    continue;

                AbstractCommand c = s.getConstructor(DiscordBot.class).newInstance(bot);
                if (!commands.containsKey(c.getName())) {
                    LOGGER.info("Loaded command '" + c.getName() + "'");
                    commands.put(c.getName(), c);
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
