package com.github.wrdlbrnft.simpleorm.database;

import android.content.ContentValues;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public interface WritableSQLiteWrapper {
    void beginTransaction();
    long insert(String tableName, ContentValues contentValues);
    void delete(String tableName, String selection, String[] selectionArgs);
    void setTransactionSuccessFul();
    void endTransaction();
    void execSql(String sql, String[] bindArgs);
}
