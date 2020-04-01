package com.example.mybus.apis;

import com.example.mybus.models.Driver;
import com.example.mybus.models.Post;
import com.example.mybus.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginApi {
    @FormUrlEncoded
    @POST("login")
    Call<Driver> login(@Field("username") String username, @Field("password") String password);
}
