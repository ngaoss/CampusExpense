package com.example.manager.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RecurringExpenseRepository {

    private final DatabaseHelper dbHelper;

    public RecurringExpenseRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public double getEstimatedMonthlyRecurringForCategory(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        double totalMonthlyRecurring = 0;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_RECURRING_EXPENSES,
                    new String[]{DatabaseHelper.KEY_AMOUNT, DatabaseHelper.KEY_FREQUENCY},
                    DatabaseHelper.KEY_CATEGORY_ID + " = ?",
                    new String[]{String.valueOf(categoryId)},
                    null, null, null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_AMOUNT));
                    String frequency = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FREQUENCY));

                    if (frequency != null) {
                        switch (frequency.toUpperCase()) {
                            case "DAILY":
                                totalMonthlyRecurring += amount * 30;
                                break;
                            case "MONTHLY":
                                totalMonthlyRecurring += amount;
                                break;
                        }
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return totalMonthlyRecurring;
    }

}