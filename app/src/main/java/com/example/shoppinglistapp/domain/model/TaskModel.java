package com.example.shoppinglistapp.domain.model;

import java.util.Objects;

public class TaskModel {
    private String name;
    private double price;
    private boolean completed; // Added to track task completion

    // No-argument constructor for Firebase
    public TaskModel() {
    }

    // Constructor with parameters
    public TaskModel(String name, double price, boolean completed) {
        this.name = name;
        this.price = price;
        this.completed = completed; // Initialize the completed field
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

    // Override the toString method for debugging/logging
    @Override
    public String toString() {
        return "TaskModel{name='" + name + "', price=" + price + ", completed=" + completed + "}";
    }

    // Override equals and hashCode to compare tasks based on name, price, and completed status
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskModel task = (TaskModel) o;
        return Double.compare(task.price, price) == 0 &&
                completed == task.completed &&
                Objects.equals(name, task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, completed);
    }
}
