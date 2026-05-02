package com.redlab.utang.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "debtors")
public class Debtor {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private double totalAmount;
    private double amountPaid;
    private double interestRate;      // % per month, 0 = no interest
    private double penaltyAmount;     // accumulated penalty
    private String startDate;
    private String fullyPaidDate;
    private String notes;
    private boolean isFullyPaid;

    public Debtor() {}

    @Ignore
    public Debtor(String name, double totalAmount, String startDate, String notes) {
        this.name        = name;
        this.totalAmount = totalAmount;
        this.amountPaid  = 0;
        this.interestRate = 0;
        this.penaltyAmount = 0;
        this.startDate   = startDate;
        this.notes       = notes;
        this.isFullyPaid = false;
    }

    public double getRemainingBalance() {
        return Math.max(0, totalAmount + penaltyAmount - amountPaid);
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
    public double getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(double penaltyAmount) { this.penaltyAmount = penaltyAmount; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getFullyPaidDate() { return fullyPaidDate; }
    public void setFullyPaidDate(String fullyPaidDate) { this.fullyPaidDate = fullyPaidDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isFullyPaid() { return isFullyPaid; }
    public void setFullyPaid(boolean fullyPaid) { isFullyPaid = fullyPaid; }
}
