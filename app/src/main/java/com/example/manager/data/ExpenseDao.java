package com.example.manager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.manager.Models.Category;
import com.example.manager.Models.Expense;
import com.example.manager.Models.RecurringExpense;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ExpenseDao {
    private DatabaseHelper dbHelper;

    public ExpenseDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addCategory(String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_CATEGORY_NAME, name);
        db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
        db.close();
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES, null);

        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DatabaseHelper.KEY_CATEGORY_NAME);

            if (idIndex != -1 && nameIndex != -1) {
                if (cursor.moveToFirst()) {
                    do {
                        categories.add(new Category(cursor.getLong(idIndex), cursor.getString(nameIndex)));
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        }
        db.close();
        return categories;
    }

    public void addExpense(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_AMOUNT, expense.getAmount());
        values.put(DatabaseHelper.KEY_DESCRIPTION, expense.getDescription());
        values.put(DatabaseHelper.KEY_EXPENSE_DATE, expense.getDate());
        values.put(DatabaseHelper.KEY_CATEGORY_ID, expense.getCategory().getId());
        db.insert(DatabaseHelper.TABLE_EXPENSES, null, values);
        db.close();
    }

    public void updateExpense(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_AMOUNT, expense.getAmount());
        values.put(DatabaseHelper.KEY_DESCRIPTION, expense.getDescription());
        values.put(DatabaseHelper.KEY_EXPENSE_DATE, expense.getDate());
        values.put(DatabaseHelper.KEY_CATEGORY_ID, expense.getCategory().getId());
        db.update(DatabaseHelper.TABLE_EXPENSES, values, DatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(expense.getId())});
        db.close();
    }

    public void deleteExpense(long expenseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_EXPENSES, DatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(expenseId)});
        db.close();
    }

    public Expense getExpenseById(long expenseId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Expense expense = null;
        String query = "SELECT E.*, C." + DatabaseHelper.KEY_CATEGORY_NAME + " as cat_name, C." + DatabaseHelper.KEY_ID + " as cat_id"
                + " FROM " + DatabaseHelper.TABLE_EXPENSES + " E JOIN " + DatabaseHelper.TABLE_CATEGORIES
                + " C ON E." + DatabaseHelper.KEY_CATEGORY_ID + " = C." + DatabaseHelper.KEY_ID
                + " WHERE E." + DatabaseHelper.KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(expenseId)});

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
            int amountIndex = cursor.getColumnIndex(DatabaseHelper.KEY_AMOUNT);
            int descIndex = cursor.getColumnIndex(DatabaseHelper.KEY_DESCRIPTION);
            int dateIndex = cursor.getColumnIndex(DatabaseHelper.KEY_EXPENSE_DATE);
            int catIdIndex = cursor.getColumnIndex("cat_id");
            int catNameIndex = cursor.getColumnIndex("cat_name");

            Category category = new Category(cursor.getLong(catIdIndex), cursor.getString(catNameIndex));
            expense = new Expense(
                    cursor.getLong(idIndex),
                    cursor.getDouble(amountIndex),
                    cursor.getString(descIndex),
                    cursor.getString(dateIndex),
                    category,
                    false
            );
            cursor.close();
        }
        db.close();
        return expense;
    }

    // --- RECURRING EXPENSE FUNCTIONS ---
    public void addRecurringExpense(RecurringExpense recExpense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_AMOUNT, recExpense.getAmount());
        values.put(DatabaseHelper.KEY_DESCRIPTION, recExpense.getDescription());
        values.put(DatabaseHelper.KEY_START_DATE, recExpense.getStartDate());
        values.put(DatabaseHelper.KEY_END_DATE, recExpense.getEndDate());
        values.put(DatabaseHelper.KEY_FREQUENCY, recExpense.getFrequency());
        values.put(DatabaseHelper.KEY_CATEGORY_ID, recExpense.getCategory().getId());
        db.insert(DatabaseHelper.TABLE_RECURRING_EXPENSES, null, values);
        db.close();
    }
    public void deleteRecurringRule(long ruleId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_RECURRING_EXPENSES, DatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(ruleId)});
        db.close();
    }
    public void updateRecurringRule(RecurringExpense rule) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_AMOUNT, rule.getAmount());
        values.put(DatabaseHelper.KEY_DESCRIPTION, rule.getDescription());
        values.put(DatabaseHelper.KEY_START_DATE, rule.getStartDate());
        values.put(DatabaseHelper.KEY_FREQUENCY, rule.getFrequency());
        values.put(DatabaseHelper.KEY_CATEGORY_ID, rule.getCategory().getId());
        db.update(DatabaseHelper.TABLE_RECURRING_EXPENSES, values, DatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(rule.getId())});
        db.close();
    }

    public RecurringExpense getRecurringRuleById(long ruleId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        RecurringExpense rule = null;
        String query = "SELECT R.*, C." + DatabaseHelper.KEY_CATEGORY_NAME + " as cat_name, C." + DatabaseHelper.KEY_ID + " as cat_id"
                + " FROM " + DatabaseHelper.TABLE_RECURRING_EXPENSES + " R JOIN " + DatabaseHelper.TABLE_CATEGORIES
                + " C ON R." + DatabaseHelper.KEY_CATEGORY_ID + " = C." + DatabaseHelper.KEY_ID
                + " WHERE R." + DatabaseHelper.KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(ruleId)});

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
            int amountIndex = cursor.getColumnIndex(DatabaseHelper.KEY_AMOUNT);
            int descIndex = cursor.getColumnIndex(DatabaseHelper.KEY_DESCRIPTION);
            int startIndex = cursor.getColumnIndex(DatabaseHelper.KEY_START_DATE);
            int endIndex = cursor.getColumnIndex(DatabaseHelper.KEY_END_DATE);
            int freqIndex = cursor.getColumnIndex(DatabaseHelper.KEY_FREQUENCY);
            int catIdIndex = cursor.getColumnIndex("cat_id");
            int catNameIndex = cursor.getColumnIndex("cat_name");

            Category category = new Category(cursor.getLong(catIdIndex), cursor.getString(catNameIndex));
            rule = new RecurringExpense(
                    cursor.getLong(idIndex),
                    cursor.getDouble(amountIndex),
                    cursor.getString(descIndex),
                    cursor.getString(startIndex),
                    cursor.getString(endIndex),
                    cursor.getString(freqIndex),
                    category
            );
            cursor.close();
        }
        db.close();
        return rule;
    }
    List<RecurringExpense> getAllRecurringRules() {
        List<RecurringExpense> recurringExpenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT R.*, C." + DatabaseHelper.KEY_CATEGORY_NAME + " as cat_name, C." + DatabaseHelper.KEY_ID + " as cat_id"
                + " FROM " + DatabaseHelper.TABLE_RECURRING_EXPENSES + " R JOIN " + DatabaseHelper.TABLE_CATEGORIES
                + " C ON R." + DatabaseHelper.KEY_CATEGORY_ID + " = C." + DatabaseHelper.KEY_ID;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
            int amountIndex = cursor.getColumnIndex(DatabaseHelper.KEY_AMOUNT);
            int descIndex = cursor.getColumnIndex(DatabaseHelper.KEY_DESCRIPTION);
            int startIndex = cursor.getColumnIndex(DatabaseHelper.KEY_START_DATE);
            int endIndex = cursor.getColumnIndex(DatabaseHelper.KEY_END_DATE);
            int freqIndex = cursor.getColumnIndex(DatabaseHelper.KEY_FREQUENCY);
            int catIdIndex = cursor.getColumnIndex("cat_id");
            int catNameIndex = cursor.getColumnIndex("cat_name");

            if (idIndex != -1 && amountIndex != -1 && descIndex != -1 && startIndex != -1 && freqIndex != -1 && catIdIndex != -1 && catNameIndex != -1) {
                if (cursor.moveToFirst()) {
                    do {
                        Category category = new Category(cursor.getLong(catIdIndex), cursor.getString(catNameIndex));
                        recurringExpenses.add(new RecurringExpense(
                                cursor.getLong(idIndex),
                                cursor.getDouble(amountIndex),
                                cursor.getString(descIndex),
                                cursor.getString(startIndex),
                                cursor.getString(endIndex),
                                cursor.getString(freqIndex),
                                category
                        ));
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        }
        db.close();
        return recurringExpenses;
    }

    public List<Expense> getExpensesForMonth(int year, int month, String recurringPrefix) {
        List<Expense> allExpenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String monthString = String.format(Locale.US, "%02d", month);
        String yearMonthStr = year + "-" + monthString;

        String query = "SELECT E.*, C." + DatabaseHelper.KEY_CATEGORY_NAME + " as cat_name, C." + DatabaseHelper.KEY_ID + " as cat_id"
                + " FROM " + DatabaseHelper.TABLE_EXPENSES + " E JOIN " + DatabaseHelper.TABLE_CATEGORIES
                + " C ON E." + DatabaseHelper.KEY_CATEGORY_ID + " = C." + DatabaseHelper.KEY_ID
                + " WHERE strftime('%Y-%m', E." + DatabaseHelper.KEY_EXPENSE_DATE + ") = ?";
        Cursor cursor = db.rawQuery(query, new String[]{yearMonthStr});

        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
            int amountIndex = cursor.getColumnIndex(DatabaseHelper.KEY_AMOUNT);
            int descIndex = cursor.getColumnIndex(DatabaseHelper.KEY_DESCRIPTION);
            int dateIndex = cursor.getColumnIndex(DatabaseHelper.KEY_EXPENSE_DATE);
            int catIdIndex = cursor.getColumnIndex("cat_id");
            int catNameIndex = cursor.getColumnIndex("cat_name");

            if (cursor.moveToFirst()) {
                do {
                    Category category = new Category(cursor.getLong(catIdIndex), cursor.getString(catNameIndex));
                    allExpenses.add(new Expense(
                            cursor.getLong(idIndex),
                            cursor.getDouble(amountIndex),
                            cursor.getString(descIndex),
                            cursor.getString(dateIndex),
                            category,
                            false
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        List<RecurringExpense> allRecurringRules = getAllRecurringRules();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar ruleStartDate = Calendar.getInstance();
        Calendar monthStartDate = Calendar.getInstance();
        monthStartDate.set(year, month - 1, 1);

        for (RecurringExpense rule : allRecurringRules) {
            try {
                ruleStartDate.setTime(sdf.parse(rule.getStartDate()));

                if (!ruleStartDate.after(monthStartDate)) {
                    if ("MONTHLY".equals(rule.getFrequency())) {
                        String dayOfMonth = rule.getStartDate().substring(8, 10);
                        String expenseDateInMonth = year + "-" + monthString + "-" + dayOfMonth;
                        allExpenses.add(new Expense(
                                rule.getId(),
                                rule.getAmount(),
                                recurringPrefix + " " + rule.getDescription(),
                                expenseDateInMonth,
                                rule.getCategory(),
                                true
                        ));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        db.close();

        Collections.sort(allExpenses, (e1, e2) -> e2.getDate().compareTo(e1.getDate()));
        return allExpenses;
    }
}