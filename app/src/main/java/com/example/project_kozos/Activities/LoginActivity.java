package com.example.project_kozos.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.project_kozos.AccessTokenManager;
import com.example.project_kozos.Dtos.LoginResult;
import com.example.project_kozos.R;
import com.example.project_kozos.RetrofitClient;
import com.example.project_kozos.RetrofitInterface;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextInputEditText login_email;
    private TextInputEditText login_password;
    private Button button;
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
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        drawerLayout = findViewById(R.id.login_drawer_layout);
        NavigationView navigationView = findViewById(R.id.login_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.open_nav,R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        button.setOnClickListener(v -> handleLoginDialogue());
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
        Retrofit retrofit = RetrofitClient.getClient();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        login_message = findViewById(R.id.login_problem_message);
    }
    private void handleLoginDialogue() {
        HashMap<String, String> map = new HashMap<>();
        map.put("email", Objects.requireNonNull(login_email.getText()).toString());
        map.put("password", Objects.requireNonNull(login_password.getText()).toString());

        Call<LoginResult> call = retrofitInterface.executeLogin(map);

        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(@NonNull Call<LoginResult> call, @NonNull Response<LoginResult> response) {
                if(response.isSuccessful()) {
                    LoginResult result = response.body();
                    if (result != null && result.getAccessToken() != null) {
                        String accessToken = result.getAccessToken();

                        AccessTokenManager accessTokenManager = new AccessTokenManager(LoginActivity.this);
                        accessTokenManager.saveAccessToken(accessToken);
                        Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        assert response.body() != null;
                        login_message.setText(response.body().getErrorMessage());
                    }
                } else {
                    login_message.setText(String.format("%s%s", getString(R.string.unexpected_error), response.message()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<LoginResult> call, @NonNull Throwable t) {
                login_message.setText(t.getMessage());
            }
        });
    }
}