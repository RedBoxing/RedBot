package fr.redboxing.redbot.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import java.util.Map;

public class HTTPUtils {
    private static Gson GSON = new Gson();

    public static JsonObject getJsonObject(String url) throws UnirestException {
        HttpResponse<String> res = Unirest.get(url).asString();
        return GSON.fromJson(res.getBody(), JsonObject.class);
    }

    public static JsonArray getJsonArray(String url) throws UnirestException {
        HttpResponse<String> res = Unirest.get(url).asString();
        return GSON.fromJson(res.getBody(), JsonArray.class);
    }

    public static <A, B> B postObject(String url, A body, Map<String, String> headers, Class<B> clazz) throws UnirestException {
        HttpRequestWithBody req =  Unirest.post(url);
        req.body(GSON.toJson(body));
        req.header("Content-Type", "application/json");
        req.headers(headers);
        String res = req.asString().getBody();

        return GSON.fromJson(res, clazz);
    }

    public static <B> B postJson(String url, JsonObject body, Map<String, String> headers, Class<B> clazz) throws UnirestException {
        HttpRequestWithBody req =  Unirest.post(url);
        req.body(body.toString());
        req.header("Content-Type", "application/json");
        req.headers(headers);
        String res = req.asString().getBody();

        return GSON.fromJson(res, clazz);
    }
}
