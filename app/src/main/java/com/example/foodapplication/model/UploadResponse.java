package com.example.foodapplication.model;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {

    @SerializedName("image_url")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
