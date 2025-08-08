package com.example.manager.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager.Models.CategorySummary;
import com.example.manager.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategorySummaryAdapter extends RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder> {

    private List<CategorySummary> categorySummaries;
    private final NumberFormat currencyFormat;

    public CategorySummaryAdapter() {
        this.categorySummaries = new ArrayList<>();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    public void setData(List<CategorySummary> newSummaries) {
        this.categorySummaries.clear();
        this.categorySummaries.addAll(newSummaries);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategorySummary summary = categorySummaries.get(position);
        holder.tvCategoryName.setText(summary.getCategoryName());
        holder.tvBudgetAmount.setText(currencyFormat.format(summary.getBudgetAmount()));
        holder.tvTotalExpenses.setText(currencyFormat.format(summary.getTotalExpenses()));
        holder.tvRemainingBudget.setText(currencyFormat.format(summary.getRemainingBudget()));

        if (summary.getRemainingBudget() < 0) {
            holder.tvRemainingBudget.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvRemainingBudget.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return categorySummaries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        TextView tvBudgetAmount;
        TextView tvTotalExpenses;
        TextView tvRemainingBudget;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_item_category_name);
            tvBudgetAmount = itemView.findViewById(R.id.tv_item_budget_amount);
            tvTotalExpenses = itemView.findViewById(R.id.tv_item_total_expenses);
            tvRemainingBudget = itemView.findViewById(R.id.tv_item_remaining_budget);
        }
    }
}