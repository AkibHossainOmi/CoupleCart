package com.example.shoppinglistapp.models;

public class Task {
    private String id;
    private String name;
    private double price;
    private boolean completed;
    private String monthYear; // New field for storing the selected month-year

    public Task(String id, String name, double price, boolean completed, String monthYear) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.completed = completed;
        this.monthYear = monthYear; // Initialize monthYear
    }

    public Task() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getMonthYear() {
        return monthYear; // Getter for monthYear
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear; // Setter for monthYear
    }
}
