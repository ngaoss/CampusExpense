package com.example.manager;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private TextView tvTotalBalance, tvCashAmount, tvTotalSpent, tvTotalIncome;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvCashAmount = findViewById(R.id.tvCashAmount);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        pieChart = findViewById(R.id.pieChart);

        updateFinancialSummary();

        setupPieChart();
    }

    private void updateFinancialSummary() {
        double totalSpentThisMonth = calculateTotalSpent();
        double totalIncomeThisMonth = calculateTotalIncome();
        double currentBalance = totalIncomeThisMonth - totalSpentThisMonth;

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvTotalSpent.setText(formatter.format(totalSpentThisMonth) + " đ");
        tvTotalIncome.setText(formatter.format(totalIncomeThisMonth) + " đ");
        tvTotalBalance.setText(formatter.format(currentBalance) + " đ");
    }

    private double calculateTotalSpent() {
        return 2150000;
    }

    private double calculateTotalIncome() {
        return 3000000;
    }

    private void setupPieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(500000, "Food"));
        entries.add(new PieEntry(300000, "Transport"));
        entries.add(new PieEntry(200000, "Entertainment"));
        entries.add(new PieEntry(100000, "Bills"));


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#FFEB3B"),
                Color.parseColor("#F44336"),
                Color.parseColor("#2196F3")
        );
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleAlpha(0);

        Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setWordWrapEnabled(true);

        pieChart.invalidate();
    }
}