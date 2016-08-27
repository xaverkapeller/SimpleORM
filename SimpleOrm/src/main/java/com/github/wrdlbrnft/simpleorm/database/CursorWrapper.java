package com.github.wrdlbrnft.simpleorm.database;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public interface CursorWrapper extends RowReader, ColumnIndexer {
    boolean moveToFirst();
    boolean moveToNext();
    boolean moveToPosition(int position);
    void close();
    int getCount();
}
