package com.github.wrdlbrnft.simpleorm.database.plain;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.github.wrdlbrnft.simpleorm.database.WritableSQLiteWrapper;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
class WritableSQLiteWrapperImpl implements WritableSQLiteWrapper {

    private final SQLiteDatabase mDatabase;

    WritableSQLiteWrapperImpl(SQLiteDatabase database) {
        mDatabase = database;
    }

    @Override
    public void beginTransaction() {
        mDatabase.beginTransaction();
    }

    @Override
    public long insert(String tableName, ContentValues values) {
        return mDatabase.insert(tableName, null, values);
    }

    @Override
    public void delete(String tableName, String selection, String[] selectionArgs) {
        mDatabase.delete(tableName, selection, selectionArgs);
    }

    @Override
    public void setTransactionSuccessFul() {
        mDatabase.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        mDatabase.endTransaction();
    }

    @Override
    public void execSql(String sql, String[] bindArgs) {
        mDatabase.execSQL(sql, bindArgs);
    }
}
