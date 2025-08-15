package com.example.manager.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.manager.R;
import com.example.manager.data.BudgetRepository;
import com.example.manager.data.ExpenseDao;
import com.example.manager.Models.Category;
import com.example.manager.Models.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class OverviewFragment extends Fragment {

    private TextView tvTotalBalance, tvCashAmount, tvTotalSpent, tvTotalIncome;
    private PieChart pieChart;
    private ExpenseDao expenseDao;
    private BudgetRepository budgetRepository;
    private NumberFormat currencyFormatter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        expenseDao = new ExpenseDao(getContext());
        budgetRepository = new BudgetRepository(getContext());
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        tvCashAmount = view.findViewById(R.id.tvCashAmount);
        tvTotalSpent = view.findViewById(R.id.tvTotalSpent);
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        pieChart = view.findViewById(R.id.pieChart);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFinancialData();
    }

    private void loadFinancialData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        List<Expense> allMonthlyExpenses = expenseDao.getExpensesForMonth(year, month, getString(R.string.recurring_prefix));

        List<Category> allCategories = expenseDao.getAllCategories();

        updateFinancialSummary(allMonthlyExpenses, allCategories);
        setupPieChart(allMonthlyExpenses);
    }

    private void updateFinancialSummary(List<Expense> allExpenses, List<Category> allCategories) {
        double totalSpentThisMonth = allExpenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        double totalBudget = allCategories.stream()
                .mapToDouble(category -> budgetRepository.getBudgetForCategory(category.getName()))
                .sum();

        double currentBalance = totalBudget - totalSpentThisMonth;

        tvTotalSpent.setText(currencyFormatter.format(totalSpentThisMonth));
        tvTotalIncome.setText(currencyFormatter.format(totalBudget));
        tvTotalBalance.setText(currencyFormatter.format(currentBalance));
        tvCashAmount.setText(currencyFormatter.format(currentBalance));
    }

    private void setupPieChart(List<Expense> allExpenses) {
        Map<String, Double> expensesByCategory = allExpenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getCategory().getName(),
                        Collectors.summingDouble(Expense::getAmount)
                ));

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        if (entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("No expense data for this month.");
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(3f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterText("Monthly Spending");
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextSize(18f);
        pieChart.setEntryLabelColor(Color.BLACK);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);

        pieChart.animateY(1200);
        pieChart.invalidate();
    }
}