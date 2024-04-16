package com.example.project_kozos;

public class BasketProduct {
    private Product product;
    private int quantity;

    public BasketProduct(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
