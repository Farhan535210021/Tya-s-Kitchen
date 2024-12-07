package com.example.foodapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Transaksi {

    @SerializedName("alamat")
    private String alamat;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("status_transaksi")
    private String statusTransaksi;

    @SerializedName("status_pembayaran")
    private String statusPembayaran;

    @SerializedName("tanggal_transaksi")
    private String tanggalTransaksi;

    @SerializedName("total_harga")
    private double totalHarga;

    @SerializedName("total_qty")
    private int totalQty;

    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("deskripsi_rating")
    private String deskripsiRating;

    @SerializedName("bintang_rating")
    private int rating;

    // New field for product-specific rating
    @SerializedName("ratingproduk")
    private Integer ratingProduk;

    @SerializedName("product_list")
    private List<Produk> productList;

    @SerializedName("produk_id")
    private Integer produkId;

    @SerializedName("nama_produk")
    private String namaproduk;

    @SerializedName("average_rating")
    private Double averageRating;

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    public String getNamaproduk() {
        return namaproduk;
    }

    public void setNamaproduk(String namaproduk) {
        this.namaproduk = namaproduk;
    }

    public int getProdukId() {
        return produkId;
    }

    public void setProdukId(int produkId) {
        this.produkId = produkId;
    }

    // Getter and Setter for ratingproduk
    public Integer getRatingProduk() {
        return ratingProduk;
    }

    public void setRatingProduk(Integer ratingProduk) {
        this.ratingProduk = ratingProduk;
    }

    // Other Getters and Setters
    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }

    public String getStatusPembayaran() {
        return statusPembayaran;
    }

    public void setStatusPembayaran(String statusPembayaran) {
        this.statusPembayaran = statusPembayaran;
    }

    public String getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(String tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public List<Produk> getProductList() {
        return productList;
    }

    public void setProductList(List<Produk> productList) {
        this.productList = productList;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDeskripsiRating() {
        return deskripsiRating;
    }

    public void setDeskripsiRating(String deskripsiRating) {
        this.deskripsiRating = deskripsiRating;
    }
}
