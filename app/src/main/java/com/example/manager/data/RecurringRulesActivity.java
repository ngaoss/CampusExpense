package com.example.manager.data;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.manager.R;
import com.example.manager.Models.RecurringExpense;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecurringRulesActivity extends AppCompatActivity {

    private ListView listViewRecurringRules;
    private ExpenseDao expenseDao;
    private List<RecurringExpense> ruleList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_rules);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        expenseDao = new ExpenseDao(this);
        listViewRecurringRules = findViewById(R.id.listViewRecurringRules);

        listViewRecurringRules.setOnItemClickListener((parent, view, position, id) -> {
            RecurringExpense selectedRule = ruleList.get(position);

            Intent intent = new Intent(RecurringRulesActivity.this, AddRecurringExpenseActivity.class);

            intent.putExtra(AddRecurringExpenseActivity.EXTRA_RULE_ID, selectedRule.getId());

            startActivity(intent);
        });

        listViewRecurringRules.setOnItemLongClickListener((parent, view, position, id) -> {
            RecurringExpense selectedRule = ruleList.get(position);

            showDeleteConfirmationDialog(selectedRule);

            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRules();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadRules() {
        ruleList.clear();
        ruleList.addAll(expenseDao.getAllRecurringRules());

        List<String> displayList = new ArrayList<>();
        for (RecurringExpense rule : ruleList) {
            String displayText = String.format(Locale.US, "'%s' - %,.0f (%s)",
                    rule.getDescription(),
                    rule.getAmount(),
                    rule.getFrequency()
            );
            displayList.add(displayText);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listViewRecurringRules.setAdapter(adapter);
    }

    private void showDeleteConfirmationDialog(final RecurringExpense ruleToDelete) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Rule")
                .setMessage("Are you sure you want to delete this recurring rule permanently?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    expenseDao.deleteRecurringRule(ruleToDelete.getId());
                    Toast.makeText(this, "Rule deleted.", Toast.LENGTH_SHORT).show();
                    loadRules();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}