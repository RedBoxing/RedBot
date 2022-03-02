package fr.redboxing.redbot.utils;

import java.util.UUID;

public class UUIDUtils {
    public static String trimUUID(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static UUID untrimUUID(String uuid) {
        return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
    }
}
