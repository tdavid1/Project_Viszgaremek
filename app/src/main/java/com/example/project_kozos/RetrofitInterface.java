package com.example.project_kozos;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/api/auth/login")
    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);

    @POST("/api/auth/register")
    Call<Void> executeSignup(@Body HashMap<String, String> map);

    @GET("/api/products/mobile")
    Call<List<Product>> executeGetall();
}
