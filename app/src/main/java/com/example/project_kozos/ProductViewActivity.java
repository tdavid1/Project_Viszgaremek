package com.example.project_kozos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductViewActivity extends AppCompatActivity {
    private TextView product_name;
    private ImageButton Left_arrow;
    private ImageButton Right_arrow;
    private ImageView product_picture;
    private ImageButton product_minus;
    private ImageButton product_plus;
    private TextView product_number;
    private ImageView add_basket;
    private TextView product_price;
    private ProductinBasket productinBasket;
    private String baseUrl = "http://192.168.56.1:3000";
    private TextView product_description;
    private Product product;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private int what_picture = 0;
    private Button return_button;
    private TextView number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product_in_detailed");
        init();
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductViewActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(what_picture==0){
                    what_picture = product.getProductPictures().size()-1;
                    picture();
                }
                else{
                    what_picture=what_picture-1;
                    picture();
                }
            }
        });
        Right_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(what_picture==product.getProductPictures().size()-1){
                    what_picture = 0;
                    picture();
                }
                else{
                    what_picture=what_picture+1;
                    picture();
                }
            }
        });
        product_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String help =String.valueOf(Integer.parseInt(number.getText().toString())+1);
                number.setText(help);
            }
        });
        product_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(0==Integer.parseInt(number.getText().toString())-1){
                    Toast.makeText(ProductViewActivity.this, "Minimum 1 nek kell lennie a terméknek", Toast.LENGTH_SHORT).show();
                }
                else{
                    String help =String.valueOf(Integer.parseInt(number.getText().toString())-1);
                    number.setText(help);
                }
            }
        });
        add_basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int help = Integer.parseInt(number.getText().toString());
                HashMap<String, Integer> map = new HashMap<>();
                map.put("productId", product.getProduct_id());
                map.put("quantity", help);
                AccessTokenManager accessTokenManager = new AccessTokenManager(ProductViewActivity.this);

                HashMap<String, HashMap<String, Integer>> requestData = new HashMap<>();
                requestData.put("data", map);
                Call<Void> call = retrofitInterface.executeAddCart("Bearer "+accessTokenManager.getAccessToken(), map);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code()==201){
                            Toast.makeText(ProductViewActivity.this, "Sikeresen hozzáadta a terméket a kosárhoz", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.code() != 201){
                            Toast.makeText(ProductViewActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ProductViewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();;
                    }
                });
            }
        });
    }
    public void init(){
        product_name = findViewById(R.id.product_name);
        Left_arrow = findViewById(R.id.Left_arrow);
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Right_arrow = findViewById(R.id.Right_arrow);
        product_picture = findViewById(R.id.product_picture);
        product_minus = findViewById(R.id.product_minus);
        product_plus = findViewById(R.id.product_plus);
        product_number = findViewById(R.id.product_number);
        return_button = findViewById(R.id.return_button);
        add_basket = findViewById(R.id.add_basket);
        product_price = findViewById(R.id.product_price);
        product_description = findViewById(R.id.product_description);
        number =findViewById(R.id.product_number);
        productinBasket = new ProductinBasket(product,0);
        fill();
    }
    public void fill(){
        product_name.setText(product.getProduct_name());
        product_description.setText(product.getDescription());
        product_price.setText(product.getPrice()+"Ft");
        picture();
    }
    public void picture(){
        ProductPictures productPicturesList = product.getProductPictures().get(what_picture);
        String imageUri = productPicturesList.getImage();
        Picasso.get().load(imageUri).placeholder(R.drawable.loading).error(R.drawable.loading).into(product_picture);
    }
}