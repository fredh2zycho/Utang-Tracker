package com.redlab.utang.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.redlab.utang.models.Debtor;
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
public final class DebtorDao_Impl implements DebtorDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Debtor> __insertionAdapterOfDebtor;

  private final EntityDeletionOrUpdateAdapter<Debtor> __deletionAdapterOfDebtor;

  private final EntityDeletionOrUpdateAdapter<Debtor> __updateAdapterOfDebtor;

  private final SharedSQLiteStatement __preparedStmtOfAddPayment;

  private final SharedSQLiteStatement __preparedStmtOfAddPenalty;

  public DebtorDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDebtor = new EntityInsertionAdapter<Debtor>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `debtors` (`id`,`name`,`totalAmount`,`amountPaid`,`interestRate`,`penaltyAmount`,`startDate`,`fullyPaidDate`,`notes`,`isFullyPaid`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Debtor entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        statement.bindDouble(3, entity.getTotalAmount());
        statement.bindDouble(4, entity.getAmountPaid());
        statement.bindDouble(5, entity.getInterestRate());
        statement.bindDouble(6, entity.getPenaltyAmount());
        if (entity.getStartDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getStartDate());
        }
        if (entity.getFullyPaidDate() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getFullyPaidDate());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
        final int _tmp = entity.isFullyPaid() ? 1 : 0;
        statement.bindLong(10, _tmp);
      }
    };
    this.__deletionAdapterOfDebtor = new EntityDeletionOrUpdateAdapter<Debtor>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `debtors` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Debtor entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfDebtor = new EntityDeletionOrUpdateAdapter<Debtor>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `debtors` SET `id` = ?,`name` = ?,`totalAmount` = ?,`amountPaid` = ?,`interestRate` = ?,`penaltyAmount` = ?,`startDate` = ?,`fullyPaidDate` = ?,`notes` = ?,`isFullyPaid` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Debtor entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        statement.bindDouble(3, entity.getTotalAmount());
        statement.bindDouble(4, entity.getAmountPaid());
        statement.bindDouble(5, entity.getInterestRate());
        statement.bindDouble(6, entity.getPenaltyAmount());
        if (entity.getStartDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getStartDate());
        }
        if (entity.getFullyPaidDate() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getFullyPaidDate());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
        final int _tmp = entity.isFullyPaid() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfAddPayment = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE debtors SET amountPaid = amountPaid + ?, isFullyPaid = CASE WHEN (amountPaid + ?) >= (totalAmount + penaltyAmount) THEN 1 ELSE 0 END, fullyPaidDate = CASE WHEN (amountPaid + ?) >= (totalAmount + penaltyAmount) THEN ? ELSE fullyPaidDate END WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfAddPenalty = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE debtors SET penaltyAmount = penaltyAmount + ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final Debtor debtor) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfDebtor.insertAndReturnId(debtor);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Debtor debtor) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfDebtor.handle(debtor);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Debtor debtor) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfDebtor.handle(debtor);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void addPayment(final long id, final double amount, final String date) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfAddPayment.acquire();
    int _argIndex = 1;
    _stmt.bindDouble(_argIndex, amount);
    _argIndex = 2;
    _stmt.bindDouble(_argIndex, amount);
    _argIndex = 3;
    _stmt.bindDouble(_argIndex, amount);
    _argIndex = 4;
    if (date == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, date);
    }
    _argIndex = 5;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfAddPayment.release(_stmt);
    }
  }

  @Override
  public void addPenalty(final long id, final double penalty) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfAddPenalty.acquire();
    int _argIndex = 1;
    _stmt.bindDouble(_argIndex, penalty);
    _argIndex = 2;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfAddPenalty.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Debtor>> getAllDebtorsAlphabetical() {
    final String _sql = "SELECT * FROM debtors ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"debtors"}, false, new Callable<List<Debtor>>() {
      @Override
      @Nullable
      public List<Debtor> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
          final int _cursorIndexOfAmountPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "amountPaid");
          final int _cursorIndexOfInterestRate = CursorUtil.getColumnIndexOrThrow(_cursor, "interestRate");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penaltyAmount");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfFullyPaidDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fullyPaidDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsFullyPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "isFullyPaid");
          final List<Debtor> _result = new ArrayList<Debtor>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Debtor _item;
            _item = new Debtor();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            _item.setName(_tmpName);
            final double _tmpTotalAmount;
            _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
            _item.setTotalAmount(_tmpTotalAmount);
            final double _tmpAmountPaid;
            _tmpAmountPaid = _cursor.getDouble(_cursorIndexOfAmountPaid);
            _item.setAmountPaid(_tmpAmountPaid);
            final double _tmpInterestRate;
            _tmpInterestRate = _cursor.getDouble(_cursorIndexOfInterestRate);
            _item.setInterestRate(_tmpInterestRate);
            final double _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getDouble(_cursorIndexOfPenaltyAmount);
            _item.setPenaltyAmount(_tmpPenaltyAmount);
            final String _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            }
            _item.setStartDate(_tmpStartDate);
            final String _tmpFullyPaidDate;
            if (_cursor.isNull(_cursorIndexOfFullyPaidDate)) {
              _tmpFullyPaidDate = null;
            } else {
              _tmpFullyPaidDate = _cursor.getString(_cursorIndexOfFullyPaidDate);
            }
            _item.setFullyPaidDate(_tmpFullyPaidDate);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final boolean _tmpIsFullyPaid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFullyPaid);
            _tmpIsFullyPaid = _tmp != 0;
            _item.setFullyPaid(_tmpIsFullyPaid);
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
  public List<Debtor> getAllDebtorsSync() {
    final String _sql = "SELECT * FROM debtors ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
      final int _cursorIndexOfAmountPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "amountPaid");
      final int _cursorIndexOfInterestRate = CursorUtil.getColumnIndexOrThrow(_cursor, "interestRate");
      final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penaltyAmount");
      final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
      final int _cursorIndexOfFullyPaidDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fullyPaidDate");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final int _cursorIndexOfIsFullyPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "isFullyPaid");
      final List<Debtor> _result = new ArrayList<Debtor>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Debtor _item;
        _item = new Debtor();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        _item.setName(_tmpName);
        final double _tmpTotalAmount;
        _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
        _item.setTotalAmount(_tmpTotalAmount);
        final double _tmpAmountPaid;
        _tmpAmountPaid = _cursor.getDouble(_cursorIndexOfAmountPaid);
        _item.setAmountPaid(_tmpAmountPaid);
        final double _tmpInterestRate;
        _tmpInterestRate = _cursor.getDouble(_cursorIndexOfInterestRate);
        _item.setInterestRate(_tmpInterestRate);
        final double _tmpPenaltyAmount;
        _tmpPenaltyAmount = _cursor.getDouble(_cursorIndexOfPenaltyAmount);
        _item.setPenaltyAmount(_tmpPenaltyAmount);
        final String _tmpStartDate;
        if (_cursor.isNull(_cursorIndexOfStartDate)) {
          _tmpStartDate = null;
        } else {
          _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
        }
        _item.setStartDate(_tmpStartDate);
        final String _tmpFullyPaidDate;
        if (_cursor.isNull(_cursorIndexOfFullyPaidDate)) {
          _tmpFullyPaidDate = null;
        } else {
          _tmpFullyPaidDate = _cursor.getString(_cursorIndexOfFullyPaidDate);
        }
        _item.setFullyPaidDate(_tmpFullyPaidDate);
        final String _tmpNotes;
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _tmpNotes = null;
        } else {
          _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
        }
        _item.setNotes(_tmpNotes);
        final boolean _tmpIsFullyPaid;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsFullyPaid);
        _tmpIsFullyPaid = _tmp != 0;
        _item.setFullyPaid(_tmpIsFullyPaid);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<Debtor> getDebtorById(final long id) {
    final String _sql = "SELECT * FROM debtors WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return __db.getInvalidationTracker().createLiveData(new String[] {"debtors"}, false, new Callable<Debtor>() {
      @Override
      @Nullable
      public Debtor call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
          final int _cursorIndexOfAmountPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "amountPaid");
          final int _cursorIndexOfInterestRate = CursorUtil.getColumnIndexOrThrow(_cursor, "interestRate");
          final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penaltyAmount");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfFullyPaidDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fullyPaidDate");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsFullyPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "isFullyPaid");
          final Debtor _result;
          if (_cursor.moveToFirst()) {
            _result = new Debtor();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _result.setId(_tmpId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            _result.setName(_tmpName);
            final double _tmpTotalAmount;
            _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
            _result.setTotalAmount(_tmpTotalAmount);
            final double _tmpAmountPaid;
            _tmpAmountPaid = _cursor.getDouble(_cursorIndexOfAmountPaid);
            _result.setAmountPaid(_tmpAmountPaid);
            final double _tmpInterestRate;
            _tmpInterestRate = _cursor.getDouble(_cursorIndexOfInterestRate);
            _result.setInterestRate(_tmpInterestRate);
            final double _tmpPenaltyAmount;
            _tmpPenaltyAmount = _cursor.getDouble(_cursorIndexOfPenaltyAmount);
            _result.setPenaltyAmount(_tmpPenaltyAmount);
            final String _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            }
            _result.setStartDate(_tmpStartDate);
            final String _tmpFullyPaidDate;
            if (_cursor.isNull(_cursorIndexOfFullyPaidDate)) {
              _tmpFullyPaidDate = null;
            } else {
              _tmpFullyPaidDate = _cursor.getString(_cursorIndexOfFullyPaidDate);
            }
            _result.setFullyPaidDate(_tmpFullyPaidDate);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _result.setNotes(_tmpNotes);
            final boolean _tmpIsFullyPaid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFullyPaid);
            _tmpIsFullyPaid = _tmp != 0;
            _result.setFullyPaid(_tmpIsFullyPaid);
          } else {
            _result = null;
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
  public Debtor getDebtorByIdSync(final long id) {
    final String _sql = "SELECT * FROM debtors WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
      final int _cursorIndexOfAmountPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "amountPaid");
      final int _cursorIndexOfInterestRate = CursorUtil.getColumnIndexOrThrow(_cursor, "interestRate");
      final int _cursorIndexOfPenaltyAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "penaltyAmount");
      final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
      final int _cursorIndexOfFullyPaidDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fullyPaidDate");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final int _cursorIndexOfIsFullyPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "isFullyPaid");
      final Debtor _result;
      if (_cursor.moveToFirst()) {
        _result = new Debtor();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        _result.setName(_tmpName);
        final double _tmpTotalAmount;
        _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
        _result.setTotalAmount(_tmpTotalAmount);
        final double _tmpAmountPaid;
        _tmpAmountPaid = _cursor.getDouble(_cursorIndexOfAmountPaid);
        _result.setAmountPaid(_tmpAmountPaid);
        final double _tmpInterestRate;
        _tmpInterestRate = _cursor.getDouble(_cursorIndexOfInterestRate);
        _result.setInterestRate(_tmpInterestRate);
        final double _tmpPenaltyAmount;
        _tmpPenaltyAmount = _cursor.getDouble(_cursorIndexOfPenaltyAmount);
        _result.setPenaltyAmount(_tmpPenaltyAmount);
        final String _tmpStartDate;
        if (_cursor.isNull(_cursorIndexOfStartDate)) {
          _tmpStartDate = null;
        } else {
          _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
        }
        _result.setStartDate(_tmpStartDate);
        final String _tmpFullyPaidDate;
        if (_cursor.isNull(_cursorIndexOfFullyPaidDate)) {
          _tmpFullyPaidDate = null;
        } else {
          _tmpFullyPaidDate = _cursor.getString(_cursorIndexOfFullyPaidDate);
        }
        _result.setFullyPaidDate(_tmpFullyPaidDate);
        final String _tmpNotes;
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _tmpNotes = null;
        } else {
          _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
        }
        _result.setNotes(_tmpNotes);
        final boolean _tmpIsFullyPaid;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsFullyPaid);
        _tmpIsFullyPaid = _tmp != 0;
        _result.setFullyPaid(_tmpIsFullyPaid);
      } else {
        _result = null;
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
