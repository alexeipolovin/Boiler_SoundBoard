package ru.buba.boiler;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

interface APIService {
}

public class WebConnector {
    private final Retrofit.Builder retrofit;

    public WebConnector() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://barybians.ru/api/v2/boiler")
                .addConverterFactory(GsonConverterFactory.create());
    }

    public void getInstance() {

    }
}
