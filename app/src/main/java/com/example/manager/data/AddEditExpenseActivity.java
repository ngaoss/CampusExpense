package com.example.manager.data;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat; // <-- THÊM DÒNG IMPORT NÀY

import com.example.manager.R;
import com.example.manager.Models.Category;
import com.example.manager.Models.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditExpenseActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";

    private EditText editTextAmount;
    private EditText editTextDescription;
    private TextView textViewDate;
    private Spinner spinnerCategory;
    private Button buttonSave;
    private Button buttonCancel;
    private Button buttonDelete;

    private ExpenseDao expenseDao;
    private long expenseId = -1L;
    private Calendar selectedDate = Calendar.getInstance();
    private List<Category> categories;
    private static final String CHANNEL_ID = "budget_channel";
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "You do not have the authority to send notifications, you will not receive alerts..", Toast.LENGTH_LONG).show();
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_expense);

        expenseDao = new ExpenseDao(this);

        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewDate = findViewById(R.id.textViewDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonDelete = findViewById(R.id.buttonDelete);

        setupCategorySpinner();

        expenseId = getIntent().getLongExtra(EXTRA_EXPENSE_ID, -1L);

        if (expenseId != -1L) {
            setTitle(getString(R.string.edit_expense_title));
            buttonDelete.setVisibility(View.VISIBLE);

            Expense expenseToEdit = expenseDao.getExpenseById(expenseId);
            if (expenseToEdit != null) {
                populateUI(expenseToEdit);
            }
        } else {
            setTitle(getString(R.string.add_expense_title));
            updateDateInView();
        }

        textViewDate.setOnClickListener(v -> showDatePickerDialog());
        buttonSave.setOnClickListener(v -> saveExpense());
        buttonCancel.setOnClickListener(v -> finish());
        buttonDelete.setOnClickListener(v -> deleteExpense());
        createNotificationChannel();
        askNotificationPermission();
    }

    private void populateUI(Expense expense) {
        DecimalFormat formatter = new DecimalFormat("0.##");
        formatter.setGroupingUsed(false);
        editTextAmount.setText(formatter.format(expense.getAmount()));

        editTextDescription.setText(expense.getDescription());

        textViewDate.setText(expense.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            selectedDate.setTime(sdf.parse(expense.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Category expenseCategory = expense.getCategory();
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == expenseCategory.getId()) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private void setupCategorySpinner() {
        categories = expenseDao.getAllCategories();
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInView();
        };

        new DatePickerDialog(this, dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateInView() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textViewDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void saveExpense() {
        String amountStr = editTextAmount.getText().toString();
        String description = editTextDescription.getText().toString();
        Category category = (Category) spinnerCategory.getSelectedItem();

        if (amountStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_info_required), Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String date = textViewDate.getText().toString();
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();

        if (selectedCategory == null) {
            Toast.makeText(this, getString(R.string.toast_category_required), Toast.LENGTH_SHORT).show();
            return;
        }

        Expense expense = new Expense(expenseId, amount, description, date, selectedCategory, false);

        if (expenseId == -1L) {
            expenseDao.addExpense(expense);
            Toast.makeText(this, getString(R.string.toast_expense_added), Toast.LENGTH_SHORT).show();
        } else {
            expenseDao.updateExpense(expense);
            Toast.makeText(this, getString(R.string.toast_expense_updated), Toast.LENGTH_SHORT).show();
        }
        checkBudgetAndNotify(category);
        finish();
    }

    private void deleteExpense() {
        expenseDao.deleteExpense(expenseId);
        Toast.makeText(this, getString(R.string.toast_expense_deleted), Toast.LENGTH_SHORT).show();
        finish();
    }
    private void checkBudgetAndNotify(Category category) {
        BudgetRepository budgetRepo = new BudgetRepository(this);
        ExpenseRepository expenseRepo = new ExpenseRepository(this);
        RecurringExpenseRepository recurringRepo = new RecurringExpenseRepository(this);

        double budgetLimit = budgetRepo.getBudgetForCategory(category.getName());

        if (budgetLimit > 0) {
            double oneTimeSpending = expenseRepo.getTotalExpensesForCategory(category.getId());            double recurringSpending = recurringRepo.getEstimatedMonthlyRecurringForCategory(category.getId());
            double totalEstimatedSpending = oneTimeSpending + recurringSpending;

            if (totalEstimatedSpending > budgetLimit) {
                sendBudgetWarning(category.getName(), totalEstimatedSpending, budgetLimit);
            }
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Budget Channel";
            String description = "Channel for budget warnings";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
    private void sendBudgetWarning(String category, double totalSpending, double budgetLimit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String contentText = String.format(Locale.US, "The estimated total expenditure for '%s' (%.0f) has exceeded the budget (%.0f)!",
                category, totalSpending, budgetLimit);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Budget Overspend Warning")
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(this).notify(1002, builder.build());
    }
}