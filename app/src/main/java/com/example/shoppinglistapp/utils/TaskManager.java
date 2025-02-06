package com.example.shoppinglistapp.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.example.shoppinglistapp.MainActivity;
import com.example.shoppinglistapp.models.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private final Context context;
    private final List<Task> taskList;
    SharedPreferencesHelper sharedPreferencesHelper;

    public TaskManager(Context context) {
        this.context = context;
        taskList = new ArrayList<>();
        sharedPreferencesHelper = new SharedPreferencesHelper(context);
    }

    public void listenForTaskUpdates(LinearLayout taskListLayout, TextView totalPrice, String selectedMonthYear) {
        FirebaseHelper.getTaskReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                taskListLayout.removeAllViews();

                int item_count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    assert task != null;
                    task.setId(snapshot.getKey());

                    // Only show tasks that match the selected monthYear
                    if (selectedMonthYear.equals(task.getMonthYear()) || selectedMonthYear.isEmpty()) {
                        taskList.add(task);
                        item_count++;

                        // Use TaskAdapter to display the task view
                        taskListLayout.addView(TaskAdapter.createTaskView(task, taskListLayout, TaskManager.this));
                    }
                }

                if (item_count > sharedPreferencesHelper.getItemCountForSpecificMonth(selectedMonthYear)) {
                    NotificationHelper.showNotification(context, "Tap to see the items.");
                }
                sharedPreferencesHelper.storeItemCountForSpecificMonth(item_count, selectedMonthYear);
                updateTotalPrice(totalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }

    public void addNewTask(String name, String price, String selectedMonthYear) {
        Task task = new Task("", name, Double.parseDouble(price), false, selectedMonthYear);
        FirebaseHelper.addTask(task);
        int item_count = sharedPreferencesHelper.getItemCountForSpecificMonth(selectedMonthYear);
        sharedPreferencesHelper.storeItemCountForSpecificMonth(item_count + 1, selectedMonthYear);
    }

    public void updateTask(Task task) {
        FirebaseHelper.updateTask(task);
    }

    public void removeTask(Task task) {
        FirebaseHelper.removeTask(task.getId());
    }

    public void enableEditing(EditText taskName, EditText taskPrice, AppCompatButton saveButton, AppCompatButton removeButton, ImageButton editButton) {
        taskName.setEnabled(true);
        taskPrice.setEnabled(true);
        saveButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.GONE);
        removeButton.setVisibility(View.GONE);
    }

    public void disableEditing(EditText taskName, EditText taskPrice, AppCompatButton saveButton, AppCompatButton removeButton) {
        taskName.setEnabled(false);
        taskPrice.setEnabled(false);
        saveButton.setVisibility(View.GONE);
        removeButton.setVisibility(View.VISIBLE);
    }

    public void saveTaskChanges(Task task, EditText taskName, EditText taskPrice, AppCompatButton saveButton, AppCompatButton removeButton, ImageButton editButton) {
        String updatedName = taskName.getText().toString();
        double updatedPrice = Double.parseDouble(taskPrice.getText().toString());

        task.setName(updatedName);
        task.setPrice(updatedPrice);
        FirebaseHelper.updateTask(task);

        disableEditing(taskName, taskPrice, saveButton, removeButton);
        editButton.setVisibility(View.VISIBLE);

        updateTotalPrice(null);
    }

    @SuppressLint("SetTextI18n")
    private void updateTotalPrice(TextView totalPrice) {
        double total = 0;
        for (Task task : taskList) {
            if (task.isCompleted()) {
                total += task.getPrice();
            }
        }
        if (totalPrice != null) {
            totalPrice.setText("Total: ৳" + total);
        }
    }

    public void startForegroundServiceIfNotRunning() {
        if (!isServiceRunning(ForegroundService.class)) {
            context.startService(new Intent(context, ForegroundService.class));
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
