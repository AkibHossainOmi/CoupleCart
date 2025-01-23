package com.example.shoppinglistapp.presentation.ui.main;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import com.example.shoppinglistapp.R;
import com.example.shoppinglistapp.data.remote.FirebaseTaskService;
import com.example.shoppinglistapp.data.repository.TaskRepository;
import com.example.shoppinglistapp.domain.model.TaskModel;
import com.example.shoppinglistapp.domain.usecase.TaskUseCase;
import com.example.shoppinglistapp.presentation.utils.ForegroundService;
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isPersistenceEnabled = prefs.getBoolean("persistence_enabled", false);

        if (!isPersistenceEnabled) {
            // Enable Firebase persistence only if it has not been done before
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            // Set the flag in SharedPreferences to remember that persistence has been enabled
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("persistence_enabled", true);
            editor.apply();
        }
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        if (!isServiceRunning(ForegroundService.class)) {
            startService(serviceIntent);
        }

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
                    saveLocalTaskId(taskId);
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

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true; // Service is already running
                }
            }
        }
        return false; // Service is not running
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can start the service
                Intent serviceIntent = new Intent(this, ForegroundService.class);
                startService(serviceIntent);
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied, cannot start service", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveLocalTaskId(String taskId) {
        SharedPreferences sharedPreferences = getSharedPreferences("ShoppingListPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_added_task", taskId); // Save the last added task ID
        editor.apply();
    }

    private String getLocalTaskId() {
        SharedPreferences sharedPreferences = getSharedPreferences("ShoppingListPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("last_added_task", null); // Return null if no value is found
    }


    private void listenForTaskUpdates() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tasksRef = database.getReference("tasks");

        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList.removeAllViews(); // Clear the list before adding updated tasks
                totalPrice = 0.0; // Reset total price

                int currentItemCount = 0; // To track current number of items
                boolean newItemAdded = false; // Flag to track if a new item was added

                // Retrieve the last added task ID saved locally
                String lastAddedTaskId = getLocalTaskId();

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

                        currentItemCount++; // Increment the count

                        // Check if this task ID is different from the last added task ID
                        if (lastAddedTaskId != null && snapshot.getKey().equals(lastAddedTaskId)) {
                            newItemAdded = true; // Mark that the task was added locally
                        }
                    }
                }

                // Compare current item count with saved item count
                int savedItemCount = getSavedItemCount();
                if (!newItemAdded && currentItemCount > savedItemCount) {
                    // Show notification if new items have been added by another device
                    showNotification("Tap to see the items.");
                }

                // Save the new item count in SharedPreferences
                saveItemCount(currentItemCount);

                // Update the total price
                updateTotalPrice();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Failed to read value.", error.toException());
            }
        });
    }


    private void saveItemCount(int count) {
        SharedPreferences sharedPreferences = getSharedPreferences("ShoppingListPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("item_count", count); // Save the count in SharedPreferences
        editor.apply();
    }
    private int getSavedItemCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("ShoppingListPrefs", MODE_PRIVATE);
        return sharedPreferences.getInt("item_count", 0); // Default to 0 if no value is found
    }

    private void showNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create NotificationChannel
            NotificationChannel channel = new NotificationChannel("new_item_channel",
                    "CoupleCart",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Create AudioAttributes for notification sound
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            // Set the notification sound and lock screen visibility
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); // Show on lock screen

            // Register the notification channel
            notificationManager.createNotificationChannel(channel);
        }

        // Intent for opening the MainActivity when the notification is clicked
        Intent notificationIntent = new Intent(this, MainActivity.class);

        // PendingIntent to allow the notification to launch the app
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        Notification notification = new NotificationCompat.Builder(this, "new_item_channel")
                .setContentTitle("Got New Item!")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_logo_small) // Your notification icon
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Ensure high priority for visibility
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)  // Automatically remove the notification on click
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)  // Show on the lock screen
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))  // Set sound
                .build();

        // Notify the user with the notification
        notificationManager.notify(0, notification);
    }

    private void addTaskToList(final String taskId, String taskText, double taskPrice, boolean isChecked) {
        // Inflate the task item layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View taskView = inflater.inflate(R.layout.task_item, taskList, false);

        // Reference UI elements
        final EditText taskNameView = taskView.findViewById(R.id.taskName);
        final EditText taskPriceView = taskView.findViewById(R.id.taskPrice);
        final ImageButton editButton = taskView.findViewById(R.id.editButton); // ImageButton for editing
        final Button removeButton = taskView.findViewById(R.id.removeButton);
        final Button saveButton = taskView.findViewById(R.id.saveButton); // Save Button
        final CheckBox taskCheckBox = taskView.findViewById(R.id.taskCheckBox);

        // Initialize UI elements with task data
        taskNameView.setText(taskText);
        taskPriceView.setText(String.valueOf(taskPrice));
        taskCheckBox.setChecked(isChecked);
        taskNameView.setEnabled(false);
        taskPriceView.setEnabled(false);
        saveButton.setVisibility(View.GONE); // Initially hide the Save button

        // Handle "Edit" button click
        editButton.setOnClickListener(v -> {
            if (!taskNameView.isEnabled()) {
                // Enable editing mode
                toggleEditingMode(taskNameView, taskPriceView, true);

                // Hide the Edit and Remove buttons, show the Save button
                editButton.setVisibility(View.GONE); // Hide Edit button
                removeButton.setVisibility(View.GONE); // Hide Remove button
                saveButton.setVisibility(View.VISIBLE); // Show Save button
            }
        });

        // Handle "Save" button click
        saveButton.setOnClickListener(v -> {
            // Save the changes
            String updatedName = taskNameView.getText().toString().trim();
            String updatedPriceText = taskPriceView.getText().toString().trim();

            if (updatedName.isEmpty() || updatedPriceText.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill both name and price!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double updatedPrice = Double.parseDouble(updatedPriceText);
                updateTaskNameAndPrice(taskId, updatedName, updatedPrice);

                // After saving, hide the Save button, and re-enable remove button and checkbox
                toggleEditingMode(taskNameView, taskPriceView, false);
                removeButton.setVisibility(View.VISIBLE); // Show the remove button again
                taskCheckBox.setEnabled(true); // Enable the checkbox again

                // Hide the Save button
                saveButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE); // Show the edit button again
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid price entered!", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "Remove" button click
        removeButton.setOnClickListener(v -> {
            deleteTask(taskId); // Delete the task from Firebase
            taskList.removeView(taskView); // Remove the task view from the list
        });

        // Handle checkbox functionality
        taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
            if (isChecked1) {
                totalPrice += taskPrice; // Add task's price to the total when checked
                updateTaskCheckedState(taskId, true); // Update the task's checked state in Firebase
            } else {
                totalPrice -= taskPrice; // Subtract task's price from the total when unchecked
                updateTaskCheckedState(taskId, false); // Update the task's checked state in Firebase
            }
            updateTotalPrice(); // Update the total price
        });

        // Add the inflated task view to the task list
        taskList.addView(taskView);
    }

    private void toggleEditingMode(EditText taskNameView, EditText taskPriceView, boolean enable) {
        taskNameView.setEnabled(enable);
        taskPriceView.setEnabled(enable);
    }




    private void updateTaskNameAndPrice(String taskId, String newName, double newPrice) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference taskRef = database.getReference("tasks").child(taskId);

        // Update both name and price in Firebase
        taskRef.child("name").setValue(newName);
        taskRef.child("price").setValue(newPrice)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Task updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to update task", Toast.LENGTH_SHORT).show();
                    }
                });
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
    private void updateTaskName(String taskId, String newName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference taskRef = database.getReference("tasks").child(taskId);

        taskRef.child("name").setValue(newName) // Update the name in Firebase
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Task name updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to update task name", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
