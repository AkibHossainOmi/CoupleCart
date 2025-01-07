package com.example.shoppinglistapp.domain.repository;

import com.example.shoppinglistapp.domain.model.TaskModel;

public interface TaskRepositoryInterface {
    void saveTask(String taskId, TaskModel task);
    void updateTask(String taskId, TaskModel task);
    void removeTask(String taskId);
    void getTasks(TaskDataCallback callback);  // Ensure this matches the method in TaskRepository
}
