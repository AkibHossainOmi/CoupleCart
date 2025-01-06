package com.example.shoppinglistapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private LinearLayout taskList;
    private EditText inputBox, priceBox;
    private TextView totalPriceText;
    private DatabaseReference databaseTasks;
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database
        databaseTasks = FirebaseDatabase.getInstance().getReference("tasks");

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
                    saveTaskToFirebase(taskText, price); // Save to Firebase
                    inputBox.setText(""); // Clear input box
                    priceBox.setText(""); // Clear price box
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

    private void addTaskToList(final String taskId, final String taskText, double taskPrice) {
        View taskView = LayoutInflater.from(this).inflate(R.layout.task_item, null);

        TextView taskTextView = taskView.findViewById(R.id.taskText);
        taskTextView.setText(taskText);

        EditText priceEditText = taskView.findViewById(R.id.taskPrice);
        priceEditText.setText(String.format("%.2f", taskPrice));

        // Make the price EditText editable for updates
        priceEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // When focus is lost, update the price in Firebase
                String updatedPriceText = priceEditText.getText().toString().trim();
                if (!updatedPriceText.isEmpty()) {
                    try {
                        double updatedPrice = Double.parseDouble(updatedPriceText);
                        updatePriceInFirebase(taskId, updatedPrice);
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Invalid price entered!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        CheckBox taskCheckBox = taskView.findViewById(R.id.taskCheckBox);
        taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                totalPrice += taskPrice;
            } else {
                totalPrice -= taskPrice;
            }
            updateTotalPrice();
        });

        // Set unique tag to the view
        taskView.setTag(taskId);

        Button removeButton = taskView.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(v -> {
            taskList.removeView(taskView); // Remove from UI
            removeTaskFromFirebase(taskId); // Remove from Firebase
        });

        taskList.addView(taskView);
    }

    private void updateTotalPrice() {
        totalPrice = Math.max(totalPrice, 0); // Ensure total is never negative
        totalPriceText.setText(String.format("Total: $%.2f", totalPrice));
    }

    private void saveTaskToFirebase(String taskText, double price) {
        String taskId = databaseTasks.push().getKey(); // Generate a unique ID
        if (taskId != null) {
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("name", taskText);
            taskData.put("price", price);
            databaseTasks.child(taskId).setValue(taskData); // Save task with ID as key
        }
    }

    private void updatePriceInFirebase(String taskId, double updatedPrice) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("price", updatedPrice);
        databaseTasks.child(taskId).updateChildren(updatedData); // Update price in Firebase
    }

    private void listenForTaskUpdates() {
        databaseTasks.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                String taskId = snapshot.getKey();
                Map<String, Object> taskData = (Map<String, Object>) snapshot.getValue();
                if (taskData != null) {
                    String taskText = (String) taskData.get("name");
                    Double taskPrice = taskData.get("price") instanceof Long
                            ? ((Long) taskData.get("price")).doubleValue()
                            : (Double) taskData.get("price");
                    if (taskId != null && taskText != null && taskPrice != null) {
                        addTaskToList(taskId, taskText, taskPrice); // Add task to UI
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                String taskId = snapshot.getKey();
                Map<String, Object> taskData = (Map<String, Object>) snapshot.getValue();
                if (taskData != null) {
                    String taskText = (String) taskData.get("name");
                    Double taskPrice = taskData.get("price") instanceof Long
                            ? ((Long) taskData.get("price")).doubleValue()
                            : (Double) taskData.get("price");
                    if (taskId != null && taskText != null && taskPrice != null) {
                        updateTaskInUI(taskId, taskText, taskPrice); // Update task in UI
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                String taskId = snapshot.getKey();
                if (taskId != null) {
                    removeTaskFromUI(taskId); // Remove from UI
                }
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                // Optional: Handle task reordering if needed
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load tasks: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTaskInUI(String taskId, String taskText, double taskPrice) {
        for (int i = 0; i < taskList.getChildCount(); i++) {
            View taskView = taskList.getChildAt(i);
            String tag = (String) taskView.getTag();
            if (tag != null && tag.equals(taskId)) {
                TextView taskTextView = taskView.findViewById(R.id.taskText);
                EditText priceEditText = taskView.findViewById(R.id.taskPrice);

                taskTextView.setText(taskText);
                priceEditText.setText(String.format("%.2f", taskPrice));
                break;
            }
        }
    }

    private void removeTaskFromFirebase(String taskId) {
        databaseTasks.child(taskId).removeValue(); // Remove task using unique ID
    }

    private void removeTaskFromUI(String taskId) {
        for (int i = 0; i < taskList.getChildCount(); i++) {
            View taskView = taskList.getChildAt(i);
            String tag = (String) taskView.getTag();
            if (tag != null && tag.equals(taskId)) {
                taskList.removeView(taskView);
                break;
            }
        }
    }
}
