package com.example.shoppinglistapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthYearHelper {

    public static void populateMonthYearSpinner(Context context, Spinner monthYearSpinner) {

        List<String> monthsList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");

        monthsList.add(dateFormat.format(calendar.getTime()));

        for (int i = 1; i < 12; i++) {
            calendar.add(Calendar.MONTH, -1);
            monthsList.add(dateFormat.format(calendar.getTime()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, monthsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthYearSpinner.setAdapter(adapter);
    }

    public static String getCurrentMonthYear() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");
        return dateFormat.format(calendar.getTime());
    }

    public static void setDefaultMonthYear(Spinner monthYearSpinner, String currentMonthYear) {
        for (int i = 0; i < monthYearSpinner.getCount(); i++) {
            if (monthYearSpinner.getItemAtPosition(i).toString().equals(currentMonthYear)) {
                monthYearSpinner.setSelection(i);
                break;
            }
        }
    }
}
