// Category.java
package com.example.foodapplication.model;

public class Category {
    private int id;
    private String name;
    private int icon; // Drawable resource ID

    public Category(int id, String name, int icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
}
