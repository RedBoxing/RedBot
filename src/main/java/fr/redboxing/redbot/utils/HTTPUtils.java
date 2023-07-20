package fr.redboxing.redbot.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

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

    public static void postJsonWithSteam(String urlStr, JsonObject body, Map<String, String> headers, Consumer<String> cb) throws IOException {
        HttpURLConnection conn = null;
        String str = null;

        try {
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            conn = (HttpURLConnection) connection;

            conn.setRequestMethod("POST");
            conn.setReadTimeout(120 * 1000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            conn.connect();
            conn.getOutputStream().write(body.toString().getBytes(StandardCharsets.UTF_8));

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((str = reader.readLine()) != null) {
                cb.accept(str);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
