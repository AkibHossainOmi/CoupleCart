package com.example.shoppinglistapp.domain.repository;

import com.example.shoppinglistapp.domain.model.TaskModel;

import java.util.List;

public interface TaskDataCallback {
    void onDataFetched(List<TaskModel> tasks);
}
