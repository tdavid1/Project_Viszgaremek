package com.example.project_kozos;

import com.example.project_kozos.Dtos.BasketProduct;
import com.example.project_kozos.Dtos.LoginResult;
import com.example.project_kozos.Dtos.Product;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitInterface {
    @POST("/api/auth/login")
    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);

    @POST("/api/auth/register")
    Call<Void> executeSignup(@Body HashMap<String, String> map);

    @GET("/api/products/mobile")
    Call<List<Product>> executeGetall();

    @GET("/api/products/search")
    Call<List<Product>> executeSearchResault(@Query("query") String Products_name);

    @POST("/api/cart/add")
    Call<Void> executeAddCart(@Header("Authorization") String accessToken, @Body HashMap<String, Integer> map);

    @POST("/api/cart/remove")
    Call<Void> executeRemoveCart(@Header("Authorization") String accessToken, @Body HashMap<String, Integer> map);

    @GET("/api/cart/items")
    Call<List<BasketProduct>> executeAllinBasket(@Header("Authorization") String accessToken);

    @GET("/api/cart/total")
    Call<Integer> executeTotalPrice(@Header("Authorization") String accessToken);

    @POST("/api/cart/remove-item")
    Call<Void> executeDeleteItem(@Header("Authorization") String accessToken,@Body HashMap<String, Integer> map);

    @POST("/api/order/new")
    Call<Void> executeOrder(@Header("Authorization") String accessToken,@Body HashMap<String, Object> map);
}