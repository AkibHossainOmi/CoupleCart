package com.example.shoppinglistapp.utils;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.example.shoppinglistapp.R;
import com.example.shoppinglistapp.models.Task;

public class TaskAdapter {
    @SuppressLint("DefaultLocale")
    public static View createTaskView(Task task, LinearLayout taskListLayout, TaskManager taskManager) {
        @SuppressLint("InflateParams") View taskView = LayoutInflater.from(taskListLayout.getContext()).inflate(R.layout.task_item, null);

        CheckBox taskCheckBox = taskView.findViewById(R.id.taskCheckBox);
        EditText taskName = taskView.findViewById(R.id.taskName);
        EditText taskPrice = taskView.findViewById(R.id.taskPrice);
        ImageButton editButton = taskView.findViewById(R.id.editButton);
        AppCompatButton saveButton = taskView.findViewById(R.id.saveButton);
        AppCompatButton removeButton = taskView.findViewById(R.id.removeButton);

        taskName.setText(task.getName());
        taskPrice.setText(String.format("%.2f", task.getPrice()));
        taskCheckBox.setChecked(task.isCompleted());

        taskManager.disableEditing(taskName, taskPrice, saveButton, removeButton);

        taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            taskManager.updateTask(task);
        });

        editButton.setOnClickListener(v -> taskManager.enableEditing(taskName, taskPrice, saveButton, removeButton, editButton));

        saveButton.setOnClickListener(v -> taskManager.saveTaskChanges(task, taskName, taskPrice, saveButton, removeButton, editButton));

        removeButton.setOnClickListener(v -> taskManager.removeTask(task));

        return taskView;
    }
}
