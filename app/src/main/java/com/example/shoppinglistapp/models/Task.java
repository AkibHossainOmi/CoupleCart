package com.example.shoppinglistapp.models;

public class Task {
    private String id;
    private String name;
    private double price;
    private boolean completed;

    public Task(String id, String name, double price, boolean completed) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.completed = completed;
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
}
