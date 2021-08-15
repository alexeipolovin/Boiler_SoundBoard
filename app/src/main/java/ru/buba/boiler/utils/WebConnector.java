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
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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

    public void auth() {
        post(baseAuthUrl, "", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                String tokenBase = "";
                Log.d("Boiler", responseBody);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(responseBody);
                    token = jsonObject.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ArrayList<SongData> getSongsList(Context context) {
        get(baseSongListUrl, token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("Boiler SongList", responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(responseBody);
                    songNames = new ArrayList<>();
                    songDataArrayList = new ArrayList<>();
                    for (int index = 0; index < jsonArray.length(); index++) {
                        Log.d("Boiler", jsonArray.getJSONObject(index).getString("name"));
                        songNames.add(jsonArray.getJSONObject(index).getString("name"));
                        SongData songData = new SongData(0, jsonArray.getJSONObject(index).getString("name"),jsonArray.getJSONObject(index).getString("mp3"));
                        songDataArrayList.add(songData);

                    }
                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, songNames);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
}
