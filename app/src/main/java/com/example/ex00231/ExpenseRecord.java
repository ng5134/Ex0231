package com.example.ex00231;

import java.io.Serializable;

public class ExpenseRecord implements Serializable {
    private String keyID;
    private String Description;
    private double Amount;
    private String Category;
    private String Date;
    public ExpenseRecord() { //default firebase constructor
    }
    public ExpenseRecord(String keyID, String description, double amount, String category, String date) {
        this.keyID = keyID;
        this.Description = description;
        this.Amount = amount;
        this.Category = category;
        this.Date = date;
    }
    //getters and setters
    public String getKeyID() { return keyID;
    }
    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getDescription() {
        return Description;
    }
    public void setDescription(String description) {
        Description = description;
    }

    public double getAmount() {
        return Amount;
    }
    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getCategory() {
        return Category;
    }
    public void setCategory(String category) {
        Category = category;
    }

    public String getDate() {
        return Date;
    }
    public void setDate(String date) {
        Date = date;
    }


}
