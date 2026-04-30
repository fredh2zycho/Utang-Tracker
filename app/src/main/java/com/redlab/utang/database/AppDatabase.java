package com.redlab.utang.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.redlab.utang.models.Debtor;
import com.redlab.utang.models.PaymentRecord;

@Database(entities = {Debtor.class, PaymentRecord.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract DebtorDao debtorDao();
    public abstract PaymentRecordDao paymentRecordDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "utang_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
