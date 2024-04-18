package com.example.project_kozos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerificationActivity extends AppCompatActivity {

    private Button next;
    private Button back;
    private EditText country;
    private EditText street;
    private EditText state;
    private EditText city;
    private Button back_main;
    private Button order;
    private LinearLayout linearLayout_data;
    private LinearLayout linearLayout_end;
    private EditText house_number;
    private TextView order_place;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private boolean card = false;
    private Address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        init();
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_order();
            }
        });
        back_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_end.setVisibility(View.GONE);
                linearLayout_data.setVisibility(View.VISIBLE);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardcard();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backwardcard();
            }
        });
    }
    public void forwardcard(){
        if(card==false && elenorzes()){
            card =true;
            rewrite();
        } else if (card==true && elenorzes()) {
            linearLayout_end.setVisibility(View.VISIBLE);
            linearLayout_data.setVisibility(View.GONE);
        }
    }
    public void backwardcard(){
        if(card==false){
            Toast.makeText(this, "Nem lehet visszább menni", Toast.LENGTH_SHORT).show();
        }
        else if (card== true) {
            card = false;
            fromCard();
        }
    }
    public boolean elenorzes(){
        if(country.getText().toString().equals("") ||
                state.getText().toString().equals("") ||
                city.getText().toString().equals("") ||
                street.getText().toString().equals("") ||
                house_number.getText().toString().equals("")){
            return false;
        }
        else{
            return true;
        }
    }
    public void rewrite(){
        if(address == null){
            address = new Address(country.getText().toString(),state.getText().toString(),city.getText().toString(),street.getText().toString(),house_number.getText().toString());
        }
        else{
            address.setCountry(country.getText().toString());
            address.setCity(city.getText().toString());
            address.setState(state.getText().toString());
            address.setStreet(street.getText().toString());
            address.setHouse_number(house_number.getText().toString());
        }
        country.setHint("Kártyaszám*");
        country.setText("");
        country.setInputType(InputType.TYPE_CLASS_NUMBER);
        state.setHint("Lejárati idő*");
        street.setText("");
        state.setText("");
        city.setHint("Tulajdonos neve*");
        city.setText("");
        street.setHint("CVC*");
        house_number.setText("");
        street.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
    public void fromCard(){
        country.setHint("Ország*");
        country.setText(address.getCountry());
        country.setInputType(InputType.TYPE_CLASS_TEXT);
        state.setHint("Megye*");
        state.setText(address.getState());
        city.setHint("Város*");
        city.setText(address.getCity());
        street.setHint("Utca*");
        street.setText(address.getStreet());
        street.setInputType(InputType.TYPE_CLASS_TEXT);
        house_number.setText(address.getHouse_number());
    }
    public void make_order(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("paymentType", "20");
        map.put("address", address);
        AccessTokenManager accessTokenManager = new AccessTokenManager(VerificationActivity.this);

        Call<Void> call = retrofitInterface.executeOrder("Bearer "+accessTokenManager.getAccessToken(), map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==201){
                    Toast.makeText(VerificationActivity.this, "Sikeresen hozzáadta a terméket a kosárhoz", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerificationActivity.this , MainActivity.class);
                    startActivity(intent);
                    finish();

                }
                else if(response.code() != 201){
                    Toast.makeText(VerificationActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(VerificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();;
            }
        });
    }
    public void init(){
        String baseUrl = NetworkConnection.getBackendUrl();
        if (baseUrl == null) {
            baseUrl = "http://fallbackurl.com";
        }
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);
        country = findViewById(R.id.country);
        back_main = findViewById(R.id.back_main);
        order = findViewById(R.id.finalyzing);
        linearLayout_data = findViewById(R.id.linearlayout_data);
        linearLayout_end = findViewById(R.id.linearlayout_end);
        state = findViewById(R.id.state);
        street = findViewById(R.id.streat);
        city = findViewById(R.id.city);
        house_number = findViewById(R.id.house_number);
        order_place = findViewById(R.id.order_place);
    }
}