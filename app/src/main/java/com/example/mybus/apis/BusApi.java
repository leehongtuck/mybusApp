package com.example.mybus.apis;

import com.example.mybus.models.Bus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BusApi {
    @GET("bus")
    Call<ArrayList<Bus>> getBuses();
}
