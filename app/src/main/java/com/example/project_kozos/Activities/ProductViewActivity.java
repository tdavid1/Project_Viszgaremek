package com.example.project_kozos.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_kozos.AccessTokenManager;
import com.example.project_kozos.Dtos.Product;
import com.example.project_kozos.Dtos.ProductPictures;
import com.example.project_kozos.R;
import com.example.project_kozos.RetrofitClient;
import com.example.project_kozos.RetrofitInterface;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductViewActivity extends AppCompatActivity {
    private TextView product_name;
    private ImageButton Left_arrow;
    private ImageButton Right_arrow;
    private ImageView product_picture;
    private Button product_minus;
    private ImageButton product_plus;
    private ImageView add_basket;
    private TextView product_price;
    private TextView product_description;
    private Product product;
    private RetrofitInterface retrofitInterface;
    private int what_picture = 0;
    private Button return_button;
    private TextView product_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product_in_detailed");
        init();
        return_button.setOnClickListener(v -> {
            Intent intent1 = new Intent(ProductViewActivity.this , MainActivity.class);
            startActivity(intent1);
            finish();
        });
        Left_arrow.setOnClickListener(v -> {
            if(what_picture==0){
                what_picture = product.getProductPictures().size()-1;
                picture();
            }
            else{
                what_picture=what_picture-1;
                picture();
            }
        });
        Right_arrow.setOnClickListener(v -> {
            if(what_picture==product.getProductPictures().size()-1){
                what_picture = 0;
                picture();
            }
            else{
                what_picture=what_picture+1;
                picture();
            }
        });
        product_plus.setOnClickListener(v -> {
            String help =String.valueOf(Integer.parseInt(product_number.getText().toString())+1);
            product_number.setText(help);
        });
        product_minus.setOnClickListener(v -> {
            if(0==Integer.parseInt(product_number.getText().toString())-1){
                Toast.makeText(ProductViewActivity.this, "A minimum érték 1", Toast.LENGTH_SHORT).show();
            }
            else{
                String help =String.valueOf(Integer.parseInt(product_number.getText().toString())-1);
                product_number.setText(help);
            }
        });
        add_basket.setOnClickListener(v -> {
            int help = Integer.parseInt(product_number.getText().toString());
            HashMap<String, Integer> map = new HashMap<>();
            map.put("productId", product.getProduct_id());
            map.put("quantity", help);
            AccessTokenManager accessTokenManager = new AccessTokenManager(ProductViewActivity.this);

            HashMap<String, HashMap<String, Integer>> requestData = new HashMap<>();
            requestData.put("data", map);
            Call<Void> call = retrofitInterface.executeAddCart("Bearer "+accessTokenManager.getAccessToken(), map);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if(response.code()==201){
                        Toast.makeText(ProductViewActivity.this, "Sikeresen hozzáadta a terméket a kosárhoz", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (accessTokenManager.getAccessToken() == null) {
                            Toast.makeText(ProductViewActivity.this, "A kosárba helyezéshez be kell jelentkezned!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductViewActivity.this, "A kosárhoz adás sikertelen volt, kérjük próbálja újra később!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(ProductViewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    public void init(){
        product_name = findViewById(R.id.product_name);
        Left_arrow = findViewById(R.id.Left_arrow);
        Retrofit retrofit = RetrofitClient.getClient();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Right_arrow = findViewById(R.id.Right_arrow);
        product_picture = findViewById(R.id.product_picture);
        product_minus = findViewById(R.id.product_minus);
        product_plus = findViewById(R.id.product_plus);
        return_button = findViewById(R.id.return_button);
        add_basket = findViewById(R.id.add_basket);
        product_price = findViewById(R.id.product_price);
        product_description = findViewById(R.id.product_description);
        product_description.setMovementMethod(new ScrollingMovementMethod());
        product_number =findViewById(R.id.product_number);
        fill();
    }
    public void fill(){
        product_name.setText(product.getProduct_name());
        product_description.setText(product.getDescription());
        product_price.setText(product.getPrice()+" Ft");
        picture();
    }
    public void picture(){
        ProductPictures productPicturesList = product.getProductPictures().get(what_picture);
        String imageUri = productPicturesList.getImage();
        Picasso.get().load(imageUri).placeholder(R.drawable.loading).error(R.drawable.loading).into(product_picture);
    }
}