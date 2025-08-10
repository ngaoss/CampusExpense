package com.example.manager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager.Models.Category;
import com.example.manager.Models.CategorySummary;
import com.example.manager.R;
import com.example.manager.data.AddEditExpenseActivity;
import com.example.manager.data.BudgetRepository;
import com.example.manager.data.CategorySummaryAdapter;
import com.example.manager.data.ExpenseDao;
import com.example.manager.data.ExpenseRepository;
import com.example.manager.data.RecurringExpenseRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private FloatingActionButton fabAdd;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wallet);
        budgetRepository = new BudgetRepository(this);
        expenseRepository = new ExpenseRepository(this);
        recurringExpenseRepository = new RecurringExpenseRepository(this);
        expenseDao = new ExpenseDao(this);
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
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(ConclusionActivity.this, AddEditExpenseActivity.class);
            startActivity(intent);
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_overview) {
                Toast.makeText(this, "Move to Overview", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_expense) {
                Toast.makeText(this, "Move to Expense", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_wallet) {
                Toast.makeText(this, "Move to Wallet", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_account) {
                Toast.makeText(this, "Move to account", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_overview);
    }

}
