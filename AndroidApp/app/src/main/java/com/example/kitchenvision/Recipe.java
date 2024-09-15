package com.example.kitchenvision;

public class Recipe {
    private int image;
    private String name;
    private String description;
    private String ingredient;
    private String instructions;

    public Recipe(int image, String name, String description, String ingredient, String instructions) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.ingredient = ingredient;
        this.instructions = instructions;
    }

    // Getters and Setters for the attributes
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
