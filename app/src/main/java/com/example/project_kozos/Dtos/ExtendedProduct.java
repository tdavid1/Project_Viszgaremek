package com.example.project_kozos.Dtos;

import java.util.ArrayList;
import java.util.List;

public class ExtendedProduct {
    private Product product;
    private List<ProductPictures> productPictures = new ArrayList<>();

    public ExtendedProduct(Product product, List<ProductPictures> productPictures) {
        this.product = product;
        this.productPictures = productPictures;
    }
    public Product getProducts(){return product;}
    public List<ProductPictures> getProductPictures() {return productPictures;}
}
