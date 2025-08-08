package com.example.manager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.manager.data.AddRecurringExpenseActivity;
import com.example.manager.data.RecurringRulesActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;
import com.example.manager.data.AddEditExpenseActivity;
import com.example.manager.data.ExpenseDao;
import com.example.manager.Models.Expense;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private ListView listViewExpenses;
    private FloatingActionButton fabAddExpense;
    private ExpenseDao expenseDao;
    private Button buttonAddRecurring;
    private Button buttonManageRules;
    private ArrayAdapter<String> expenseAdapter;
    private List<Expense> expenseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        expenseDao = new ExpenseDao(this);
        listViewExpenses = findViewById(R.id.listViewExpenses);
        fabAddExpense = findViewById(R.id.fabAddExpense);
        buttonAddRecurring = findViewById(R.id.buttonAddRecurring);
        buttonManageRules = findViewById(R.id.buttonManageRules);
        if (expenseDao.getAllCategories().isEmpty()) {
            expenseDao.addCategory(getString(R.string.category_food));
            expenseDao.addCategory(getString(R.string.category_transport));
            expenseDao.addCategory(getString(R.string.category_bills));
            expenseDao.addCategory(getString(R.string.category_entertainment));
        }

        fabAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditExpenseActivity.class);
            startActivity(intent);
        });

        buttonAddRecurring.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRecurringExpenseActivity.class);
            startActivity(intent);
        });
        buttonManageRules.setOnClickListener(v -> {
                    startActivity(new Intent(MainActivity.this, RecurringRulesActivity.class));
                });
        listViewExpenses.setOnItemClickListener((parent, view, position, id) -> {
            Expense selectedExpense = expenseList.get(position);
            if (selectedExpense.isRecurring()) {
                Toast.makeText(MainActivity.this, getString(R.string.edit_recurring_warning), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, AddEditExpenseActivity.class);
            intent.putExtra(AddEditExpenseActivity.EXTRA_EXPENSE_ID, selectedExpense.getId());
            startActivity(intent);
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
            loadExpenses();
    }

    private void loadExpenses() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        expenseList.clear();
        String recurringPrefix = getString(R.string.recurring_prefix);
        expenseList.addAll(expenseDao.getExpensesForMonth(year, month, recurringPrefix));

        List<String> displayList = new ArrayList<>();
        for (Expense expense : expenseList) {
            String currencyUnit = getString(R.string.currency_unit);
            String displayText = String.format(Locale.US, "%s: %s - %,.0f %s (%s)",
                    expense.getDate(),
                    expense.getDescription(),
                    expense.getAmount(),
                    currencyUnit,
                    expense.getCategory().getName());
            displayList.add(displayText);
        }

        expenseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listViewExpenses.setAdapter(expenseAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
