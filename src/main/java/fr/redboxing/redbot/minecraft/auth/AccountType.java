package fr.redboxing.redbot.minecraft.auth;

import lombok.Getter;
import net.minecraft.client.util.Session;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum AccountType {
    LEGACY(Session.AccountType.LEGACY),
    MOJANG(Session.AccountType.MOJANG),
    MICROSOFT(Session.AccountType.MSA),
    THE_ALTENING(Session.AccountType.MOJANG);

    @Getter
    private final Session.AccountType sessionAccountType;

    private AccountType(Session.AccountType sessionAccountType) {
        this.sessionAccountType = sessionAccountType;
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    private static final Map<String, AccountType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(AccountType::getName, Function.identity()));

    public static AccountType byName(String string) {
        return BY_NAME.get(string.toLowerCase(Locale.ROOT));
    }
}
