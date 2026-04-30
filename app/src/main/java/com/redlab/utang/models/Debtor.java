package com.redlab.utang.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "debtors")
public class Debtor {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private double totalAmount;
    private double amountPaid;
    private String startDate;       // ISO date string
    private String fullyPaidDate;   // null if not yet paid
    private String fingerprintKey;  // stored biometric key identifier
    private String notes;
    private boolean isFullyPaid;

    public Debtor() {}

    @Ignore
    public Debtor(String name, double totalAmount, String startDate, String notes) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.amountPaid = 0;
        this.startDate = startDate;
        this.notes = notes;
        this.isFullyPaid = false;
    }

    // --- Getters & Setters ---

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getFullyPaidDate() { return fullyPaidDate; }
    public void setFullyPaidDate(String fullyPaidDate) { this.fullyPaidDate = fullyPaidDate; }

    public String getFingerprintKey() { return fingerprintKey; }
    public void setFingerprintKey(String fingerprintKey) { this.fingerprintKey = fingerprintKey; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isFullyPaid() { return isFullyPaid; }
    public void setFullyPaid(boolean fullyPaid) { isFullyPaid = fullyPaid; }

    public double getRemainingBalance() {
        return totalAmount - amountPaid;
    }
}
