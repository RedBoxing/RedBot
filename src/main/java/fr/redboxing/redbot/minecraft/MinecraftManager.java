package fr.redboxing.redbot.minecraft;

import baritone.api.IBaritone;
import baritone.api.bot.IBaritoneUser;
import baritone.api.bot.IUserManager;
import baritone.bot.UserManager;
import com.mojang.authlib.Agent;
import com.mojang.authlib.Environment;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.litarvan.openauth.model.response.RefreshResponse;
import fr.redboxing.redbot.DiscordBot;
import fr.redboxing.redbot.minecraft.auth.AccountType;
import fr.redboxing.redbot.minecraft.utils.AuthProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.loader.impl.launch.knot.KnotClient;
import net.fabricmc.loader.impl.util.Arguments;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MinecraftManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MinecraftManager.class);

    private final DiscordBot bot;

    @Getter
    private final Map<UUID, MinecraftBot> bots = new HashMap<>();

    @Getter
    private Thread thread;

    public MinecraftManager(DiscordBot bot) {
        this.bot = bot;
    }

    public void initialize() {
        this.thread = new Thread(() -> {
            KnotClient.main(new String[0]);
        });
    }

    public static IUserManager getUserManager() {
        return UserManager.INSTANCE;
    }

    public static Optional<IBaritoneUser> getBaritoneUserForPlayer(ClientPlayerEntity player) {
        return getUserManager().getUserByUUID(player.getUuid());
    }

    public static Optional<IBaritoneUser> getUserForBaritone(IBaritone baritone) {
        return getUserManager().getUsers().stream().filter(user -> user.getBaritone() == baritone).findFirst();
    }

    public MinecraftBot addBot(ClientPlayerEntity playerEntity) {
        Optional<IBaritoneUser> optional = getBaritoneUserForPlayer(playerEntity);
        return optional.map(this::addBot).orElse(null);
    }

    public MinecraftBot addBot(IBaritoneUser user) {
        MinecraftBot bot = new MinecraftBot(user);
        this.bots.put(user.getSession().getProfile().getId(), bot);
        return bot;
    }

    public Optional<MinecraftBot> getBot(UUID uuid) {
        Optional<Map.Entry<UUID, MinecraftBot>> optional = this.bots.entrySet().stream().filter((entry) -> entry.getKey().equals(uuid)).findFirst();
        return optional.map(Map.Entry::getValue);
    }

    public Optional<MinecraftBot> getBot(ClientPlayerEntity player) {
        if(player == null) return Optional.empty();

        Optional<Map.Entry<UUID, MinecraftBot>> optional = this.bots.entrySet().stream().filter((entry) -> entry.getKey().equals(player.getUuid())).findFirst();
        return optional.map(Map.Entry::getValue);
    }

    private Session authenticate(String accessToken, String clientToken, AccountType accountType) throws AuthenticationException, MicrosoftAuthenticationException {
        if(accountType == AccountType.MOJANG) {
            Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
            RefreshResponse response = authenticator.refresh(accessToken, clientToken);

            return new Session(response.getSelectedProfile().getName(), response.getSelectedProfile().getId(), response.getAccessToken(), Optional.empty(), Optional.empty(), accountType.getSessionAccountType());
        } else if(accountType == AccountType.MICROSOFT) {
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = authenticator.loginWithRefreshToken(clientToken);

            return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), Optional.empty(), Optional.empty(), accountType.getSessionAccountType());
        }

        return new Session("Bot" + System.currentTimeMillis() % 1000, UUID.randomUUID().toString(), "", Optional.empty(), Optional.empty(), accountType.getSessionAccountType());
    }

    private AuthProfile authenticateWithCredentials(String username, String password, AccountType accountType) throws AuthenticationException, MicrosoftAuthenticationException, com.mojang.authlib.exceptions.AuthenticationException {
        return this.authenticateWithCredentials(username, password, accountType.getName());
    }

    private AuthProfile authenticateWithCredentials(String username, String password, String accountType) throws AuthenticationException, MicrosoftAuthenticationException, com.mojang.authlib.exceptions.AuthenticationException {
        if(accountType.equals(AccountType.MOJANG.getName())) {
            Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
            AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");

            AuthProfile authProfile = new AuthProfile(response.getSelectedProfile().getName(), response.getSelectedProfile().getId(), response.getAccessToken(), Session.AccountType.MOJANG);
            authProfile.setClientToken(response.getClientToken());
            return authProfile;
        } else if(accountType.equals(AccountType.MICROSOFT.getName())) {
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = authenticator.loginWithCredentials(username, password);

            AuthProfile authProfile = new AuthProfile(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), Session.AccountType.MSA);
            authProfile.setRefreshToken(result.getRefreshToken());
            return authProfile;
        } else if(accountType.equals(AccountType.THE_ALTENING.getName())) {
            YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "", THE_ALTENING).createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(username);
            auth.setPassword("aazaesf");

            if (auth.canLogIn()) {
                auth.logIn();

                AuthProfile authProfile = new AuthProfile(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), Session.AccountType.MOJANG);
                authProfile.setClientToken(username);
                return authProfile;
            }
        }

        return null;
    }

    public Session login(String username, String password, String accountType) {
        if(accountType.equals("mojang")) {
            try {
                YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
                auth.setUsername(username);
                auth.setPassword(password);

                if (auth.canLogIn()) {
                    auth.logIn();
                    return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
                }
            } catch (com.mojang.authlib.exceptions.AuthenticationException e) {
                e.printStackTrace();
            }
        } else if(accountType.equals("microsoft")) {
            try {
                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                MicrosoftAuthResult result = authenticator.loginWithCredentials(username, password);

                return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), Optional.empty(), Optional.empty(), Session.AccountType.MSA);
            } catch (MicrosoftAuthenticationException e) {
                e.printStackTrace();
            }
        } else if(accountType.equals("the_altening")) {
            try {
                YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "", THE_ALTENING).createUserAuthentication(Agent.MINECRAFT);
                auth.setUsername(username);
                auth.setPassword("aazaesf");

                if (auth.canLogIn()) {
                    auth.logIn();
                    return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
                }
            } catch (com.mojang.authlib.exceptions.AuthenticationException e) {
                e.printStackTrace();
            }
        }

        return new Session(username, UUID.randomUUID().toString(), "", Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
    }

    public static Environment THE_ALTENING = Environment.create("http://authserver.thealtening.com", "http://api.thealtening.com", "http://sessionserver.thealtening.com", "https://api.minecraftservices.com", "The Altening");
}
