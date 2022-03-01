package fr.redboxing.redbot.utils;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Utils {
    private static final Random RANDOM = new Random();
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
}
