package com.example.manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager.Models.Category;
import com.example.manager.Models.CategorySummary;
import com.example.manager.data.BudgetRepository;
import com.example.manager.data.CategorySummaryAdapter;
import com.example.manager.data.ExpenseDao;
import com.example.manager.data.ExpenseRepository;
import com.example.manager.data.RecurringExpenseRepository;

import java.util.ArrayList;
import java.util.List;

public class ConclusionActivity extends AppCompatActivity {

    private Button btnAddBudget;
    private Button btnManageExpenses;

    private RecyclerView recyclerView;
    private CategorySummaryAdapter adapter;

    private BudgetRepository budgetRepository;
    private ExpenseRepository expenseRepository;
    private RecurringExpenseRepository recurringExpenseRepository;
    private ExpenseDao expenseDao;
    private Button btnViewReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conclusion);

        budgetRepository = new BudgetRepository(this);
        expenseRepository = new ExpenseRepository(this);
        recurringExpenseRepository = new RecurringExpenseRepository(this);
        expenseDao = new ExpenseDao(this);
        btnViewReport = findViewById(R.id.btn_view_reports);

        btnAddBudget = findViewById(R.id.btn_add_budget);
        btnManageExpenses = findViewById(R.id.btn_manage_expenses);

        recyclerView = findViewById(R.id.recycler_view_category_summaries);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategorySummaryAdapter();
        recyclerView.setAdapter(adapter);

        setupEventListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayData();
    }

    private void loadAndDisplayData() {
        List<CategorySummary> summaries = new ArrayList<>();
        List<Category> allCategories = expenseDao.getAllCategories();

        for (Category category : allCategories) {
            String categoryName = category.getName();
            long categoryId = category.getId();

            double budgetAmount = budgetRepository.getBudgetForCategory(categoryName);

            double oneTimeExpenses = expenseRepository.getTotalExpensesForCategory(categoryId);

            double recurringExpenses = recurringExpenseRepository.getEstimatedMonthlyRecurringForCategory(categoryId);

            double totalExpenses = oneTimeExpenses + recurringExpenses;
            double remainingBudget = budgetAmount - totalExpenses;

            summaries.add(new CategorySummary(categoryName, budgetAmount, totalExpenses, remainingBudget));
        }

        adapter.setData(summaries);
    }

    private void setupEventListeners() {
        btnAddBudget.setOnClickListener(v -> {
            Intent intent = new Intent(ConclusionActivity.this, Budget.class);
            startActivity(intent);
        });

        btnManageExpenses.setOnClickListener(v -> {
            Intent intent = new Intent(ConclusionActivity.this, MainActivity.class);
            startActivity(intent);
        });
        btnViewReport.setOnClickListener(v -> {
            Intent intent = new Intent(ConclusionActivity.this, ReportActivity.class);
            startActivity(intent);
        });

    }
}