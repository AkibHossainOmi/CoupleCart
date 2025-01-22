package com.example.shoppinglistapp.presentation.ui.task;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppinglistapp.R;
import com.example.shoppinglistapp.domain.model.TaskModel;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<TaskModel> taskList;
    private TaskRemoveListener listener;

    public TaskAdapter(List<TaskModel> taskList, TaskRemoveListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        TaskModel task = taskList.get(position);

        // Set initial data
        holder.taskName.setText(task.getName());
        holder.taskPrice.setText(String.valueOf(task.getPrice()));
        holder.taskCheckBox.setChecked(task.isCompleted());

        // Listen for changes in task name
        holder.taskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action required
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action required
            }

            @Override
            public void afterTextChanged(Editable s) {
                task.setName(s.toString()); // Update task name in the model
            }
        });

        // Listen for changes in task price
        holder.taskPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action required
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action required
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double price = Double.parseDouble(s.toString());
                    task.setPrice(price); // Update task price in the model
                } catch (NumberFormatException e) {
                    holder.taskPrice.setError("Invalid price"); // Show error for invalid input
                }
            }
        });

        // Handle task completion (checkbox)
        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked); // Update task completion status in the model
        });

        // Handle task removal
        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskRemoved(position); // Notify listener about task removal
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        EditText taskName; // Changed from TextView to EditText for editable task name
        EditText taskPrice;
        CheckBox taskCheckBox;
        View removeButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskPrice = itemView.findViewById(R.id.taskPrice);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    public interface TaskRemoveListener {
        void onTaskRemoved(int position);
    }
}
