package com.example.foodapplication.model;

public class CartItem {
    private int produk_id;
    private String productName;
    private int quantity;
    private double price;  // Harga satuan
    private double totalPrice;  // Total harga keseluruhan keranjang

    // Getter dan Setter

    public int getProduk_id() {
        return produk_id;
    }

    public void setProduk_id(int produk_id) {
        this.produk_id = produk_id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Tambahkan method ini untuk menghitung total harga produk (harga satuan * quantity)
    public double getTotalProductPrice() {
        return this.price * this.quantity;  // Menghitung total harga produk
    }
}
