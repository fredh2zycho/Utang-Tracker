package com.redlab.utang.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.redlab.utang.models.Debtor;

import java.util.List;

@Dao
public interface DebtorDao {

    @Insert
    long insert(Debtor debtor);

    @Update
    void update(Debtor debtor);

    @Delete
    void delete(Debtor debtor);

    @Query("SELECT * FROM debtors ORDER BY name ASC")
    LiveData<List<Debtor>> getAllDebtorsAlphabetical();

    @Query("SELECT * FROM debtors ORDER BY name ASC")
    List<Debtor> getAllDebtorsSync();

    @Query("SELECT * FROM debtors WHERE id = :id")
    LiveData<Debtor> getDebtorById(long id);

    @Query("SELECT * FROM debtors WHERE id = :id")
    Debtor getDebtorByIdSync(long id);

    @Query("UPDATE debtors SET " +
           "amountPaid = amountPaid + :amount, " +
           "isFullyPaid = CASE WHEN (amountPaid + :amount) >= (totalAmount + penaltyAmount) THEN 1 ELSE 0 END, " +
           "fullyPaidDate = CASE WHEN (amountPaid + :amount) >= (totalAmount + penaltyAmount) THEN :date ELSE fullyPaidDate END " +
           "WHERE id = :id")
    void addPayment(long id, double amount, String date);

    @Query("UPDATE debtors SET penaltyAmount = penaltyAmount + :penalty WHERE id = :id")
    void addPenalty(long id, double penalty);
}
