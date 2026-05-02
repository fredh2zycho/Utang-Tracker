package com.redlab.utang.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.redlab.utang.models.PaymentRecord;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class PaymentRecordDao_Impl implements PaymentRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PaymentRecord> __insertionAdapterOfPaymentRecord;

  private final EntityDeletionOrUpdateAdapter<PaymentRecord> __deletionAdapterOfPaymentRecord;

  public PaymentRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPaymentRecord = new EntityInsertionAdapter<PaymentRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `payment_records` (`id`,`debtorId`,`amount`,`paymentDate`,`photoPath`,`recordType`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final PaymentRecord entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDebtorId());
        statement.bindDouble(3, entity.getAmount());
        if (entity.getPaymentDate() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPaymentDate());
        }
        if (entity.getPhotoPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPhotoPath());
        }
        if (entity.getRecordType() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRecordType());
        }
      }
    };
    this.__deletionAdapterOfPaymentRecord = new EntityDeletionOrUpdateAdapter<PaymentRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `payment_records` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final PaymentRecord entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public long insert(final PaymentRecord record) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfPaymentRecord.insertAndReturnId(record);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final PaymentRecord record) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfPaymentRecord.handle(record);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<PaymentRecord>> getPaymentsForDebtor(final long debtorId) {
    final String _sql = "SELECT * FROM payment_records WHERE debtorId = ? ORDER BY paymentDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, debtorId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"payment_records"}, false, new Callable<List<PaymentRecord>>() {
      @Override
      @Nullable
      public List<PaymentRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDebtorId = CursorUtil.getColumnIndexOrThrow(_cursor, "debtorId");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfRecordType = CursorUtil.getColumnIndexOrThrow(_cursor, "recordType");
          final List<PaymentRecord> _result = new ArrayList<PaymentRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentRecord _item;
            _item = new PaymentRecord();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final long _tmpDebtorId;
            _tmpDebtorId = _cursor.getLong(_cursorIndexOfDebtorId);
            _item.setDebtorId(_tmpDebtorId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _item.setAmount(_tmpAmount);
            final String _tmpPaymentDate;
            if (_cursor.isNull(_cursorIndexOfPaymentDate)) {
              _tmpPaymentDate = null;
            } else {
              _tmpPaymentDate = _cursor.getString(_cursorIndexOfPaymentDate);
            }
            _item.setPaymentDate(_tmpPaymentDate);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            _item.setPhotoPath(_tmpPhotoPath);
            final String _tmpRecordType;
            if (_cursor.isNull(_cursorIndexOfRecordType)) {
              _tmpRecordType = null;
            } else {
              _tmpRecordType = _cursor.getString(_cursorIndexOfRecordType);
            }
            _item.setRecordType(_tmpRecordType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public List<PaymentRecord> getPaymentsForDebtorSync(final long debtorId) {
    final String _sql = "SELECT * FROM payment_records WHERE debtorId = ? ORDER BY paymentDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, debtorId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDebtorId = CursorUtil.getColumnIndexOrThrow(_cursor, "debtorId");
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
      final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
      final int _cursorIndexOfRecordType = CursorUtil.getColumnIndexOrThrow(_cursor, "recordType");
      final List<PaymentRecord> _result = new ArrayList<PaymentRecord>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final PaymentRecord _item;
        _item = new PaymentRecord();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _item.setId(_tmpId);
        final long _tmpDebtorId;
        _tmpDebtorId = _cursor.getLong(_cursorIndexOfDebtorId);
        _item.setDebtorId(_tmpDebtorId);
        final double _tmpAmount;
        _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
        _item.setAmount(_tmpAmount);
        final String _tmpPaymentDate;
        if (_cursor.isNull(_cursorIndexOfPaymentDate)) {
          _tmpPaymentDate = null;
        } else {
          _tmpPaymentDate = _cursor.getString(_cursorIndexOfPaymentDate);
        }
        _item.setPaymentDate(_tmpPaymentDate);
        final String _tmpPhotoPath;
        if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
          _tmpPhotoPath = null;
        } else {
          _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
        }
        _item.setPhotoPath(_tmpPhotoPath);
        final String _tmpRecordType;
        if (_cursor.isNull(_cursorIndexOfRecordType)) {
          _tmpRecordType = null;
        } else {
          _tmpRecordType = _cursor.getString(_cursorIndexOfRecordType);
        }
        _item.setRecordType(_tmpRecordType);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<PaymentRecord> getAllPaymentsSync() {
    final String _sql = "SELECT * FROM payment_records ORDER BY paymentDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDebtorId = CursorUtil.getColumnIndexOrThrow(_cursor, "debtorId");
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentDate");
      final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
      final int _cursorIndexOfRecordType = CursorUtil.getColumnIndexOrThrow(_cursor, "recordType");
      final List<PaymentRecord> _result = new ArrayList<PaymentRecord>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final PaymentRecord _item;
        _item = new PaymentRecord();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _item.setId(_tmpId);
        final long _tmpDebtorId;
        _tmpDebtorId = _cursor.getLong(_cursorIndexOfDebtorId);
        _item.setDebtorId(_tmpDebtorId);
        final double _tmpAmount;
        _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
        _item.setAmount(_tmpAmount);
        final String _tmpPaymentDate;
        if (_cursor.isNull(_cursorIndexOfPaymentDate)) {
          _tmpPaymentDate = null;
        } else {
          _tmpPaymentDate = _cursor.getString(_cursorIndexOfPaymentDate);
        }
        _item.setPaymentDate(_tmpPaymentDate);
        final String _tmpPhotoPath;
        if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
          _tmpPhotoPath = null;
        } else {
          _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
        }
        _item.setPhotoPath(_tmpPhotoPath);
        final String _tmpRecordType;
        if (_cursor.isNull(_cursorIndexOfRecordType)) {
          _tmpRecordType = null;
        } else {
          _tmpRecordType = _cursor.getString(_cursorIndexOfRecordType);
        }
        _item.setRecordType(_tmpRecordType);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
