package com.example.manager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "budgetwise.db";
    private static final int DATABASE_VERSION = 5;
    public static final String TABLE_USER = "users";
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_ROLE = "role";
    public static final String COL_CREATED_AT = "created_at";
    public static final String COL_UPDATED_AT = "updated_at";

    public static final String TABLE_CATEGORIES = "Categories";
    public static final String TABLE_EXPENSES = "Expenses";
    public static final String TABLE_RECURRING_EXPENSES = "RecurringExpenses";
    public static final String TABLE_BUDGETS = "budgets";

    public static final String KEY_ID = "id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_EXPENSE_DATE = "expense_date";
    public static final String KEY_CATEGORY_NAME = "name";
    public static final String KEY_START_DATE = "start_date";
    public static final String KEY_END_DATE = "end_date";
    public static final String KEY_FREQUENCY = "frequency";
    public static final String KEY_BUDGET_CATEGORY = "category";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_INCOME_DATE = "income_date";
    public static final String TABLE_INCOMES = "incomes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " VARCHAR(30) NOT NULL, "
                + COL_EMAIL + " VARCHAR(60) NOT NULL, "
                + COL_PASSWORD + " VARCHAR(200) NOT NULL, "
                + COL_ROLE + " INTEGER NOT NULL, "
                + COL_CREATED_AT + " DATETIME, "
                + COL_UPDATED_AT + " DATETIME)";

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_CATEGORY_NAME + " TEXT NOT NULL)";

        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_CATEGORY_ID + " INTEGER,"
                + KEY_AMOUNT + " REAL NOT NULL,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_EXPENSE_DATE + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + KEY_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + KEY_ID + "))";

        String CREATE_RECURRING_EXPENSES_TABLE = "CREATE TABLE " + TABLE_RECURRING_EXPENSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_CATEGORY_ID + " INTEGER NOT NULL,"
                + KEY_AMOUNT + " REAL NOT NULL,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_START_DATE + " TEXT NOT NULL,"
                + KEY_END_DATE + " TEXT,"
                + KEY_FREQUENCY + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + KEY_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + KEY_ID + "))";

        String CREATE_BUDGETS_TABLE = "CREATE TABLE " + TABLE_BUDGETS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_BUDGET_CATEGORY + " TEXT NOT NULL, "
                + KEY_AMOUNT + " REAL NOT NULL, "
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        String CREATE_INCOMES_TABLE = "CREATE TABLE " + TABLE_INCOMES + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_AMOUNT + " REAL NOT NULL, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_INCOME_DATE + " TEXT NOT NULL, "
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_EXPENSES_TABLE);
        db.execSQL(CREATE_RECURRING_EXPENSES_TABLE);
        db.execSQL(CREATE_BUDGETS_TABLE);
        db.execSQL(CREATE_INCOMES_TABLE);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_EXPENSES_TABLE);
        db.execSQL(CREATE_RECURRING_EXPENSES_TABLE);
        db.execSQL(CREATE_BUDGETS_TABLE);
        ContentValues values = new ContentValues();

        values.put(KEY_CATEGORY_NAME, "Food");
        db.insert(TABLE_CATEGORIES, null, values);

        values.put(KEY_CATEGORY_NAME, "Transport");
        db.insert(TABLE_CATEGORIES, null, values);

        values.put(KEY_CATEGORY_NAME, "Bills");
        db.insert(TABLE_CATEGORIES, null, values);

        values.put(KEY_CATEGORY_NAME, "Entertainment");
        db.insert(TABLE_CATEGORIES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRING_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOMES);
        onCreate(db);
    }
}