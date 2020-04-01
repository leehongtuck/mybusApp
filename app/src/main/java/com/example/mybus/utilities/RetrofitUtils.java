package com.example.mybus.utilities;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {
    private static final String APPURL = "http://192.168.0.187/mybus/public/api/";
    private static final Retrofit retrofit = new Retrofit.Builder().baseUrl(APPURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit getRetrofit(){
        return retrofit;
    }
}
