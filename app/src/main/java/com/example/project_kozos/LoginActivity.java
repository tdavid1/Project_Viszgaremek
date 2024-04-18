package com.example.project_kozos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText login_email;
    private EditText login_password;
    private Button button;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private TextView login_message;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.login_drawer_layout);
        NavigationView navigationView = findViewById(R.id.login_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.open_nav,R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginDialoge();
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.nav_home){
            Intent intent = new Intent(LoginActivity.this , MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.nav_register){
            Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
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
    private void init(){
        login_email = findViewById(R.id.login_email);
        button = findViewById(R.id.login_button);
        login_password= findViewById(R.id.login_password);
        String baseUrl = NetworkConnection.getBackendUrl();
        if (baseUrl == null) {
            baseUrl = "http://fallbackurl.com";
        }
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        login_message = findViewById(R.id.login_problem_message);
    }
    private void handleLoginDialoge() {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", login_email.getText().toString());
        map.put("password", login_password.getText().toString());

        Call<LoginResult> call = retrofitInterface.executeLogin(map);

        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if(response.isSuccessful()) {
                    LoginResult result = response.body();
                    if (result != null && result.getAccessToken() != null) {
                        String accessToken = result.getAccessToken();

                        AccessTokenManager accessTokenManager = new AccessTokenManager(LoginActivity.this);
                        accessTokenManager.saveAccessToken(accessToken);

                        //setContentView(R.layout.activity_login);
                        //NavigationView navigationView = findViewById(R.id.navigation_view);
                        //Menu menu = navigationView.getMenu();
                        //MenuItem loginMenuItem = menu.findItem(R.id.nav_login);
                        //loginMenuItem.setVisible(false);
                        //loginMenuItem.setEnabled(false);
                        //MenuItem regMenuItem = menu.findItem(R.id.nav_register);
                        //regMenuItem.setVisible(false);
                        //regMenuItem.setEnabled(false);

                        Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        login_message.setText("Invalid response from server");
                    }
                } else if(response.code() == 400) {
                    login_message.setText("Invalid email or password");
                } else {
                    login_message.setText("Unexpected error: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                login_message.setText(t.getMessage());
            }
        });
    }
}