package com.redlab.utang.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile DebtorDao _debtorDao;

  private volatile PaymentRecordDao _paymentRecordDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `debtors` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `totalAmount` REAL NOT NULL, `amountPaid` REAL NOT NULL, `interestRate` REAL NOT NULL, `penaltyAmount` REAL NOT NULL, `startDate` TEXT, `fullyPaidDate` TEXT, `notes` TEXT, `isFullyPaid` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `payment_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `debtorId` INTEGER NOT NULL, `amount` REAL NOT NULL, `paymentDate` TEXT, `photoPath` TEXT, `recordType` TEXT, FOREIGN KEY(`debtorId`) REFERENCES `debtors`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_payment_records_debtorId` ON `payment_records` (`debtorId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f78cc7ebd6276c0b621c78f77e07af8d')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `debtors`");
        db.execSQL("DROP TABLE IF EXISTS `payment_records`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDebtors = new HashMap<String, TableInfo.Column>(10);
        _columnsDebtors.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtors.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtors.put("totalAmount", new TableInfo.Column("totalAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtors.put("amountPaid", new TableInfo.Column("amountPaid", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtors.put("interestRate", new TableInfo.Column("interestRate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtors.put("penaltyAmount", new TableInfo.Column("penaltyAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtors.put("startDate", new TableInfo.Column("startDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtors.put("fullyPaidDate", new TableInfo.Column("fullyPaidDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtors.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtors.put("isFullyPaid", new TableInfo.Column("isFullyPaid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDebtors = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDebtors = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDebtors = new TableInfo("debtors", _columnsDebtors, _foreignKeysDebtors, _indicesDebtors);
        final TableInfo _existingDebtors = TableInfo.read(db, "debtors");
        if (!_infoDebtors.equals(_existingDebtors)) {
          return new RoomOpenHelper.ValidationResult(false, "debtors(com.redlab.utang.models.Debtor).\n"
                  + " Expected:\n" + _infoDebtors + "\n"
                  + " Found:\n" + _existingDebtors);
        }
        final HashMap<String, TableInfo.Column> _columnsPaymentRecords = new HashMap<String, TableInfo.Column>(6);
        _columnsPaymentRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRecords.put("debtorId", new TableInfo.Column("debtorId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRecords.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRecords.put("paymentDate", new TableInfo.Column("paymentDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRecords.put("photoPath", new TableInfo.Column("photoPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPaymentRecords.put("recordType", new TableInfo.Column("recordType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPaymentRecords = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPaymentRecords.add(new TableInfo.ForeignKey("debtors", "CASCADE", "NO ACTION", Arrays.asList("debtorId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPaymentRecords = new HashSet<TableInfo.Index>(1);
        _indicesPaymentRecords.add(new TableInfo.Index("index_payment_records_debtorId", false, Arrays.asList("debtorId"), Arrays.asList("ASC")));
        final TableInfo _infoPaymentRecords = new TableInfo("payment_records", _columnsPaymentRecords, _foreignKeysPaymentRecords, _indicesPaymentRecords);
        final TableInfo _existingPaymentRecords = TableInfo.read(db, "payment_records");
        if (!_infoPaymentRecords.equals(_existingPaymentRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "payment_records(com.redlab.utang.models.PaymentRecord).\n"
                  + " Expected:\n" + _infoPaymentRecords + "\n"
                  + " Found:\n" + _existingPaymentRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f78cc7ebd6276c0b621c78f77e07af8d", "6f211796617aaff73f95b57f64bb3da7");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "debtors","payment_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `debtors`");
      _db.execSQL("DELETE FROM `payment_records`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DebtorDao.class, DebtorDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PaymentRecordDao.class, PaymentRecordDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DebtorDao debtorDao() {
    if (_debtorDao != null) {
      return _debtorDao;
    } else {
      synchronized(this) {
        if(_debtorDao == null) {
          _debtorDao = new DebtorDao_Impl(this);
        }
        return _debtorDao;
      }
    }
  }

  @Override
  public PaymentRecordDao paymentRecordDao() {
    if (_paymentRecordDao != null) {
      return _paymentRecordDao;
    } else {
      synchronized(this) {
        if(_paymentRecordDao == null) {
          _paymentRecordDao = new PaymentRecordDao_Impl(this);
        }
        return _paymentRecordDao;
      }
    }
  }
}
