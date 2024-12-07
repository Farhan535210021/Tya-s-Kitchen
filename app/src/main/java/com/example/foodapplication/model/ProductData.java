package com.example.foodapplication.model;

import com.google.gson.annotations.SerializedName;

public class ProductData {
    @SerializedName("nama_produk")
    private String namaProduk;

    @SerializedName("total_qty")
    private int totalQty;


    // Getter dan Setter untuk namaProduk
    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    // Getter dan Setter untuk totalQty
    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }



    @Override
    public String toString() {
        return "ProductData{" +
                "namaProduk='" + namaProduk + '\'' +
                ", totalQty=" + totalQty +
                '}';
    }
}
