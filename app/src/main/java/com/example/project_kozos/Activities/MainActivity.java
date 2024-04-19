package com.example.project_kozos.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.project_kozos.AccessTokenManager;
import com.example.project_kozos.Dtos.Product;
import com.example.project_kozos.Dtos.ProductPictures;
import com.example.project_kozos.Dtos.ProductinBasket;
import com.example.project_kozos.R;
import com.example.project_kozos.RetrofitClient;
import com.example.project_kozos.RetrofitInterface;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private List<Product> products = new ArrayList<>();
    private RetrofitInterface retrofitInterface;
    private Button searchButton;
    private EditText searchEditText;
    private ListView listView;
    private final List<ProductinBasket> productInBasketList = new ArrayList<>();
    private ArrayAdapter<Product> productsAdapter;

    private final TextWatcher searchTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String searchText = s.toString();
            if (searchText.isEmpty()) {
                getAllProducts();
            } else {
                searchProducts(searchText);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getAllProducts();
        searchButton.setOnClickListener(v -> {
            if (searchEditText.getText().toString().isEmpty()) {
                getAllProducts();
            } else {
                searchProducts(searchEditText.getText().toString());
            }
        });
        searchEditText.addTextChangedListener(searchTextChangedListener);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        AccessTokenManager accessTokenManager = new AccessTokenManager(MainActivity.this);
        if (item.getItemId() == R.id.nav_basket) {
            Intent intent = new Intent(MainActivity.this , ProductBasketActivity.class);
            ProductinBasket[] productArray = productInBasketList.toArray(new ProductinBasket[0]);
            intent.putExtra("productfrommaintobasket",productArray);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.nav_login) {
            Intent intent = new Intent(MainActivity.this , LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.nav_register) {
            Intent intent = new Intent(MainActivity.this , RegisterActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.nav_logout) {
            accessTokenManager.clearAccessToken();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class ProductsAdapter extends ArrayAdapter<Product> {
        private final List<Product> productList;

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
            Button viewButton = view.findViewById(R.id.viewProductButton);
            ImageView basket = view.findViewById(R.id.addToCartButton);
            ImageView productImg = view.findViewById(R.id.image_products);

            if (productList != null && position < productList.size()) {
                final Product prod = productList.get(position);

                viewButton.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), ProductViewActivity.class);
                    intent.putExtra("product_in_detailed", prod);
                    getContext().startActivity(intent);
                });

                basket.setOnClickListener(v -> {
                    HashMap<String, Integer> map = new HashMap<>();
                    map.put("productId", prod.getProduct_id());
                    map.put("quantity", 1);
                    AccessTokenManager accessTokenManager = new AccessTokenManager(MainActivity.this);

                    HashMap<String, HashMap<String, Integer>> requestData = new HashMap<>();
                    requestData.put("data", map);
                    Call<Void> call = retrofitInterface.executeAddCart("Bearer " + accessTokenManager.getAccessToken(), map);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.code() == 201) {
                                Toast.makeText(MainActivity.this, "Sikeresen hozzáadta a terméket a kosárhoz", Toast.LENGTH_SHORT).show();
                            } else {
                                if (accessTokenManager.getAccessToken() == null) {
                                    Toast.makeText(MainActivity.this, "A kosárba helyezéshez be kell jelentkezned!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "A kosárhoz adás sikertelen volt, kérjük próbálja újra később!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                name.setText(prod.getProduct_name());
                price.setText(prod.getPrice() + "Ft");

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
                            .into(productImg);
                } else {
                    // Handle case when there's no image available
                    productImg.setImageResource(R.drawable.loading);
                }
            }
            return view;
        }
    }

    public void getAllProducts() {
        products.clear();
        Call<List<Product>> call = retrofitInterface.executeGetall();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    products = response.body();
                    if (products != null && !products.isEmpty()) {
                        productsAdapter = new ProductsAdapter(products);
                        listView.setAdapter(productsAdapter);
                    } else {
                        Log.e("Error", "No products found");
                    }
                } else {
                    Log.e("Error", "Failed to fetch products: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                Log.e("Error", "Failed to fetch products: " + t.getMessage());
            }
        });
    }

    public void searchProducts(String searchText) {
        products.clear();
        Call<List<Product>> call = retrofitInterface.executeSearchResault(searchText);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    products = response.body();
                    if (products != null && !products.isEmpty()) {
                        productsAdapter = new ProductsAdapter(products);
                        listView.setAdapter(productsAdapter);
                    } else {
                        Log.e("Error", "No products found");
                    }
                } else {
                    Log.e("Error", "Failed to fetch products: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                Log.e("Error", "Failed to fetch products: " + t.getMessage());
            }
        });
    }

    public void navbarstart() {
        AccessTokenManager accessTokenManager = new AccessTokenManager(MainActivity.this);
    }

    public void init() {
        Retrofit retrofit = RetrofitClient.getClient();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        listView = findViewById(R.id.listview_main);
        searchEditText = findViewById(R.id.Kereseset_eszkoz_editext);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navbarstart();
    }
}
