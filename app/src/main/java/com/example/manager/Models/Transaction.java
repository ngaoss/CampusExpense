package com.example.manager.Models;

public class Transaction {
    private long id;
    private String description;
    private double amount;
    private String date;
    private String categoryName;
    private String type;

    public Transaction(long id, String description, double amount, String date, String categoryName, String type) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.categoryName = categoryName;
        this.type = type;
    }

    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getCategoryName() { return categoryName; }
    public String getType() { return type; }
}