package com.github.wrdlbrnft.simpleorm.entities;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
public interface CursorReader<T> {
    T read();
    boolean moveToFirst();
    boolean moveToNext();
    void close();
    int getCount();
}
