package com.example.manager.Models;

public class CategorySummary {
    private String categoryName;
    private double budgetAmount;
    private double totalExpenses;
    private double remainingBudget;

    public CategorySummary(String categoryName, double budgetAmount, double totalExpenses, double remainingBudget) {
        this.categoryName = categoryName;
        this.budgetAmount = budgetAmount;
        this.totalExpenses = totalExpenses;
        this.remainingBudget = remainingBudget;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public double getRemainingBudget() {
        return remainingBudget;
    }
}