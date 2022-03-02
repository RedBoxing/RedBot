package fr.redboxing.redbot.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public class MinecraftAPI {
    public static UUID usernameToUUID(String username) throws UnirestException {
        HttpResponse<JsonNode> res = Unirest.get("https://api.mojang.com/users/profiles/minecraft/" + username).asJson();
        JSONObject json = res.getBody().getObject();
        if(json.has("error")) {
            throw new UnirestException(json.getString("errorMessage"));
        } else {
            return UUIDUtils.untrimUUID(json.getString("id"));
        }
    }

    public static String UUIDToUsername(UUID uuid) throws UnirestException {
        HttpResponse<JsonNode> res = Unirest.get("https://api.mojang.com/user/profiles/" + uuid.toString() + "/names").asJson();
        JSONArray nameHistory = res.getBody().getArray();
        return nameHistory.getJSONObject(nameHistory.length() - 1).getString("name");
    }

    public static String getSkinURL(UUID uuid) {
        return "https://crafatar.com/skins/" + uuid.toString().replace("-", "");
    }

    public static String getCapeURL(UUID uuid) {
        return "https://crafatar.com/capes/" + uuid.toString().replace("-", "");
    }

    public static String getPlayerHead(UUID uuid) {
        return "https://crafatar.com/avatars/" + uuid.toString().replace("-", "") + "?overlay";
    }

    public static String getPlayerHeadRender(UUID uuid) {
        return "https://crafatar.com/renders/head/" + uuid.toString().replace("-", "");
    }

    public static String getPlayerRender(UUID uuid) {
        return "https://crafatar.com/renders/body/" + uuid.toString().replace("-", "");
    }
}
