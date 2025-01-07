package com.example.shoppinglistapp.data.model;

public class Task {
    private String name;
    private double price;
    private boolean completed;  // Added the completed field

    // Constructor with completed field
    public Task(String name, double price, boolean completed) {
        this.name = name;
        this.price = price;
        this.completed = completed;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
