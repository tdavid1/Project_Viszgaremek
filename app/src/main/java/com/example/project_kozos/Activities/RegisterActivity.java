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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.project_kozos.R;
import com.example.project_kozos.RetrofitClient;
import com.example.project_kozos.RetrofitInterface;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private EditText signup_email;
    private EditText signup_password;
    private EditText signup_name;
    private Button signupButton;
    private RetrofitInterface retrofitInterface;
    private TextView signup_message;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        Toolbar toolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.register_drawer_layout);
        NavigationView navigationView = findViewById(R.id.register_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.open_nav,R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUpDialoge();
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.nav_home){
            Intent intent = new Intent(RegisterActivity.this , MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.nav_login){
            Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
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
    private void handleSignUpDialoge() {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", signup_name.getText().toString());
        map.put("email", signup_email.getText().toString());
        map.put("password", signup_password.getText().toString());

        Call<Void> call = retrofitInterface.executeSignup(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                signup_message.setText("siker");
                if(response.code()==201){
                    signup_message.setText("Sikeres Regisztáció");
                    signup_email.setText("");
                    signup_name.setText("");
                    signup_password.setText("");
                }
                else if(response.code() == 400){
                    signup_message.setText("Ezzel az email címmel már regisztráltak");
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                signup_message.setText(t.getMessage());
            }
        });
    }
    private void init(){
        signupButton = findViewById(R.id.register_button);
        signup_email = findViewById(R.id.user_email);
        signup_name = findViewById(R.id.user_name);
        signup_password=findViewById(R.id.password);
        Retrofit retrofit = RetrofitClient.getClient();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        signup_message = findViewById(R.id.signup_problem_message);
    }
}