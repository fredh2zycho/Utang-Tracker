package com.redlab.utang.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(
    tableName = "payment_records",
    foreignKeys = @ForeignKey(
        entity = Debtor.class,
        parentColumns = "id",
        childColumns = "debtorId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("debtorId")}
)
public class PaymentRecord {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long debtorId;
    private double amount;
    private String paymentDate;       // ISO date string
    private String receiptType;       // "BIOMETRIC" or "PHOTO"
    private String photoPath;         // path to photo if receiptType == PHOTO
    private boolean biometricVerified;

    public PaymentRecord() {}

    @Ignore
    public PaymentRecord(long debtorId, double amount, String paymentDate,
                         String receiptType, String photoPath, boolean biometricVerified) {
        this.debtorId = debtorId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.receiptType = receiptType;
        this.photoPath = photoPath;
        this.biometricVerified = biometricVerified;
    }

    // --- Getters & Setters ---

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getDebtorId() { return debtorId; }
    public void setDebtorId(long debtorId) { this.debtorId = debtorId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getReceiptType() { return receiptType; }
    public void setReceiptType(String receiptType) { this.receiptType = receiptType; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public boolean isBiometricVerified() { return biometricVerified; }
    public void setBiometricVerified(boolean biometricVerified) { this.biometricVerified = biometricVerified; }
}
