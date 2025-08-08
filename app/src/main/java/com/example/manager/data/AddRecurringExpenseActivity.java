package com.example.manager.data;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.manager.R;
import com.example.manager.Models.Category;
import com.example.manager.Models.RecurringExpense;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Calendar;
import java.util.Locale;

public class AddRecurringExpenseActivity extends AppCompatActivity {
    public static final String EXTRA_RULE_ID = "extra_rule_id";
    private EditText editTextAmount, editTextDescription;
    private Spinner spinnerCategory, spinnerFrequency;
    private TextView textViewStartDate;
    private Button buttonSave, buttonCancel;
    private ExpenseDao expenseDao;
    private Calendar startDate = Calendar.getInstance();
    private long ruleId = -1L;
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
        setContentView(R.layout.activity_add_recurring_expense);

        expenseDao = new ExpenseDao(this);

        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        textViewStartDate = findViewById(R.id.textViewStartDate);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        setupCategorySpinner();
        setupFrequencySpinner();

        ruleId = getIntent().getLongExtra(EXTRA_RULE_ID, -1L);

        if (ruleId != -1L) {
            setTitle("Edit Recurring Rule");
            RecurringExpense ruleToEdit = expenseDao.getRecurringRuleById(ruleId);
            if (ruleToEdit != null) {
                populateUI(ruleToEdit);
            }
        } else {
            setTitle("Add Recurring Rule");
            updateDateInView();
        }

        textViewStartDate.setOnClickListener(v -> showDatePickerDialog());
        buttonSave.setOnClickListener(v -> saveRecurringExpense());
        buttonCancel.setOnClickListener(v -> finish());
        createNotificationChannel();
        askNotificationPermission();
    }

    private void populateUI(RecurringExpense rule) {
        DecimalFormat formatter = new DecimalFormat("0.##");
        formatter.setGroupingUsed(false);
        editTextAmount.setText(formatter.format(rule.getAmount()));
        editTextDescription.setText(rule.getDescription());

        textViewStartDate.setText(rule.getStartDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            startDate.setTime(sdf.parse(rule.getStartDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Category> categories = expenseDao.getAllCategories();
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == rule.getCategory().getId()) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerFrequency.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(rule.getFrequency())) {
                spinnerFrequency.setSelection(i);
                break;
            }
        }
    }
    private void saveRecurringExpense() {
        String amountStr = editTextAmount.getText().toString();
        String description = editTextDescription.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Amount is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        Category category = (Category) spinnerCategory.getSelectedItem();
        String frequency = (String) spinnerFrequency.getSelectedItem();
        String date = textViewStartDate.getText().toString();

        RecurringExpense recurringExpense = new RecurringExpense(
                ruleId == -1L ? 0 : ruleId,
                amount, description, date, null, frequency, category
        );

        if (ruleId != -1L) {
            expenseDao.updateRecurringRule(recurringExpense);
            Toast.makeText(this, "Rule updated!", Toast.LENGTH_SHORT).show();
        } else {
            expenseDao.addRecurringExpense(recurringExpense);
            Toast.makeText(this, "Recurring rule saved!", Toast.LENGTH_SHORT).show();
        }

        checkBudgetAndNotify(category);

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
    private void setupCategorySpinner() {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenseDao.getAllCategories());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupFrequencySpinner() {
        String[] frequencies = {"DAILY", "MONTHLY" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, frequencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(adapter);
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            startDate.set(year, month, dayOfMonth);
            updateDateInView();
        }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateInView() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        textViewStartDate.setText(sdf.format(startDate.getTime()));
    }

}