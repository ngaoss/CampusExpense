package com.example.manager.data; // Hãy chắc chắn package này đúng với dự án của bạn

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BudgetRepository {

    private final DatabaseHelper dbHelper;

    public BudgetRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Cập nhật hoặc Thêm mới (Upsert) một khoản ngân sách cho một danh mục.
     * Nếu ngân sách cho danh mục này đã tồn tại, nó sẽ cập nhật số tiền.
     * Nếu chưa, nó sẽ tạo một mục mới.
     * @param categoryName Tên của danh mục.
     * @param amount Số tiền ngân sách mới.
     * @return Số hàng bị ảnh hưởng (>0 nếu thành công).
     */
    public long saveOrUpdateBudget(String categoryName, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 1. Tạo một "gói" chứa dữ liệu để cập nhật hoặc thêm mới
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_BUDGET_CATEGORY, categoryName);
        values.put(DatabaseHelper.KEY_AMOUNT, amount);

        // 2. Thử cập nhật hàng đã tồn tại trước
        // Lệnh update sẽ tìm một hàng có categoryName khớp và cập nhật số tiền
        int rowsAffected = db.update(
                DatabaseHelper.TABLE_BUDGETS,
                values,
                DatabaseHelper.KEY_BUDGET_CATEGORY + " = ?",
                new String[]{categoryName}
        );

        long result;
        // 3. Nếu không có hàng nào được cập nhật (rowsAffected == 0),
        // có nghĩa là chưa có ngân sách cho danh mục này, vì vậy chúng ta sẽ thêm mới.
        if (rowsAffected == 0) {
            // Thực hiện lệnh thêm mới (insert)
            result = db.insert(DatabaseHelper.TABLE_BUDGETS, null, values);
        } else {
            // Nếu cập nhật thành công, kết quả là số hàng đã bị ảnh hưởng
            result = rowsAffected;
        }

        db.close();
        return result;
    }

    /**
     * Lấy ngân sách đã đặt cho một danh mục cụ thể.
     * @param categoryName Tên của danh mục cần tìm.
     * @return Số tiền ngân sách. Trả về 0 nếu không tìm thấy.
     */
    public double getBudgetForCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        double budgetAmount = 0;

        try {
            cursor = db.query(
                    DatabaseHelper.TABLE_BUDGETS,
                    new String[]{DatabaseHelper.KEY_AMOUNT}, // Chỉ lấy cột số tiền
                    DatabaseHelper.KEY_BUDGET_CATEGORY + " = ?", // Điều kiện WHERE
                    new String[]{categoryName}, // Giá trị cho điều kiện WHERE
                    null, null, null, "1" // Giới hạn chỉ lấy 1 kết quả
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