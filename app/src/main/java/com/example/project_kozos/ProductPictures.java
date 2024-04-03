package com.example.project_kozos;

public class ProductPictures {
    private int id;
    private String image;
    private int productid;

    public ProductPictures(int id, String image, int productid) {
        this.id = id;
        this.image = image;
        this.productid = productid;
    }

    public int getId(){ return id;}
    public String getImage(){ return image;}
    public int getProductid(){ return productid;}
}
