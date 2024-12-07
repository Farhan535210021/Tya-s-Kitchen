package com.example.foodapplication.model;

import com.google.gson.annotations.SerializedName;

public class MostViewed {
    private int id;
    private String name;
    private String description;
    private double price;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("average_rating") // Ensure this matches the JSON response field
    private double averageRating;

    private int views;

    @SerializedName("category_id")
    private int categoryId;

    // Constructor
    public MostViewed(int id, String name, String description, String imageUrl, int views, double price, double averageRating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.views = views;
        this.price = price;
        this.averageRating = averageRating;
    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
