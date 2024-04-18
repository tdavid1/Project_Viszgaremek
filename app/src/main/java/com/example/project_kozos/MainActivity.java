package com.example.project_kozos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private Retrofit retrofit;
    private List<Product> products = new ArrayList<>();
    private RetrofitInterface retrofitInterface;
    private TextView hibajelentes;
    private Button Search;
    private EditText search_edit_text;
    private ListView listView;
    private List<ProductinBasket> productinBasketList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        GetAll();

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search_edit_text.toString().isEmpty()){
                    GetAll();
                } else {
                    Search_Result();
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        AccessTokenManager accessTokenManager = new AccessTokenManager(MainActivity.this);
        if(item.getItemId()==R.id.nav_basket){
            Intent intent = new Intent(MainActivity.this , ProductBasketActivity.class);
            ProductinBasket[] productArray = productinBasketList.toArray(new ProductinBasket[productinBasketList.size()]);
            intent.putExtra("productfrommaintobasket",productArray);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.nav_login){
            Intent intent = new Intent(MainActivity.this , LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.nav_register){
            Intent intent = new Intent(MainActivity.this , RegisterActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId()==R.id.nav_logout) {
            accessTokenManager.clearAccessToken();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private class ProductsAdapter extends ArrayAdapter<Product> {
        private List<Product> productList;

        public ProductsAdapter(List<Product> products) {
            super(MainActivity.this, R.layout.list_adapter, products);
            this.productList = products;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.list_adapter, parent, false);

            TextView name = view.findViewById(R.id.Name);
            TextView price = view.findViewById(R.id.Price);
            Button new_view = view.findViewById(R.id.viewProductButton);
            ImageView basket = view.findViewById(R.id.addToCartButton);
            ImageView productimg = view.findViewById(R.id.image_products);

            if (productList != null && position < productList.size()) {
                final Product prod = productList.get(position);

                new_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ProductViewActivity.class);
                        intent.putExtra("product_in_detailed", prod);
                        getContext().startActivity(intent);
                    }
                });

                basket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Integer> map = new HashMap<>();
                        map.put("productId", prod.getProduct_id());
                        map.put("quantity", 1);
                        AccessTokenManager accessTokenManager = new AccessTokenManager(MainActivity.this);

                        HashMap<String, HashMap<String, Integer>> requestData = new HashMap<>();
                        requestData.put("data", map);
                        Call<Void> call = retrofitInterface.executeAddCart("Bearer "+accessTokenManager.getAccessToken(), map);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.code()==201){
                                    Toast.makeText(MainActivity.this, "Sikeresen hozzáadta a terméket a kosárhoz", Toast.LENGTH_SHORT).show();
                                }
                                else if(response.code() != 201){
                                    Toast.makeText(MainActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();;
                            }
                        });
                    }
                });

                name.setText(prod.getProduct_name());
                price.setText(String.valueOf(prod.getPrice()) + "Ft");

                List<ProductPictures> productPicturesList = prod.getProductPictures();
                if (productPicturesList != null && !productPicturesList.isEmpty()) {
                    ProductPictures productPicture = productPicturesList.get(0);
                    String imageUri = productPicture.getImage();

                    // Load image with Picasso
                    Picasso.get()
                            .load(imageUri)
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.loading)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE) // Disable cache
                            .into(productimg);
                } else {
                    // Handle case when there's no image available
                    productimg.setImageResource(R.drawable.loading);
                }
            }
            return view;
        }
    }

    public void GetAll(){
        products.clear();
        Call<List<Product>> call = retrofitInterface.executeGetall();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    products = response.body();
                    if (products != null && !products.isEmpty()) {
                        listView.setAdapter(new ProductsAdapter(products));
                    } else {
                        Log.e("Error", "No products found");
                    }
                } else {
                    Log.e("Error", "Failed to fetch products: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("Error", "Failed to fetch products: " + t.getMessage());
            }
        });
    }

    public void Search_Result(){
        products.clear();
        String Helper = search_edit_text.getText().toString();
        Call<List<Product>> call = retrofitInterface.executeSearchResault(Helper);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    products = response.body();
                    if (products != null && !products.isEmpty()) {
                        listView.setAdapter(new ProductsAdapter(products));
                    } else {
                        Log.e("Error", "No products found");
                    }
                } else {
                    Log.e("Error", "Failed to fetch products: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("Error", "Failed to fetch products: " + t.getMessage());
            }
        });
    }

    public void navbarstart(){
        AccessTokenManager accessTokenManager = new AccessTokenManager(MainActivity.this);

    }

    public void init(){
        String baseUrl = NetworkConnection.getBackendUrl();
        if (baseUrl == null) {
            baseUrl = "http://fallbackurl.com";
        }
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        listView = findViewById(R.id.listview_main);
        Search = findViewById(R.id.Kereses_button);
        search_edit_text = findViewById(R.id.Kereseset_eszkoz_editext);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Remove the ActionBarDrawerToggle initialization

        navbarstart();
    }
}
