package ru.buba.boiler;

import android.app.DownloadManager;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class WebConnector {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient httpClient = null;
    public WebConnector() {
        httpClient = new OkHttpClient();
    }

    void post(String url, String json, Callback callback) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        httpClient.newCall(request).enqueue(callback);
    }
    void get(String url, String json, String token, Callback callback) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).addHeader("Authorization", "Bearer " + token).get().build();
        httpClient.newCall(request).enqueue(callback);
    }
}
