package com.example.shoppinglistapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "shopping_list_prefs";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Method to store item count for the current month
    public void storeItemCountForCurrentMonth(int itemCount) {
        String currentMonthKey = getCurrentMonthKey();
        editor.putInt(currentMonthKey, itemCount);
        editor.apply();
    }

    // Method to get item count for the current month
    public int getItemCountForCurrentMonth() {
        String currentMonthKey = getCurrentMonthKey();
        return sharedPreferences.getInt(currentMonthKey, 0);
    }

    // Helper method to get the current month key in "yyyy-MM" format
    private String getCurrentMonthKey() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        String currentMonth = dateFormat.format(Calendar.getInstance().getTime());
        return "item_count_" + currentMonth;
    }

    // Optional: Method to store item count for a specific month
    public void storeItemCountForSpecificMonth(int itemCount, String yearMonth) {
        String key = "item_count_" + yearMonth;
        editor.putInt(key, itemCount);
        editor.apply();
    }

    // Optional: Method to get item count for a specific month
    public int getItemCountForSpecificMonth(String yearMonth) {
        String key = "item_count_" + yearMonth;
        return sharedPreferences.getInt(key, 0);
    }
}
