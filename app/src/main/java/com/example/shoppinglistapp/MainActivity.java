package com.example.shoppinglistapp;

import static com.example.shoppinglistapp.utils.NotificationHelper.createNotificationChannel;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppinglistapp.utils.MonthYearHelper;
import com.example.shoppinglistapp.utils.TaskManager;

public class MainActivity extends AppCompatActivity {
    private EditText inputBox, inputPrice;
    private Button addButton;
    private LinearLayout taskListLayout;
    private TextView totalPrice;
    private TaskManager taskManager;
    private Spinner monthYearSpinner;
    private String selectedMonthYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskManager = new TaskManager(this);
        createNotificationChannel(this);
        initUI();
        taskManager.startForegroundServiceIfNotRunning();

        MonthYearHelper.populateMonthYearSpinner(this, monthYearSpinner);
        String currentMonthYear = MonthYearHelper.getCurrentMonthYear();
        MonthYearHelper.setDefaultMonthYear(monthYearSpinner, currentMonthYear);
        selectedMonthYear = currentMonthYear;
        taskManager.listenForTaskUpdates(taskListLayout, totalPrice, selectedMonthYear);

        monthYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, android.view.View selectedItemView, int position, long id) {
                selectedMonthYear = parentView.getItemAtPosition(position).toString();
                taskManager.listenForTaskUpdates(taskListLayout, totalPrice, selectedMonthYear);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        addButton.setOnClickListener(v -> {
            String taskName = inputBox.getText().toString();
            String taskPrice = inputPrice.getText().toString();
            taskManager.addNewTask(taskName, taskPrice, selectedMonthYear);
            clearInputs();
        });
    }

    private void initUI() {
        inputBox = findViewById(R.id.inputBox);
        inputPrice = findViewById(R.id.inputPrice);
        addButton = findViewById(R.id.addButton);
        taskListLayout = findViewById(R.id.taskList);
        totalPrice = findViewById(R.id.totalPrice);
        monthYearSpinner = findViewById(R.id.monthYearSpinner);
    }

    public void clearInputs() {
        inputBox.setText("");
        inputPrice.setText("");
    }
}
