package com.example.manager.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager.Models.Expense;
import com.example.manager.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> items;
    private final NumberFormat currencyFormat;

    public interface OnItemClickListener {
        void onItemClick(Expense expense);
        void onItemLongClick(Expense expense);
    }
    private OnItemClickListener listener;

    public ExpenseAdapter(Context context, List<Object> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerTitle.setText((String) items.get(position));
        } else {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            Expense expense = (Expense) items.get(position);

            itemHolder.description.setText(expense.getDescription());
            itemHolder.category.setText(expense.getCategory().getName());
            itemHolder.amount.setText(currencyFormat.format(expense.getAmount()));
            itemHolder.date.setText(expense.getDate());

            int iconResId = getIconForCategory(expense.getCategory().getName());
            itemHolder.categoryIcon.setImageResource(iconResId);
            itemHolder.itemView.setOnClickListener(v -> listener.onItemClick(expense));
            itemHolder.itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(expense);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.tv_section_header);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView description, category, amount, date;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.iv_category_icon);
            description = itemView.findViewById(R.id.tv_expense_description);
            category = itemView.findViewById(R.id.tv_expense_category);
            amount = itemView.findViewById(R.id.tv_expense_amount);
            date = itemView.findViewById(R.id.tv_expense_date);
        }
    }
    private int getIconForCategory(String categoryName) {
        if (categoryName == null) return R.drawable.ic_category_placeholder;

        switch (categoryName.toLowerCase()) {
            case "food":
                return R.drawable.ic_food;
            case "transport":
                return R.drawable.ic_transport;
            case "bills":
                return R.drawable.ic_bills;
            case "entertainment":
                return R.drawable.ic_entertainment;
            default:
                return R.drawable.ic_category_placeholder;
        }
    }
}