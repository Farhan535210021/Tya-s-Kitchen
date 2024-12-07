package com.example.foodapplication.model;

public class Trending {

    private String placeName;
    private String recom;
    private int imageThumb; // Drawable resource id

    public Trending(String placeName, String recom, int imageThumb) {
        this.placeName = placeName;
        this.recom = recom;
        this.imageThumb = imageThumb;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getRecom() {
        return recom;
    }

    public int getImageThumb() {
        return imageThumb;
    }
}
