package com.example.foodapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SalesReport {

    @SerializedName("tahun")
    private String tahun;

    @SerializedName("bulan")
    private String bulan;

    @SerializedName("total_pendapatan")
    private double totalPendapatan;

    @SerializedName("total_pesanan")
    private int totalPesanan;

    @SerializedName("total_produk_terjual")
    private int totalProdukTerjual;

    // Updated to use TransactionGroup instead of TransactionDetail
    @SerializedName("transaction_details")
    private List<TransactionGroup> transactionDetails;

    // Getters and Setters
    public String getTahun() { return tahun; }
    public void setTahun(String tahun) { this.tahun = tahun; }

    public String getBulan() { return bulan; }
    public void setBulan(String bulan) { this.bulan = bulan; }

    public double getTotalPendapatan() { return totalPendapatan; }
    public void setTotalPendapatan(double totalPendapatan) { this.totalPendapatan = totalPendapatan; }

    public int getTotalPesanan() { return totalPesanan; }
    public void setTotalPesanan(int totalPesanan) { this.totalPesanan = totalPesanan; }

    public int getTotalProdukTerjual() { return totalProdukTerjual; }
    public void setTotalProdukTerjual(int totalProdukTerjual) { this.totalProdukTerjual = totalProdukTerjual; }

    public List<TransactionGroup> getTransactionDetails() { return transactionDetails; }
    public void setTransactionDetails(List<TransactionGroup> transactionDetails) { this.transactionDetails = transactionDetails; }
}
