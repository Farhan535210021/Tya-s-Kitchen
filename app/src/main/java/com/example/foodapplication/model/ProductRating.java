package com.example.foodapplication.model;

public class ProductRating {
    private int produk_id;
    private Double average_rating;

    // Constructor
    public ProductRating(int produk_id, Double average_rating) {
        this.produk_id = produk_id;
        this.average_rating = average_rating;
    }

    // Getters and setters
    public int getProdukId() {
        return produk_id;
    }

    public void setProdukId(int produk_id) {
        this.produk_id = produk_id;
    }

    public Double getAverageRating() {
        return average_rating;
    }

    public void setAverageRating(Double average_rating) {
        this.average_rating = average_rating;
    }
}
