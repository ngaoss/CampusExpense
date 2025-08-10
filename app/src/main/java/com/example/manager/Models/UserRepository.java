package com.example.manager.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;


import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class UserRepository extends DatabaseHelper {
    public UserRepository(@Nullable Context context) {
        super(context);
    }

    public long saveUserAccount(String username, String password, String email) {
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        @SuppressLint({"NewApi", "LocalSuppress"}) ZonedDateTime zoneDt = ZonedDateTime.now();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());

// Bây giờ bạn có thể sử dụng biến currentDate mà không bị cảnh báo
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_CREATED_AT, currentDate);
        values.put(DatabaseHelper.COL_USERNAME, username);
        values.put(DatabaseHelper.COL_PASSWORD, password);
        values.put(DatabaseHelper.COL_EMAIL, email);
        values.put(DatabaseHelper.COL_ROLE, 0);
        values.put(DatabaseHelper.COL_CREATED_AT, currentDate);

        SQLiteDatabase db = this.getWritableDatabase();
        long insert = db.insert(DatabaseHelper.TABLE_USER, null, values);
        db.close();

        return insert;
    }

    @SuppressLint("Range")
    public UserModel getInfoAccountByUsername(String username, String password) {
        UserModel userAccount = new UserModel();

        SQLiteDatabase db = this.getReadableDatabase(); // Tạo cục bộ, không dùng biến toàn cục
        try {
            String[] cols = {
                    DatabaseHelper.COL_ID,
                    DatabaseHelper.COL_USERNAME,
                    DatabaseHelper.COL_EMAIL,
                    DatabaseHelper.COL_ROLE
            };

            String condition = DatabaseHelper.COL_USERNAME + " =? AND " + DatabaseHelper.COL_PASSWORD + " =?";
            String[] params = {username, password};

            Cursor data = db.query(DatabaseHelper.TABLE_USER, cols, condition, params, null, null, null);
            if (data.moveToFirst()) {
                userAccount.setId(data.getInt(data.getColumnIndex(DatabaseHelper.COL_ID)));
                userAccount.setUsername(data.getString(data.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                userAccount.setEmail(data.getString(data.getColumnIndex(DatabaseHelper.COL_EMAIL)));
                userAccount.setRole(data.getInt(data.getColumnIndex(DatabaseHelper.COL_ROLE)));
            }

            data.close();
        } finally {
            db.close(); // Đóng an toàn
        }

        return userAccount;
    }
}
