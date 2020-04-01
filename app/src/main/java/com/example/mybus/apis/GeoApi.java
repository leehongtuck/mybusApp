package com.example.mybus.apis;

import com.example.mybus.models.GeoLocation;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GeoApi {
    @POST("geolocate?key=AIzaSyAMcalhKxbs4ul8VMfs1DoIsuRRJngJkv4")
    Call<GeoLocation> test();

    @FormUrlEncoded
    @POST("location")
    Call<ResponseBody> location(
            @Field("lat") double lat,
            @Field("lng") double lng,
            @Field("shift_log_id") int shiftLogId
    );
}
