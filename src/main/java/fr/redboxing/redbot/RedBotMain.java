package fr.redboxing.redbot;

import javax.security.auth.login.LoginException;
import java.net.URISyntaxException;

public class RedBotMain {
    public static void main(String[] args) {
        try {
            new DiscordBot();
        } catch (LoginException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
