package com.example.project_kozos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductBasketActivity extends AppCompatActivity {
    private TextView basket_total_price;
    private TextView basket_number;
    private Button order;
    private ListView basket_list;
    private Retrofit retrofit;
    private List<BasketProduct> products = new ArrayList<>();
    private RetrofitInterface retrofitInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_basket);
        init();
        GetAll();
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductBasketActivity.this , VerificationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void GetAll(){
        products.clear();
        AccessTokenManager accessTokenManager = new AccessTokenManager(ProductBasketActivity.this);
        Call<List<BasketProduct>> call = retrofitInterface.executeAllinBasket("Bearer "+accessTokenManager.getAccessToken());
        call.enqueue(new Callback<List<BasketProduct>>() {
            @Override
            public void onResponse(Call<List<BasketProduct>> call, Response<List<BasketProduct>> response) {
                if (response.isSuccessful()) {
                    products = response.body();
                    if (products != null && !products.isEmpty()) {
                        basket_list.setAdapter(new ProductBasketActivity.ProductsAdapter(products));
                        GetTotalPrice();
                    } else {
                        Log.e("Error", "No products found");
                    }
                } else {
                    Log.e("Error", "Failed to fetch products: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<BasketProduct>> call, Throwable t) {
                Log.e("Error", "Failed to fetch products: " + t.getMessage());
            }
        });
    }
    public void GetTotalPrice(){
        AccessTokenManager accessTokenManager = new AccessTokenManager(ProductBasketActivity.this);
        Call<Integer> call = retrofitInterface.executeTotalPrice("Bearer "+accessTokenManager.getAccessToken());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Integer helper = response.body();
                    if (helper != null) {
                        basket_total_price.setText(String.valueOf(helper)+" Ft");
                    } else {
                        Log.e("Error", "No products found");
                    }
                } else {
                    Log.e("Error", "Failed to fetch products: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("Error", "Failed to fetch products: " + t.getMessage());
            }
        });
    }
    private class ProductsAdapter extends ArrayAdapter<BasketProduct> {
        private List<BasketProduct> productList;
        public ProductsAdapter(List<BasketProduct> products) {
            super(ProductBasketActivity.this, R.layout.list_adapter, products);
            this.productList = products;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.basket_adapter, parent, false);

            TextView name = view.findViewById(R.id.basket_name);
            TextView price = view.findViewById(R.id.basket_price);
            TextView number = view.findViewById(R.id.basket_product_number);
            Button plus = view.findViewById(R.id.basket_plusz);
            Button minusz = view.findViewById(R.id.basket_minusz);
            ImageButton trash = view.findViewById(R.id.basket_trash);
            if (productList != null && position < productList.size()) {
                final BasketProduct prod = productList.get(position);
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Integer> map = new HashMap<>();
                        map.put("productId", prod.getProduct().getProduct_id());
                        map.put("quantity", 1);
                        AccessTokenManager accessTokenManager = new AccessTokenManager(ProductBasketActivity.this);

                        HashMap<String, HashMap<String, Integer>> requestData = new HashMap<>();
                        requestData.put("data", map);
                        Call<Void> call = retrofitInterface.executeAddCart("Bearer "+accessTokenManager.getAccessToken(), map);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code()==201){
                                    GetAll();
                                    Toast.makeText(ProductBasketActivity.this, "Sikeresen hozzáadta a terméket a kosárhoz", Toast.LENGTH_SHORT).show();
                                }
                                else if(response.code() != 201){
                                    Toast.makeText(ProductBasketActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(ProductBasketActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();;
                            }
                        });
                    }
                });
                minusz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Integer> map = new HashMap<>();
                        map.put("productId", prod.getProduct().getProduct_id());
                        map.put("quantity", 1);
                        AccessTokenManager accessTokenManager = new AccessTokenManager(ProductBasketActivity.this);

                        HashMap<String, HashMap<String, Integer>> requestData = new HashMap<>();
                        requestData.put("data", map);
                        Call<Void> call = retrofitInterface.executeRemoveCart("Bearer "+accessTokenManager.getAccessToken(), map);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code()==201){
                                    GetAll();
                                    Toast.makeText(ProductBasketActivity.this, "Sikeresen Kivonta a terméket a kosárhoz", Toast.LENGTH_SHORT).show();
                                }
                                else if(response.code() != 201){
                                    Toast.makeText(ProductBasketActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(ProductBasketActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();;
                            }
                        });
                    }
                });
                trash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Integer> map = new HashMap<>();
                        map.put("productId", prod.getProduct().getProduct_id());
                        AccessTokenManager accessTokenManager = new AccessTokenManager(ProductBasketActivity.this);
                        Call<Void> call = retrofitInterface.executeDeleteItem("Bearer "+accessTokenManager.getAccessToken(), map);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code()==201){
                                    GetAll();
                                    Toast.makeText(ProductBasketActivity.this, "Sikeresen Terméket a terméket a kosárból", Toast.LENGTH_SHORT).show();
                                }
                                else if(response.code() != 201){
                                    Toast.makeText(ProductBasketActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(ProductBasketActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();;
                            }
                        });
                    }
                });
                name.setText(prod.getProduct().getProduct_name());
                price.setText(String.valueOf(prod.getProduct().getPrice())+" Ft");
                number.setText(String.valueOf(prod.getQuantity()));
            }
            return view;
        }
    }
    public void init(){
        basket_number = findViewById(R.id.basket_total_price);
        basket_total_price = findViewById(R.id.basket_total_price);
        order = findViewById(R.id.order);
        basket_list = findViewById(R.id.listview_basket);
        String baseUrl = NetworkConnection.getBackendUrl();
        if (baseUrl == null) {
            baseUrl = "http://fallbackurl.com";
        }
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }
}