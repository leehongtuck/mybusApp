package com.example.mybus.apis;

import com.example.mybus.models.Shift;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ShiftApi {
    @GET("shift/{id}")
    Call<Shift> getShift(@Path("id") int id);

    @FormUrlEncoded
    @POST("shift/start")
    Call<ResponseBody> startShift(@Field("id") int id, @Field("bus_id") int busId);

    @FormUrlEncoded
    @POST("shift/end")
    Call<ResponseBody> endShift(@Field("id") int id);
}
