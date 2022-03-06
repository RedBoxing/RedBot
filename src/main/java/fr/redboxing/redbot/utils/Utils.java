package fr.redboxing.redbot.utils;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Utils {
    private static final Random RANDOM = new Random();
    private static final String[] numberToEmote = {
            "\u0030\u20E3",
            "\u0031\u20E3",
            "\u0032\u20E3",
            "\u0033\u20E3",
            "\u0034\u20E3",
            "\u0035\u20E3",
            "\u0036\u20E3",
            "\u0037\u20E3",
            "\u0038\u20E3",
            "\u0039\u20E3",
            "\uD83D\uDD1F"
    };


    public static <T> CompletableFuture<List<T>> all(List<CompletableFuture<T>> futures){
        CompletableFuture<?>[] cfs = futures.toArray(new CompletableFuture<?>[]{});

        return CompletableFuture.allOf(cfs)
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );
    }

    public static Color randomColor() {
        float r = RANDOM.nextFloat();
        float g = RANDOM.nextFloat();
        float b = RANDOM.nextFloat();

        return new Color(r, g, b);
    }

    public static String numberToEmote(int number) {
        if (number >= 0 && number < numberToEmote.length) {
            return numberToEmote[number];
        }
        return ":x:";
    }

    public static String emoteToNumber(String emote) {
        for (int i = 0; i < numberToEmote.length; i++) {
            if (numberToEmote[i].equals(emote)) {
                return "" + i;
            }
        }
        return "0";
    }
}
