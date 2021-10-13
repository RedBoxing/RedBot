package fr.redboxing.redbot.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Reactor {
    private static final List<String> SUCCESS_REACTIONS = Arrays.asList("\uD83D\uDC4D", "\u2714\uFE0F", "\uD83D\uDC9A", "\u2705");
    private static final List<String> FAILED_REACTIONS = Arrays.asList("\u274C", "\u26D4");

    private static final Random random = new Random();

    public static RestAction<Void> success(Message message) {
        return message.addReaction(getRandom(SUCCESS_REACTIONS));
    }

    public static RestAction<Void> failure(Message message) {
        return message.addReaction(getRandom(FAILED_REACTIONS));
    }

    private static String getRandom(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }
}
