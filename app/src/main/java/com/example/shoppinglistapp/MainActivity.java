package com.example.shoppinglistapp;

import static com.example.shoppinglistapp.utils.NotificationHelper.createNotificationChannel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppinglistapp.utils.TaskManager;

public class MainActivity extends AppCompatActivity {
    private EditText inputBox, inputPrice;
    private Button addButton;
    private LinearLayout taskListLayout;
    private TextView totalPrice;
    private TaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskManager = new TaskManager(this);
        createNotificationChannel(this);
        initUI();
        taskManager.startForegroundServiceIfNotRunning();
        addButton.setOnClickListener(v -> taskManager.addNewTask(inputBox.getText().toString(), inputPrice.getText().toString()));
        taskManager.listenForTaskUpdates(taskListLayout, totalPrice);
    }

    private void initUI() {
        inputBox = findViewById(R.id.inputBox);
        inputPrice = findViewById(R.id.inputPrice);
        addButton = findViewById(R.id.addButton);
        taskListLayout = findViewById(R.id.taskList);
        totalPrice = findViewById(R.id.totalPrice);
    }

    public void clearInputs() {
        inputBox.setText("");
        inputPrice.setText("");
    }
}
