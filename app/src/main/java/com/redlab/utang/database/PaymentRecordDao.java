package com.redlab.utang.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.redlab.utang.models.PaymentRecord;

import java.util.List;

@Dao
public interface PaymentRecordDao {

    @Insert
    long insert(PaymentRecord record);

    @Delete
    void delete(PaymentRecord record);

    @Query("SELECT * FROM payment_records WHERE debtorId = :debtorId ORDER BY paymentDate DESC")
    LiveData<List<PaymentRecord>> getPaymentsForDebtor(long debtorId);

    @Query("SELECT * FROM payment_records WHERE debtorId = :debtorId ORDER BY paymentDate ASC")
    List<PaymentRecord> getPaymentsForDebtorSync(long debtorId);

    @Query("SELECT * FROM payment_records ORDER BY paymentDate DESC")
    List<PaymentRecord> getAllPaymentsSync();
}
