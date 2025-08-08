package com.example.manager.Models;
public class Expense {
    private long id;
    private double amount;
    private String description;
    private String date;
    private Category category;
    private boolean isRecurring;

    public Expense(long id, double amount, String description, String date, Category category, boolean isRecurring) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category = category;
        this.isRecurring = isRecurring;
    }

    public long getId() { return id; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public Category getCategory() { return category; }
    public boolean isRecurring() { return isRecurring; }
}