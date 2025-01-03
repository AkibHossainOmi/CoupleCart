package com.example.shoppinglistapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {
    private LinearLayout taskList;
    private EditText inputBox;
    private DatabaseReference databaseTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database
        databaseTasks = FirebaseDatabase.getInstance().getReference("tasks");

        inputBox = findViewById(R.id.inputBox);
        taskList = findViewById(R.id.taskList);
        Button addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> {
            String taskText = inputBox.getText().toString().trim();
            if (!taskText.isEmpty()) {
                saveTaskToFirebase(taskText); // Save to Firebase (adds to UI automatically)
                inputBox.setText(""); // Clear input box after adding
            } else {
                Toast.makeText(MainActivity.this, "Please enter an item!", Toast.LENGTH_SHORT).show();
            }
        });

        // Listen for changes in the Firebase database
        listenForTaskUpdates();
    }

    private void addTaskToList(final String taskId, final String taskText) {
        View taskView = LayoutInflater.from(this).inflate(R.layout.task_item, null);

        TextView taskTextView = taskView.findViewById(R.id.taskText);
        taskTextView.setText(taskText);

        // Set unique tag to the view
        taskView.setTag(taskId);

        Button removeButton = taskView.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(v -> {
            taskList.removeView(taskView); // Remove from UI
            removeTaskFromFirebase(taskId); // Remove from Firebase using the unique ID
        });

        taskList.addView(taskView);
    }

    // Save task to Firebase Realtime Database
    private void saveTaskToFirebase(String taskText) {
        String taskId = databaseTasks.push().getKey(); // Generate a unique ID
        if (taskId != null) {
            databaseTasks.child(taskId).setValue(taskText); // Save task with ID as key
        }
    }

    // Listen for updates in Firebase Realtime Database
    private void listenForTaskUpdates() {
        databaseTasks.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                String taskId = snapshot.getKey();
                String taskText = snapshot.getValue(String.class);
                if (taskId != null && taskText != null) {
                    addTaskToList(taskId, taskText); // Add task to UI
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                // Optional: Handle changes to task text if needed
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
