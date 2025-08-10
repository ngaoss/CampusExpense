package com.example.manager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BudgetRepository {

    private final DatabaseHelper dbHelper;

    public BudgetRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long saveOrUpdateBudget(String categoryName, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_BUDGET_CATEGORY, categoryName);
        values.put(DatabaseHelper.KEY_AMOUNT, amount);

        int rowsAffected = db.update(
                DatabaseHelper.TABLE_BUDGETS,
                values,
                DatabaseHelper.KEY_BUDGET_CATEGORY + " = ?",
                new String[]{categoryName}
        );

        long result;
        if (rowsAffected == 0) {
            result = db.insert(DatabaseHelper.TABLE_BUDGETS, null, values);
        } else {
            result = rowsAffected;
        }

        db.close();
        return result;
    }

    public double getBudgetForCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        double budgetAmount = 0;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_BUDGETS,
                    new String[]{DatabaseHelper.KEY_AMOUNT},
                    DatabaseHelper.KEY_BUDGET_CATEGORY + " = ?",
                    new String[]{categoryName},
                    null, null, null, "1"
            );

            if (cursor != null && cursor.moveToFirst()) {
                budgetAmount = cursor.getDouble(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return budgetAmount;
    }
}