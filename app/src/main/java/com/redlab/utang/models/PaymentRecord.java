package com.redlab.utang.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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
    private String paymentDate;
    private String photoPath;       // path to timestamped photo receipt
    private String recordType;      // "PAYMENT" or "PENALTY" or "INTEREST"

    public PaymentRecord() {}

    @Ignore
    public PaymentRecord(long debtorId, double amount, String paymentDate,
                         String photoPath, String recordType) {
        this.debtorId    = debtorId;
        this.amount      = amount;
        this.paymentDate = paymentDate;
        this.photoPath   = photoPath;
        this.recordType  = recordType;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getDebtorId() { return debtorId; }
    public void setDebtorId(long debtorId) { this.debtorId = debtorId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }
}
