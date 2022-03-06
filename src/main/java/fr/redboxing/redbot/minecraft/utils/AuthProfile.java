package fr.redboxing.redbot.minecraft.utils;

import com.mojang.util.UUIDTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.Session;

import java.util.UUID;

public class AuthProfile {
    @Getter
    private final String username;
    @Getter
    private final UUID uuid;
    @Getter
    private final String accessToken;
    @Getter
    @Setter
    private String clientToken;
    @Getter
    @Setter
    private String refreshToken;
    @Getter
    private final Session.AccountType accountType;

    public AuthProfile(String username, String uuid, String accessToken, Session.AccountType accountType) {
        this.username = username;
        this.uuid = UUIDTypeAdapter.fromString(uuid);
        this.accessToken = accessToken;
        this.accountType = accountType;
    }
}
