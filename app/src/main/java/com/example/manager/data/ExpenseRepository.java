package com.example.manager.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.manager.Models.Transaction;

import java.util.ArrayList;
import java.util.List;
public class ExpenseRepository {

    private DatabaseHelper dbHelper;

    public ExpenseRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public double getTotalExpensesForCategory(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        double totalExpenses = 0;

        try {
            cursor = db.rawQuery(
                    "SELECT SUM(" + DatabaseHelper.KEY_AMOUNT + ") FROM " + DatabaseHelper.TABLE_EXPENSES +
                            " WHERE " + DatabaseHelper.KEY_CATEGORY_ID + " = ?",
                    new String[]{String.valueOf(categoryId)}
            );

            if (cursor != null && cursor.moveToFirst()) {
                totalExpenses = cursor.getDouble(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return totalExpenses;
    }
    public List<Transaction> getFilteredTransactions(String startDate, String endDate, long categoryId) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String selection = DatabaseHelper.KEY_EXPENSE_DATE + " BETWEEN ? AND ?";
            List<String> selectionArgsList = new ArrayList<>();
            selectionArgsList.add(startDate);
            selectionArgsList.add(endDate);

            if (categoryId != -1) {
                selection += " AND " + DatabaseHelper.KEY_CATEGORY_ID + " = ?";
                selectionArgsList.add(String.valueOf(categoryId));
            }

            String[] selectionArgs = selectionArgsList.toArray(new String[0]);

            String query = "SELECT e." + DatabaseHelper.KEY_ID + ", e." + DatabaseHelper.KEY_DESCRIPTION + ", e." + DatabaseHelper.KEY_AMOUNT + ", e." + DatabaseHelper.KEY_EXPENSE_DATE + ", c." + DatabaseHelper.KEY_CATEGORY_NAME +
                    " FROM " + DatabaseHelper.TABLE_EXPENSES + " e JOIN " + DatabaseHelper.TABLE_CATEGORIES + " c ON e." + DatabaseHelper.KEY_CATEGORY_ID + " = c." + DatabaseHelper.KEY_ID +
                    " WHERE " + selection + " ORDER BY e." + DatabaseHelper.KEY_EXPENSE_DATE + " DESC";

            cursor = db.rawQuery(query, selectionArgs);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_ID));
                    String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_DESCRIPTION));
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_AMOUNT));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EXPENSE_DATE));
                    String catName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_CATEGORY_NAME));
                    transactions.add(new Transaction(id, desc, amount, date, catName, "One-Time"));
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }


        return transactions;
    }


}