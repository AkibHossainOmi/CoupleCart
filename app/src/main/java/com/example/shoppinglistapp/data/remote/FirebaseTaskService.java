package com.example.shoppinglistapp.data.remote;

import com.example.shoppinglistapp.data.model.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseTaskService {
    private final DatabaseReference databaseTasks;

    public FirebaseTaskService() {
        this.databaseTasks = FirebaseDatabase.getInstance().getReference("tasks");
    }

    public void saveTask(String taskId, Task task) {
        databaseTasks.child(taskId).setValue(task);
    }

    public void updateTask(String taskId, Task task) {
        databaseTasks.child(taskId).setValue(task);
    }

    public void removeTask(String taskId) {
        databaseTasks.child(taskId).removeValue();
    }

    public DatabaseReference getTasksReference() {
        return databaseTasks;
    }
}
