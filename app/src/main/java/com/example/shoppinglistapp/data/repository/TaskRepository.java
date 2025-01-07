package com.example.shoppinglistapp.data.repository;

import com.example.shoppinglistapp.data.model.Task;
import com.example.shoppinglistapp.data.remote.FirebaseTaskService;
import com.example.shoppinglistapp.domain.repository.TaskDataCallback;
import com.example.shoppinglistapp.domain.model.TaskModel;
import com.example.shoppinglistapp.domain.repository.TaskRepositoryInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository implements TaskRepositoryInterface {
    private final FirebaseTaskService firebaseTaskService;

    public TaskRepository(FirebaseTaskService firebaseTaskService) {
        this.firebaseTaskService = firebaseTaskService;
    }

    @Override
    public void saveTask(String taskId, TaskModel task) {
        // Convert TaskModel to Task (including completed state)
        Task firebaseTask = new Task(task.getName(), task.getPrice(), task.isCompleted());
        firebaseTaskService.saveTask(taskId, firebaseTask);
    }

    @Override
    public void updateTask(String taskId, TaskModel task) {
        // Convert TaskModel to Task (including completed state)
        Task firebaseTask = new Task(task.getName(), task.getPrice(), task.isCompleted());
        firebaseTaskService.updateTask(taskId, firebaseTask);
    }

    @Override
    public void removeTask(String taskId) {
        firebaseTaskService.removeTask(taskId);
    }

    @Override
    public void getTasks(final TaskDataCallback callback) {
        final List<TaskModel> taskList = new ArrayList<>();
        firebaseTaskService.getTasksReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList.clear(); // Clear existing list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        // Convert Task to TaskModel and include completed state
                        taskList.add(new TaskModel(task.getName(), task.getPrice(), task.isCompleted()));
                    }
                }
                // Pass the list of tasks to the callback once data is fetched
                callback.onDataFetched(taskList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle the error
            }
        });
    }
}
