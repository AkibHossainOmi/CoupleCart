package com.example.shoppinglistapp.presentation.ui.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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
        holder.taskText.setText(task.getName());
        holder.taskPrice.setText(String.valueOf(task.getPrice()));

        // Handle task removal
        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskRemoved(position);
            }
        });

        // Handle task completion (checkbox)
        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskText;
        EditText taskPrice;
        CheckBox taskCheckBox;
        View removeButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.taskText);
            taskPrice = itemView.findViewById(R.id.taskPrice);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    public interface TaskRemoveListener {
        void onTaskRemoved(int position);
    }
}
