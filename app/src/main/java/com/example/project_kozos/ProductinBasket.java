package com.example.project_kozos;

import java.io.Serializable;

public class ProductinBasket implements Serializable {
    private Product product;
    private int sum;

    public ProductinBasket(Product product, int sum) {
        this.product = product;
        this.sum = sum;
    }
    public Product getProduct() {
        return product;
    }

    public int getSum() {
        return sum;
    }
    public void setSum(int sum){
        this.sum=sum;
    }
}
