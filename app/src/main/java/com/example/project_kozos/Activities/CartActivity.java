package com.example.project_kozos.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_kozos.AccessTokenManager;
import com.example.project_kozos.Dtos.BasketProduct;
import com.example.project_kozos.Dtos.ProductPictures;
import com.example.project_kozos.R;
import com.example.project_kozos.RetrofitClient;
import com.example.project_kozos.RetrofitInterface;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView basket_total_price;
    private Button order;
    private ListView basket_list;
    private List<BasketProduct> products = new ArrayList<>();
    private RetrofitInterface retrofitInterface;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        init();
        GetAll();
        setupNavigationView();
        order.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this , VerificationActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupNavigationView() {
        drawerLayout = findViewById(R.id.cart_drawer_layout);
        NavigationView navigationView = findViewById(R.id.cart_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.cart_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        AccessTokenManager accessTokenManager = new AccessTokenManager(CartActivity.this);
        if (item.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.nav_logout) {
            accessTokenManager.clearAccessToken();
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void GetAll(){
        products.clear();
        AccessTokenManager accessTokenManager = new AccessTokenManager(CartActivity.this);
        Call<List<BasketProduct>> call = retrofitInterface.executeAllinBasket("Bearer "+accessTokenManager.getAccessToken());
        call.enqueue(new Callback<List<BasketProduct>>() {
            @Override
            public void onResponse(@NonNull Call<List<BasketProduct>> call, @NonNull Response<List<BasketProduct>> response) {
                if (response.isSuccessful()) {
                    products = response.body();
                    if (products != null && !products.isEmpty()) {
                        basket_list.setAdapter(new CartActivity.ProductsAdapter(products));
                        GetTotalPrice();
                    } else {
                        Log.e("Error", "No products found");
                    }
                } else {
                    Log.e("Error", "Failed to fetch products: " + response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<BasketProduct>> call, @NonNull Throwable t) {
                Log.e("Error", "Failed to fetch products: " + t.getMessage());
            }
        });
    }
    public void GetTotalPrice(){
        AccessTokenManager accessTokenManager = new AccessTokenManager(CartActivity.this);
        Call<Integer> call = retrofitInterface.executeTotalPrice("Bearer "+accessTokenManager.getAccessToken());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if (response.isSuccessful()) {
                    Integer helper = response.body();
                    if (helper != null) {
                        basket_total_price.setText(helper +" Ft");
                    } else {
                        Log.e("Error", "No products found");
                    }
                } else {
                    Log.e("Error", "Failed to fetch products: " + response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Log.e("Error", "Failed to fetch products: " + t.getMessage());
            }
        });
    }
    private class ProductsAdapter extends ArrayAdapter<BasketProduct> {
        private final List<BasketProduct> productList;
        public ProductsAdapter(List<BasketProduct> products) {
            super(CartActivity.this, R.layout.list_adapter, products);
            this.productList = products;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.cart_adapter, parent, false);

            TextView name = view.findViewById(R.id.textViewProductNameInCart);
            TextView price = view.findViewById(R.id.textViewProductPriceInCart);
            TextView number = view.findViewById(R.id.productCartQuantity);
            ImageView productImg = view.findViewById(R.id.imageViewProductImageInCart);
            Button plus = view.findViewById(R.id.productCartPlus);
            Button minusz = view.findViewById(R.id.productCartMinus);
            Button delete = view.findViewById(R.id.productCartDelete);
            if (productList != null && position < productList.size()) {
                final BasketProduct prod = productList.get(position);
                plus.setOnClickListener(v -> {
                    HashMap<String, Integer> map = new HashMap<>();
                    map.put("productId", prod.getProduct().getProduct_id());
                    map.put("quantity", 1);
                    AccessTokenManager accessTokenManager = new AccessTokenManager(CartActivity.this);

                    HashMap<String, HashMap<String, Integer>> requestData = new HashMap<>();
                    requestData.put("data", map);
                    Call<Void> call = retrofitInterface.executeAddCart("Bearer "+accessTokenManager.getAccessToken(), map);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if(response.isSuccessful()){
                                GetAll();
                            }
                            else {
                                Toast.makeText(CartActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(CartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                minusz.setOnClickListener(v -> {
                    HashMap<String, Integer> map = new HashMap<>();
                    map.put("productId", prod.getProduct().getProduct_id());
                    map.put("quantity", 1);
                    AccessTokenManager accessTokenManager = new AccessTokenManager(CartActivity.this);

                    HashMap<String, HashMap<String, Integer>> requestData = new HashMap<>();
                    requestData.put("data", map);
                    Call<Void> call = retrofitInterface.executeRemoveCart("Bearer "+accessTokenManager.getAccessToken(), map);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if(response.isSuccessful()){
                                GetAll();
                            }
                            else {
                                Toast.makeText(CartActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(CartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                delete.setOnClickListener(v -> {
                    int productId = prod.getProduct().getProduct_id();
                    AccessTokenManager accessTokenManager = new AccessTokenManager(CartActivity.this);
                    HashMap<String, Integer> productIdMap = new HashMap<>();
                    productIdMap.put("productId", productId);
                    Call<Void> call = retrofitInterface.executeDeleteItem("Bearer " + accessTokenManager.getAccessToken(), productIdMap);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Item removed successfully
                                GetAll();
                            } else {
                                // Handle unsuccessful response
                                Toast.makeText(CartActivity.this, "Failed to remove item from cart: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            // Handle failure
                            Toast.makeText(CartActivity.this, "Failed to remove item from cart: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                name.setText(prod.getProduct().getProduct_name());
                price.setText(prod.getProduct().getPrice() + " Ft");
                List<ProductPictures> productPicturesList = prod.getProduct().getProductPictures();
                if (productPicturesList != null && !productPicturesList.isEmpty()) {
                    ProductPictures productPicture = productPicturesList.get(0);
                    String imageUri = productPicture.getImage();

                    Picasso.get()
                            .load(imageUri)
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.loading)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(productImg);
                } else {
                    productImg.setImageResource(R.drawable.loading);
                }
                number.setText(String.valueOf(prod.getQuantity()));
            }
            return view;
        }
    }
    public void init(){
        basket_total_price = findViewById(R.id.basket_total_price);
        order = findViewById(R.id.order);
        basket_list = findViewById(R.id.listview_basket);
        Retrofit retrofit = RetrofitClient.getClient();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }
}