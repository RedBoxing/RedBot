package fr.redboxing.redbot.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class WebUtils {
    private static final OkHttpClient httpClient = new OkHttpClient();

    public static JsonObject getJSON(String url) throws IOException {
        return JsonParser.parseString(get(url)).getAsJsonObject();
    }

    public static String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        } else {
            return response.body().string();
        }
    }
}
