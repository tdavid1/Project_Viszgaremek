package com.example.project_kozos;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int product_id;
    private String product_name;
    private String product_type;
    private String product_spectype;
    private int price;
    private String description;
    private List<ProductPictures> productPictures = new ArrayList<>();

    public Product(int productId, String productName, String productType, String productSpectype, int price, String description, List<ProductPictures> productPictures) {
        product_id = productId;
        product_name = productName;
        product_type = productType;
        product_spectype = productSpectype;
        this.price = price;
        this.description = description;
        this.productPictures = productPictures;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_type() {
        return product_type;
    }

    public String getProduct_spectype() {
        return product_spectype;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
    public List<ProductPictures> getProductPictures() {return productPictures;}

}