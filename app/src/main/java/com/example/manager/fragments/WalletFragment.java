package com.example.manager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.manager.R;
import com.example.manager.Models.Category;
import com.example.manager.Models.CategorySummary;
import com.example.manager.data.BudgetRepository;
import com.example.manager.data.CategorySummaryAdapter;
import com.example.manager.data.ExpenseDao;
import com.example.manager.data.ExpenseRepository;
import com.example.manager.data.RecurringExpenseRepository;
import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategorySummaryAdapter adapter;
    private BudgetRepository budgetRepository;
    private ExpenseRepository expenseRepository;
    private RecurringExpenseRepository recurringExpenseRepository;
    private ExpenseDao expenseDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        budgetRepository = new BudgetRepository(getActivity());
        expenseRepository = new ExpenseRepository(getActivity());
        recurringExpenseRepository = new RecurringExpenseRepository(getActivity());
        expenseDao = new ExpenseDao(getActivity());

        recyclerView = view.findViewById(R.id.recycler_view_category_summaries);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategorySummaryAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
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
}