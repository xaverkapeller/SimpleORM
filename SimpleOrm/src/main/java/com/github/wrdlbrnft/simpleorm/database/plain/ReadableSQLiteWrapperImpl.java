package com.github.wrdlbrnft.simpleorm.database.plain;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.wrdlbrnft.simpleorm.database.CursorWrapper;
import com.github.wrdlbrnft.simpleorm.database.ReadableSQLiteWrapper;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
class ReadableSQLiteWrapperImpl implements ReadableSQLiteWrapper {

    private final SQLiteDatabase mDatabase;

    ReadableSQLiteWrapperImpl(SQLiteDatabase database) {
        mDatabase = database;
    }

    @Override
    public CursorWrapper query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        final Cursor cursor = mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        if (cursor == null) {
            return null;
        }
        return new CursorWrapperImpl(cursor);
    }

    @Override
    public CursorWrapper query(String sql, String[] selectionArgs) {
        final Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
        if (cursor == null) {
            return null;
        }
        return new CursorWrapperImpl(cursor);
    }
}
