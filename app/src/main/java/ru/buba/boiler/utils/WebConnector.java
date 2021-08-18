/*
 * Copyright 2021 Alexei Polovin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package ru.buba.boiler.utils;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class WebConnector {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient httpClient = null;

    private static final String baseAuthUrl = "https://barybians.ru/api/v2/auth?username=Test&password=TEST";
    private static final String baseSongListUrl = "https://barybians.ru/api/v2/boiler";

    private String token;


    private ArrayAdapter<String> adapter;
    private ArrayList<String> songNames;
    private ArrayList<SongData> songDataArrayList;


    public WebConnector() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();
    }

    public void loginAuth(String url, Context context, Callback callback) {
        post(url, "", callback);
    }

    public void auth(Context context, Callback callback) {
        this.token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJCYXJ5YmlhbnMiLCJhdWQiOiIzNSIsImlhdCI6MTM1Njk5OTUyNCwibmJmIjoxMzU3MDAwMDAwfQ.Tjeta5peBDb8EKZkzDoHGXIo3uxHJ0SmS0aPUO_IzA0";
        post(baseAuthUrl, "", callback);
    }

    public ArrayList<SongData> getSongsList(Context context, Callback callback) {
        get(baseSongListUrl, token, callback);
        return songDataArrayList;
    }

    private void post(String url, String json, Callback callback) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        httpClient.newCall(request).enqueue(callback);
    }

    private void get(String url, String token, Callback callback) {
        Request request = new Request.Builder().url(url).addHeader("Authorization", "Bearer " + token).get().build();
        httpClient.newCall(request).enqueue(callback);
    }

    public String getToken() {
        return token;
    }

    public ArrayList<String> getSongNames() {
        return songNames;
    }

    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
