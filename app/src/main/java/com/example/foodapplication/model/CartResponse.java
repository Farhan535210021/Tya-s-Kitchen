package com.example.foodapplication.model;

public class CartResponse {
    private int keranjangID;
    private int user_id;
    private int produk_id;
    private String name;  // Product name
    private double harga_produk;

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }

    public int getQty_produk() {
        return qty_produk;
    }

    public void setQty_produk(int qty_produk) {
        this.qty_produk = qty_produk;
    }

    public double getHarga_produk() {
        return harga_produk;
    }

    public void setHarga_produk(double harga_produk) {
        this.harga_produk = harga_produk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProduk_id() {
        return produk_id;
    }

    public void setProduk_id(int produk_id) {
        this.produk_id = produk_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getKeranjangID() {
        return keranjangID;
    }

    public void setKeranjangID(int keranjangID) {
        this.keranjangID = keranjangID;
    }

    private int qty_produk;
    private double totalHarga;

    // Getters and Setters
}
