package com.example.shoppinglistapp.domain.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class TaskModel {
    private String id; // Unique identifier for the task (optional but useful)
    private String name;
    private double price;
    private boolean completed; // Tracks task completion

    // No-argument constructor for Firebase or serialization
    public TaskModel() {
    }

    // Constructor with parameters
    public TaskModel(String id, String name, double price, boolean completed) {
        this.id = id; // Initialize the ID field
        this.name = name;
        this.price = price;
        this.completed = completed;
    }

    // Constructor without ID (for compatibility)
    public TaskModel(String name, double price, boolean completed) {
        this(null, name, price, completed); // Call the main constructor
    }

    // Getter and Setter for ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter for Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for Price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getter and Setter for Completed Status
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Override toString for debugging/logging
    @NonNull
    @Override
    public String toString() {
        return "TaskModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", completed=" + completed +
                '}';
    }

    // Override equals and hashCode to compare tasks based on ID, name, price, and completed status
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskModel task = (TaskModel) o;
        return Double.compare(task.price, price) == 0 &&
                completed == task.completed &&
                Objects.equals(id, task.id) &&
                Objects.equals(name, task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, completed);
    }
}
