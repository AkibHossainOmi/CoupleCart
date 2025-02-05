package com.example.shoppinglistapp.utils;

import com.example.shoppinglistapp.models.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference taskRef = database.getReference("tasks");

    public static DatabaseReference getTaskReference() {
        return taskRef;
    }

    public static void addTask(Task task) {
        String taskId = taskRef.push().getKey();
        if (taskId != null) {
            task.setId(taskId);
            taskRef.child(taskId).setValue(task);
        }
    }

    public static void updateTask(Task task) {
        taskRef.child(task.getId()).setValue(task);
    }

    public static void removeTask(String taskId) {
        taskRef.child(taskId).removeValue();
    }
}
