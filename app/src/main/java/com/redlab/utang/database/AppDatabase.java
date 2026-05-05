package com.redlab.utang.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.redlab.utang.models.Debtor;
import com.redlab.utang.models.PaymentRecord;

@Database(entities = {Debtor.class, PaymentRecord.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract DebtorDao debtorDao();
    public abstract PaymentRecordDao paymentRecordDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE debtors ADD COLUMN interestRate REAL NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE debtors ADD COLUMN penaltyAmount REAL NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE payment_records ADD COLUMN recordType TEXT");
            db.execSQL("UPDATE payment_records SET recordType = 'PAYMENT'");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override public void migrate(SupportSQLiteDatabase db) {
            // Add paymentNote column — stores items/products covered by this payment
            db.execSQL("ALTER TABLE payment_records ADD COLUMN paymentNote TEXT");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "utang_database"
                    )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Closes the database connection and destroys the singleton.
     * MUST be called before overwriting the database file during restore,
     * otherwise Room holds open file descriptors that prevent a clean copy.
     * After calling this, the next call to getInstance() creates a fresh instance.
     */
    public static void closeAndReset() {
        synchronized (AppDatabase.class) {
            if (INSTANCE != null) {
                if (INSTANCE.isOpen()) {
                    INSTANCE.close();
                }
                INSTANCE = null;
            }
        }
    }
}
