package com.example.kitchenvision;  // Ensure this matches your package

public class Item {

    private int imageResId;
    private String title;
    private String subtitle;

    // Constructor
    public Item(int imageResId, String title, String subtitle) {
        this.imageResId = imageResId;
        this.title = title;
        this.subtitle = subtitle;
    }

    // Getters for the fields
    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}