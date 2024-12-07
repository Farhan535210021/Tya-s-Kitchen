package com.example.foodapplication.model;

import com.google.gson.annotations.SerializedName;

public class Produk {

    @SerializedName("id")
    private Integer id;

    @SerializedName("produk_id")
    private Integer produk_id;

    public Integer getProduk_id() {
        return produk_id;
    }

    public void setProduk_id(Integer produk_id) {
        this.produk_id = produk_id;
    }

    @SerializedName("average_rating")
    private double averageRating;

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }


    @SerializedName("ratingproduk")
    private Integer ratingProduk;

    @SerializedName("deskripsi_rating")
    private String deskripsiRating;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("category_id")
    private int category_id;

    @SerializedName("image_url")
    private String image_url;

    private int qty_in_cart;  // Jumlah dalam keranjang

    @SerializedName("views")
    private int views;

    // Constructor
    public Produk(int id, String name, String description, double price, int category_id, String image_url, int qty_in_cart, int views) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category_id = category_id;
        this.image_url = image_url;
        this.qty_in_cart = qty_in_cart;
        this.views = views;
    }

    // Getter dan Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public int getQty_in_cart() {
        return qty_in_cart;
    }

    public void setQty_in_cart(int qty_in_cart) {
        this.qty_in_cart = qty_in_cart;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Integer getRatingProduk() {
        return ratingProduk;
    }

    public void setRatingProduk(Integer ratingProduk) {
        this.ratingProduk = ratingProduk;
    }

    public String getDeskripsiRating() {
        return deskripsiRating;
    }

    public void setDeskripsiRating(String deskripsiRating) {
        this.deskripsiRating = deskripsiRating;
    }
}
