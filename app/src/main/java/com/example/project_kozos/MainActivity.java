package com.example.project_kozos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private Retrofit retrofit;
    private Products[] products;
    private RetrofitInterface retrofitInterface;
    private String baseUrl = "http://192.168.56.1:3000";
    private LoginResult result;
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

        Call<Products[]> call = retrofitInterface.executeGetall();
        call.enqueue(new Callback<Products[]>() {
            @Override
            public void onResponse(Call<Products[]> call, Response<Products[]> response) {
                if(response.code()==201){
                    products = response.body();
                }
                else if(response.code()==400){
                    Toast.makeText(MainActivity.this, "Hiba a lekérés alatt", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Products[]> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage() , Toast.LENGTH_SHORT).show();
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
    public void init(){
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }
}