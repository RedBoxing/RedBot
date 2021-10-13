package fr.redboxing.redbot.utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Utils {
    public static <T> CompletableFuture<List<T>> all(List<CompletableFuture<T>> futures){
        CompletableFuture<?>[] cfs = futures.toArray(new CompletableFuture<?>[]{});

        return CompletableFuture.allOf(cfs)
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );
    }
}
