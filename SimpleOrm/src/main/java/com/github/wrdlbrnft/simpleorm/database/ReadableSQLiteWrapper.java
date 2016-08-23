package com.github.wrdlbrnft.simpleorm.database;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public interface ReadableSQLiteWrapper {
    CursorWrapper query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit);
    CursorWrapper query(String sql, String[] selectionArgs);
}
