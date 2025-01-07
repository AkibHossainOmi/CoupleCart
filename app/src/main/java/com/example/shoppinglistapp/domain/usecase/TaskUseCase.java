package com.example.shoppinglistapp.domain.usecase;

import com.example.shoppinglistapp.domain.model.TaskModel;
import com.example.shoppinglistapp.domain.repository.TaskRepositoryInterface;
import com.example.shoppinglistapp.domain.repository.TaskDataCallback;

import java.util.UUID;

public class TaskUseCase {

    private final TaskRepositoryInterface taskRepository;

    public TaskUseCase(TaskRepositoryInterface taskRepository) {
        this.taskRepository = taskRepository;
    }

    public String saveTask(TaskModel task) {
        // Generate a unique task ID
        String taskId = UUID.randomUUID().toString();
        taskRepository.saveTask(taskId, task);  // Save the task in the repository
        return taskId;  // Return the generated task ID
    }

    public void updateTask(String taskId, TaskModel task) {
        taskRepository.updateTask(taskId, task);
    }

    public void removeTask(String taskId) {
        taskRepository.removeTask(taskId);
    }

    public void getTasks(TaskDataCallback callback) {
        // Pass the callback to the repository to fetch the tasks
        taskRepository.getTasks(callback);
    }

//    public void notifyNewTaskAdded(String taskName) {
//        PublishOptions publishOptions = new PublishOptions();
//        publishOptions.putHeader("android-ticker-text", "New Task Added");
//        publishOptions.putHeader("android-content-title", "Shopping List");
//        publishOptions.putHeader("android-content-text", "A new task was added: " + taskName);
//
//        Backendless.Messaging.publish("default");
//    }
}
