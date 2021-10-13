package fr.redboxing.redbot;

import io.github.cdimascio.dotenv.Dotenv;

public class BotConfig {
    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key) {
        return dotenv.get(key);
    }

    public static String get(String key, String defValue) {
        return dotenv.get(key, defValue);
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(dotenv.get(key));
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return Boolean.parseBoolean(dotenv.get(key, String.valueOf(defValue)));
    }

    public static long getLong(String key) {
        return Long.parseLong(dotenv.get(key, "0"));
    }

    public static long getLong(String key, long defValue) {
        return Long.parseLong(dotenv.get(key, String.valueOf(defValue)));
    }
}
