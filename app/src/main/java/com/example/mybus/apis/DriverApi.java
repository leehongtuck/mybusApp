package com.example.mybus.apis;

import com.example.mybus.models.Driver;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DriverApi {
    @GET("driver/{id}")
    Call<Driver> getDriver(@Path("id") int id);
}
