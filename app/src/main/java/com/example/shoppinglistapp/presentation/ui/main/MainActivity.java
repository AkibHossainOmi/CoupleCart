package com.example.shoppinglistapp.presentation.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppinglistapp.R;
import com.example.shoppinglistapp.data.remote.FirebaseTaskService;
import com.example.shoppinglistapp.data.repository.TaskRepository;
import com.example.shoppinglistapp.domain.model.TaskModel;
import com.example.shoppinglistapp.domain.usecase.TaskUseCase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private LinearLayout taskList;
    private EditText inputBox, priceBox;
    private TextView totalPriceText;
    private TaskUseCase taskUseCase;
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Backendless.initApp(this,"6140E01A-F403-4560-A400-BC2AECADA0B9", "53160DC3-9478-4048-96E9-F85EECCCA77A");
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // Get the Firebase token
//                        String token = task.getResult();
//
//                        // Now register the device with Backendless
//                        Backendless.Messaging.registerDevice();
//                    } else {
//                        // Handle failure to get Firebase token
//                        Log.e("Firebase", "Failed to get token: " + task.getException().getMessage());
//                    }
//                });

        // Initialize the UseCase
        taskUseCase = new TaskUseCase(new TaskRepository(new FirebaseTaskService()));

        inputBox = findViewById(R.id.inputBox);
        priceBox = findViewById(R.id.inputPrice);
        taskList = findViewById(R.id.taskList);
        totalPriceText = findViewById(R.id.totalPrice);
        Button addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> {
            String taskText = inputBox.getText().toString().trim();
            String priceText = priceBox.getText().toString().trim();

            if (!taskText.isEmpty() && !priceText.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceText);
                    TaskModel taskModel = new TaskModel(taskText, price, false);  // Create TaskModel with "not checked" state
                    String taskId = taskUseCase.saveTask(taskModel);  // Save the task and get the task ID
//                    taskUseCase.notifyNewTaskAdded(taskText);
                    inputBox.setText(""); // Clear input box
                    priceBox.setText(""); // Clear price box

                    // Do not modify the total price here
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid price entered!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Please enter an item and its price!", Toast.LENGTH_SHORT).show();
            }
        });

        // Listen for changes in the Firebase database
        listenForTaskUpdates();
    }

    private void listenForTaskUpdates() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("tasks");

        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList.removeAllViews(); // Clear the list before adding updated tasks
                totalPrice = 0.0; // Reset total price

                // Loop through the tasks in the database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TaskModel task = snapshot.getValue(TaskModel.class);
                    if (task != null) {
                        // Add each task to the UI using the inflated layout
                        addTaskToList(snapshot.getKey(), task.getName(), task.getPrice(), task.isCompleted());

                        // If the task is checked, add the task's price to the total
                        if (task.isCompleted()) {
                            totalPrice += task.getPrice();
                        }
                    }
                }

                // Update the total price
                updateTotalPrice();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Failed to read value.", error.toException());
            }
        });
    }

    private void addTaskToList(final String taskId, final String taskText, double taskPrice, boolean isChecked) {
        // Inflate the task item layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View taskView = inflater.inflate(R.layout.task_item, taskList, false);

        // Set the task name and price
        TextView taskTextView = taskView.findViewById(R.id.taskText);
        taskTextView.setText(taskText);

        EditText taskPriceView = taskView.findViewById(R.id.taskPrice);
        taskPriceView.setText(String.valueOf(taskPrice));

        // Listen for changes in the price
        taskPriceView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // When focus is lost, save the updated price to Firebase
                try {
                    double newPrice = Double.parseDouble(taskPriceView.getText().toString().trim());
                    updateTaskPrice(taskId, newPrice);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid price entered!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the Remove Button functionality
        Button removeButton = taskView.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(v -> {
            deleteTask(taskId);  // Delete the task from Firebase
            taskList.removeView(taskView);  // Remove the task view from the list
        });

        // Set up the checkbox for marking the task
        CheckBox taskCheckBox = taskView.findViewById(R.id.taskCheckBox);
        taskCheckBox.setChecked(isChecked);

        // Only update the total price when the checkbox is toggled
        taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
            if (isChecked1) {
                totalPrice += taskPrice;  // Add task's price to the total when marked
                updateTaskCheckedState(taskId, true);  // Update the task's checked state in Firebase
            } else {
                totalPrice -= taskPrice;  // Subtract task's price from the total when unmarked
                updateTaskCheckedState(taskId, false);  // Update the task's checked state in Firebase
            }
            updateTotalPrice();  // Update the total price
        });

        // Add the inflated task view to the task list
        taskList.addView(taskView);
    }

    private void updateTotalPrice() {
        totalPriceText.setText("Total: ৳" + totalPrice);
    }

    private void deleteTask(String taskId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference taskRef = database.getReference("tasks").child(taskId);

        taskRef.removeValue()  // Delete the task from Firebase
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Task deleted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTaskCheckedState(String taskId, boolean isChecked) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference taskRef = database.getReference("tasks").child(taskId);

        taskRef.child("completed").setValue(isChecked);  // Update the completed state in Firebase
    }

    private void updateTaskPrice(String taskId, double newPrice) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference taskRef = database.getReference("tasks").child(taskId);

        taskRef.child("price").setValue(newPrice)  // Update the price in Firebase
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Price successfully updated
                        Toast.makeText(MainActivity.this, "Price updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure to update
                        Toast.makeText(MainActivity.this, "Failed to update price", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
