package com.example.manager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager.R;
import com.example.manager.data.ExpenseAdapter;
import com.example.manager.data.AddEditExpenseActivity;
import com.example.manager.data.AddRecurringExpenseActivity;
import com.example.manager.data.ExpenseDao;
import com.example.manager.Models.Expense;
import com.example.manager.Models.RecurringExpense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private ExpenseDao expenseDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        expenseDao = new ExpenseDao(getActivity());
        recyclerView = view.findViewById(R.id.recyclerViewExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void loadExpenses() {
        List<Object> displayList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        List<Expense> oneTimeExpenses = expenseDao.getOneTimeExpensesForMonth(year, month);

        if (!oneTimeExpenses.isEmpty()) {
            displayList.add("One-Time Expenses");
            displayList.addAll(oneTimeExpenses);
        }

        List<RecurringExpense> recurringRules = expenseDao.getAllRecurringRules();

        if (!recurringRules.isEmpty()) {
            displayList.add("Recurring Rules");
            for (RecurringExpense rule : recurringRules) {
                displayList.add(new Expense(rule.getId(), rule.getAmount(), rule.getDescription(), rule.getFrequency(), rule.getCategory(), true));
            }
        }

        adapter = new ExpenseAdapter(getContext(), displayList, new ExpenseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Expense expense) {
                handleItemClick(expense);
            }

            @Override
            public void onItemLongClick(Expense expense) {
                showDeleteConfirmationDialog(expense);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void handleItemClick(Expense expense) {
        if (expense.isRecurring()) {
            Intent intent = new Intent(getActivity(), AddRecurringExpenseActivity.class);
            intent.putExtra(AddRecurringExpenseActivity.EXTRA_RULE_ID, expense.getId());
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), AddEditExpenseActivity.class);
            intent.putExtra(AddEditExpenseActivity.EXTRA_EXPENSE_ID, expense.getId());
            startActivity(intent);
        }
    }

    private void showDeleteConfirmationDialog(final Expense expense) {
        String title = expense.isRecurring() ? "Delete Recurring Rule" : "Delete Expense";
        String message = "Are you sure you want to delete this item permanently?";

        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (expense.isRecurring()) {
                        expenseDao.deleteRecurringRule(expense.getId());
                    } else {
                        expenseDao.deleteExpense(expense.getId());
                    }
                    Toast.makeText(getContext(), "Item deleted.", Toast.LENGTH_SHORT).show();
                    loadExpenses();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}