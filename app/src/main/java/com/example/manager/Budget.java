package com.example.manager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.manager.data.BudgetRepository; // Đảm bảo import đúng
import com.example.manager.R; // Đảm bảo import đúng

public class Budget extends AppCompatActivity {

    private EditText edtBudgetAmount;
    private Spinner spnCategory;
    private Button btnSaveBudget;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Ánh xạ các View
        edtBudgetAmount = findViewById(R.id.edtBudgetAmount);
        spnCategory = findViewById(R.id.spnCategory);
        btnSaveBudget = findViewById(R.id.btnSaveBudget);
        btnBack = findViewById(R.id.btnBack);

        // Thiết lập sự kiện cho nút Lưu
        btnSaveBudget.setOnClickListener(v -> saveBudget());

        // Thiết lập sự kiện cho nút Quay lại
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveBudget() {
        // Lấy danh mục được chọn từ Spinner
        String category = spnCategory.getSelectedItem().toString();

        // Lấy chuỗi số tiền từ EditText
        String amountStr = edtBudgetAmount.getText().toString();

        // Kiểm tra xem người dùng đã nhập số tiền chưa
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Please enter the amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double budgetAmount = Double.parseDouble(amountStr);

            // Kiểm tra số tiền phải lớn hơn 0
            if (budgetAmount <= 0) {
                Toast.makeText(this, "Budget amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Khởi tạo repository
            BudgetRepository repo = new BudgetRepository(this);

            // Gọi đến hàm mới để cập nhật hoặc thêm mới
            long result = repo.saveOrUpdateBudget(category, budgetAmount);

            // Kiểm tra kết quả
            // result sẽ là 1 nếu cập nhật thành công, hoặc là id của hàng mới nếu insert thành công
            if (result > 0) {
                Toast.makeText(this, "The budget has been saved", Toast.LENGTH_SHORT).show();
                finish(); // Đóng màn hình sau khi lưu thành công
            } else {
                Toast.makeText(this, "Error saving budget", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            // Bắt lỗi nếu người dùng nhập ký tự không phải là số
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }
}