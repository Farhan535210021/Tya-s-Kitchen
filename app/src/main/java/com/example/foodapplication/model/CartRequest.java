package com.example.foodapplication.model;

public class CartRequest {
    private int user_id;
    private int produk_id;
    private double productPrice;
    private int qty_produk;

    // Constructor
    public CartRequest(int user_id, int produk_id, double productPrice, int qty_produk) {
        this.user_id = user_id;
        this.produk_id = produk_id;
        this.productPrice = productPrice;
        this.qty_produk = qty_produk;
    }

    // Getters and Setters
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getProduk_id() {
        return produk_id;
    }

    public void setProduk_id(int produk_id) {
        this.produk_id = produk_id;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getQty_produk() {
        return qty_produk;
    }

    public void setQty_produk(int qty_produk) {
        this.qty_produk = qty_produk;
    }
}
