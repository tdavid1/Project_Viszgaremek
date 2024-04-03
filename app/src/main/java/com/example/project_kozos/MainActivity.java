package com.example.project_kozos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
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
    private List<ProductPictures> productPictures = new ArrayList<>();
    private String baseUrl = "http://192.168.56.1:3000";
    private LoginResult result;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.open_nav,R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Call<List<Product>> call = retrofitInterface.executeGetall();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    products = response.body();
                    if (products != null && !products.isEmpty()) {
                        for (Product product : products) {
                            listView.setAdapter(new ProductsAdapter());
                            Log.d("Product", product.getProduct_name());
                            productPictures = product.getProductPictures();
                            if (productPictures != null && !productPictures.isEmpty()) {
                                for (ProductPictures picture : productPictures) {
                                    Log.d("Product Picture", picture.getImage());
                                }
                            }
                        }
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.nav_home){
            //getSupportFragmentManager().beginTransaction().replace(R.id.frame_Container, new HomeFragment()).commit();
        }
        else if(item.getItemId()==R.id.nav_basket){
            //getSupportFragmentManager().beginTransaction().replace(R.id.frame_Container, new BasketFragment()).commit();
        }
        else if(item.getItemId()==R.id.nav_login){
            //getSupportFragmentManager().beginTransaction().replace(R.id.frame_Container, login_fragment).commit();
            Intent intent = new Intent(MainActivity.this , LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.nav_profile){
            //getSupportFragmentManager().beginTransaction().replace(R.id.frame_Container, new ProfilFragment()).commit();
        }
        else if(item.getItemId()==R.id.nav_register){
            //getSupportFragmentManager().beginTransaction().replace(R.id.frame_Container, new RegisterFragment()).commit();
            Intent intent = new Intent(MainActivity.this , RegisterActivity.class);
            startActivity(intent);
            finish();
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
        public ProductsAdapter(){
            super(MainActivity.this,R.layout.list_adapter,products);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.list_adapter,null);
            Product prod = products.get(position);
            TextView name = view.findViewById(R.id.Name);
            TextView price = view.findViewById(R.id.Price);
            TextView new_view = view.findViewById(R.id.make_view);
            TextView basket = view.findViewById(R.id.basket);

            name.setText(prod.getProduct_name());
            //hibajelentes.setText(String.valueOf(actualPerson.getProduct_name()));
            price.setText(String.valueOf(prod.getPrice()));
            return view;
        }
    }
    public void init(){
        hibajelentes = findViewById(R.id.problem_message);
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        listView = findViewById(R.id.listview_main);
    }
}