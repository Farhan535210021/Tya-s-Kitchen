package com.example.foodapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderHistory {

    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("total_qty")
    private int totalQty;

    @SerializedName("total_harga")
    private double totalHarga;

    @SerializedName("status_transaksi")
    private String statusTransaksi;

    @SerializedName("tanggal_transaksi")
    private String tanggalTransaksi;

    @SerializedName("product_list")  // Pastikan API juga mengirimkan product_list
    private List<ProdukHistory> productList;  // Daftar produk dalam transaksi

    @SerializedName("alamat")
    private String alamatPengiriman;

    @SerializedName("status_pembayaran")
    private String statusPembayaran;

    // Tambahkan nomorHp untuk menampilkan nomor telepon (khusus admin)
    @SerializedName("nomor_hp")
    private String nomorHp;

    // Getters and Setters

    public String getAlamatPengiriman() {
        return alamatPengiriman;
    }

    public void setAlamatPengiriman(String alamatPengiriman) {
        this.alamatPengiriman = alamatPengiriman;
    }

    public String getStatusPembayaran() {
        return statusPembayaran;
    }

    public void setStatusPembayaran(String statusPembayaran) {
        this.statusPembayaran = statusPembayaran;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }

    public String getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(String tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public List<ProdukHistory> getProductList() {
        return productList;
    }

    public void setProductList(List<ProdukHistory> productList) {
        this.productList = productList;
    }

    public String getNomorHp() {
        return nomorHp;
    }

    public void setNomorHp(String nomorHp) {
        this.nomorHp = nomorHp;
    }
}
