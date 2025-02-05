package com.example.shoppinglistapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "item";
    private static final String ITEM_COUNT_KEY = "item_count";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void storeItemCount(int itemCount) {
        editor.putInt(ITEM_COUNT_KEY, itemCount);
        editor.apply();
    }

    public int getItemCount() {
        return sharedPreferences.getInt(ITEM_COUNT_KEY, 0);
    }
}
