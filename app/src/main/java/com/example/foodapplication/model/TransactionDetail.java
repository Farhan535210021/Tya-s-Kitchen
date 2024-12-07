package com.example.foodapplication.model;

import com.google.gson.annotations.SerializedName;

public class TransactionDetail {

    @SerializedName("nama_produk")
    private String namaProduk;

    @SerializedName("qty_produk")
    private int qtyProduk;

    @SerializedName("total_harga")
    private double totalHarga;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @SerializedName("category_id") // Tambahkan ini
    private int categoryId;
    // Getters and setters
    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }

    public int getQtyProduk() { return qtyProduk; }
    public void setQtyProduk(int qtyProduk) { this.qtyProduk = qtyProduk; }

    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }
}
