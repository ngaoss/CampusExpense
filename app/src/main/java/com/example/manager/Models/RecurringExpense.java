package com.example.manager.Models;
public class RecurringExpense {
    private long id;
    private double amount;
    private String description;
    private String startDate;
    private String endDate;
    private String frequency;
    private Category category;

    public RecurringExpense(long id, double amount, String description, String startDate, String endDate, String frequency, Category category) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.category = category;
    }

    public long getId() { return id; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getFrequency() { return frequency; }
    public Category getCategory() { return category; }
}