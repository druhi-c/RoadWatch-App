package com.roadwatch.mobile.data;

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
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ComplaintDao_Impl implements ComplaintDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ComplaintEntity> __insertionAdapterOfComplaintEntity;

  private final EntityDeletionOrUpdateAdapter<ComplaintEntity> __updateAdapterOfComplaintEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkSynced;

  public ComplaintDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfComplaintEntity = new EntityInsertionAdapter<ComplaintEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `complaints` (`id`,`imagePath`,`location`,`timestamp`,`description`,`isSynced`,`roadType`,`severity`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ComplaintEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.imagePath == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.imagePath);
        }
        if (entity.location == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.location);
        }
        statement.bindLong(4, entity.timestamp);
        if (entity.description == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.description);
        }
        final int _tmp = entity.isSynced ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.roadType == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.roadType);
        }
        if (entity.severity == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.severity);
        }
      }
    };
    this.__updateAdapterOfComplaintEntity = new EntityDeletionOrUpdateAdapter<ComplaintEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `complaints` SET `id` = ?,`imagePath` = ?,`location` = ?,`timestamp` = ?,`description` = ?,`isSynced` = ?,`roadType` = ?,`severity` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ComplaintEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.imagePath == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.imagePath);
        }
        if (entity.location == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.location);
        }
        statement.bindLong(4, entity.timestamp);
        if (entity.description == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.description);
        }
        final int _tmp = entity.isSynced ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.roadType == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.roadType);
        }
        if (entity.severity == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.severity);
        }
        statement.bindLong(9, entity.id);
      }
    };
    this.__preparedStmtOfMarkSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE complaints SET isSynced = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final ComplaintEntity complaint) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfComplaintEntity.insertAndReturnId(complaint);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final ComplaintEntity complaint) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfComplaintEntity.handle(complaint);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void markSynced(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfMarkSynced.acquire();
    int _argIndex = 1;
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
      __preparedStmtOfMarkSynced.release(_stmt);
    }
  }

  @Override
  public List<ComplaintEntity> getUnsyncedComplaints() {
    final String _sql = "SELECT * FROM complaints WHERE isSynced = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
      final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
      final int _cursorIndexOfRoadType = CursorUtil.getColumnIndexOrThrow(_cursor, "roadType");
      final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
      final List<ComplaintEntity> _result = new ArrayList<ComplaintEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ComplaintEntity _item;
        _item = new ComplaintEntity();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfImagePath)) {
          _item.imagePath = null;
        } else {
          _item.imagePath = _cursor.getString(_cursorIndexOfImagePath);
        }
        if (_cursor.isNull(_cursorIndexOfLocation)) {
          _item.location = null;
        } else {
          _item.location = _cursor.getString(_cursorIndexOfLocation);
        }
        _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
        _item.isSynced = _tmp != 0;
        if (_cursor.isNull(_cursorIndexOfRoadType)) {
          _item.roadType = null;
        } else {
          _item.roadType = _cursor.getString(_cursorIndexOfRoadType);
        }
        if (_cursor.isNull(_cursorIndexOfSeverity)) {
          _item.severity = null;
        } else {
          _item.severity = _cursor.getString(_cursorIndexOfSeverity);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<Integer> getTotalComplaintsLiveData() {
    final String _sql = "SELECT COUNT(*) FROM complaints";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"complaints"}, false, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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
  public LiveData<Integer> getUnsyncedCountLiveData() {
    final String _sql = "SELECT COUNT(*) FROM complaints WHERE isSynced = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"complaints"}, false, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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
  public LiveData<List<ComplaintEntity>> getRecentCapturesLiveData() {
    final String _sql = "SELECT * FROM complaints ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"complaints"}, false, new Callable<List<ComplaintEntity>>() {
      @Override
      @Nullable
      public List<ComplaintEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfRoadType = CursorUtil.getColumnIndexOrThrow(_cursor, "roadType");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final List<ComplaintEntity> _result = new ArrayList<ComplaintEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ComplaintEntity _item;
            _item = new ComplaintEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _item.imagePath = null;
            } else {
              _item.imagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            if (_cursor.isNull(_cursorIndexOfLocation)) {
              _item.location = null;
            } else {
              _item.location = _cursor.getString(_cursorIndexOfLocation);
            }
            _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _item.description = null;
            } else {
              _item.description = _cursor.getString(_cursorIndexOfDescription);
            }
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _item.isSynced = _tmp != 0;
            if (_cursor.isNull(_cursorIndexOfRoadType)) {
              _item.roadType = null;
            } else {
              _item.roadType = _cursor.getString(_cursorIndexOfRoadType);
            }
            if (_cursor.isNull(_cursorIndexOfSeverity)) {
              _item.severity = null;
            } else {
              _item.severity = _cursor.getString(_cursorIndexOfSeverity);
            }
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
