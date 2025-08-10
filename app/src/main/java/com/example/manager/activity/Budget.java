package com.example.manager.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.manager.data.BudgetRepository;
import com.example.manager.R;

public class Budget extends AppCompatActivity {

    private EditText edtBudgetAmount;
    private Spinner spnCategory;
    private Button btnSaveBudget;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        edtBudgetAmount = findViewById(R.id.edtBudgetAmount);
        spnCategory = findViewById(R.id.spnCategory);
        btnSaveBudget = findViewById(R.id.btnSaveBudget);
        btnBack = findViewById(R.id.btnBack);

        btnSaveBudget.setOnClickListener(v -> saveBudget());

        btnBack.setOnClickListener(v -> finish());
    }

    private void saveBudget() {
        String category = spnCategory.getSelectedItem().toString();

        String amountStr = edtBudgetAmount.getText().toString();

        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Please enter the amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double budgetAmount = Double.parseDouble(amountStr);

            if (budgetAmount <= 0) {
                Toast.makeText(this, "Budget amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            BudgetRepository repo = new BudgetRepository(this);

            long result = repo.saveOrUpdateBudget(category, budgetAmount);

            if (result > 0) {
                Toast.makeText(this, "The budget has been saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving budget", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }
}