// Tạo file mới: ReportActivity.java
package com.example.manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager.Models.Category;
import com.example.manager.Models.Transaction;
import com.example.manager.data.ExpenseDao;
import com.example.manager.data.ExpenseRepository;
import com.example.manager.data.TransactionAdapter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.widget.ImageButton;

public class ReportActivity extends AppCompatActivity {

    private TextView tvStartDate, tvEndDate, tvFilteredTotal;
    private Spinner spinnerCategory;
    private Button btnApplyFilter;
    private RecyclerView recyclerView;

    private TransactionAdapter adapter;
    private ExpenseRepository expenseRepository;
    private ExpenseDao expenseDao;

    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private List<Category> categories;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setTitle("Expense Reports");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        expenseRepository = new ExpenseRepository(this);
        expenseDao = new ExpenseDao(this);
        btnBack = findViewById(R.id.btn_report_back);

        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvFilteredTotal = findViewById(R.id.tvFilteredTotal);
        spinnerCategory = findViewById(R.id.spinnerReportCategory);
        btnApplyFilter = findViewById(R.id.btnApplyFilter);
        recyclerView = findViewById(R.id.recycler_view_transactions);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);

        setupFilters();

        tvStartDate.setOnClickListener(v -> showDatePicker(true));
        tvEndDate.setOnClickListener(v -> showDatePicker(false));
        btnApplyFilter.setOnClickListener(v -> applyFilters());
        btnBack.setOnClickListener(v -> {
            finish(); // or onBackPressed();
        });
        btnApplyFilter.setOnClickListener(v -> applyFilters());
    }

    private void setupFilters() {
        startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
        endDateCalendar = Calendar.getInstance();
        updateDateViews();

        categories = new ArrayList<>();
        categories.add(new Category(-1, "All Categories"));
        categories.addAll(expenseDao.getAllCategories());

        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void applyFilters() {
        String startDate = sdf.format(startDateCalendar.getTime());
        String endDate = sdf.format(endDateCalendar.getTime());

        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        long categoryId = selectedCategory.getId();

        List<Transaction> filteredTransactions = expenseRepository.getFilteredTransactions(startDate, endDate, categoryId);
        adapter.setData(filteredTransactions);

        double total = 0;
        for (Transaction t : filteredTransactions) {
            total += t.getAmount();
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvFilteredTotal.setText("Filtered Total: " + currencyFormat.format(total));
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar cal = isStartDate ? startDateCalendar : endDateCalendar;
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(year, month, dayOfMonth);
            updateDateViews();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateViews() {
        tvStartDate.setText(sdf.format(startDateCalendar.getTime()));
        tvEndDate.setText(sdf.format(endDateCalendar.getTime()));
    }
}