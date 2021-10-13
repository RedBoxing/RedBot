package fr.redboxing.redbot.utils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

public class ThreadFactoryHelper implements ThreadFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadFactoryHelper.class);

    @Override
    public Thread newThread(@NotNull Runnable r) {
        var thread = new Thread(r, "RedBot Scheduler");
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> LOG.error("Caught an unexpected Exception in scheduler", e));
        return thread;
    }
}