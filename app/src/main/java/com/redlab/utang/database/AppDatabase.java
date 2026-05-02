package com.redlab.utang.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.redlab.utang.models.Debtor;
import com.redlab.utang.models.PaymentRecord;

@Database(entities = {Debtor.class, PaymentRecord.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract DebtorDao debtorDao();
    public abstract PaymentRecordDao paymentRecordDao();

    // Migration from v1 (had fingerprintKey, biometricVerified) to v2 (interest, penalty, recordType)
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add new columns to debtors
            database.execSQL("ALTER TABLE debtors ADD COLUMN interestRate REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE debtors ADD COLUMN penaltyAmount REAL NOT NULL DEFAULT 0");
            // Drop old biometric column not possible in SQLite, but fingerprint data harmlessly stays
            // Add recordType to payment_records
            database.execSQL("ALTER TABLE payment_records ADD COLUMN recordType TEXT");
            // Set existing records as PAYMENT type
            database.execSQL("UPDATE payment_records SET recordType = 'PAYMENT'");
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
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
