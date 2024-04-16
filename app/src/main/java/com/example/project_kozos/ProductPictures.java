package com.example.project_kozos;

import java.io.Serializable;

public class ProductPictures implements Serializable {
    private int id;
    private String image;
    private int productId;

    public ProductPictures(int id, String image, int productid) {
        this.id = id;
        this.image = image;
        this.productId = productid;
    }

    public int getId(){ return id;}
    public String getImage(){ return image;}
    public int getProductid(){ return productId;}
}
