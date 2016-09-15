package com.github.wrdlbrnft.simpleorm.database.plain;

import android.database.Cursor;

import com.github.wrdlbrnft.simpleorm.database.CursorWrapper;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
class CursorWrapperImpl implements CursorWrapper {

    private final Cursor mCursor;

    CursorWrapperImpl(Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public int getColumnIndex(String columnName) {
        return mCursor.getColumnIndex(columnName);
    }

    @Override
    public float getFloat(int index) {
        return mCursor.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return mCursor.getDouble(index);
    }

    @Override
    public int getInt(int index) {
        return mCursor.getInt(index);
    }

    @Override
    public long getLong(int index) {
        return mCursor.getLong(index);
    }

    @Override
    public short getShort(int index) {
        return mCursor.getShort(index);
    }

    @Override
    public boolean getBoolean(int index) {
        return mCursor.getInt(index) > 0;
    }

    @Override
    public Float getFloatOrNull(int index) {
        return mCursor.isNull(index) ? null : mCursor.getFloat(index);
    }

    @Override
    public Double getDoubleOrNull(int index) {
        return mCursor.isNull(index) ? null : mCursor.getDouble(index);
    }

    @Override
    public Integer getIntOrNull(int index) {
        return mCursor.isNull(index) ? null : mCursor.getInt(index);
    }

    @Override
    public Long getLongOrNull(int index) {
        return mCursor.isNull(index) ? null : mCursor.getLong(index);
    }

    @Override
    public Short getShortOrNull(int index) {
        return mCursor.isNull(index) ? null : mCursor.getShort(index);
    }

    @Override
    public Boolean getBooleanOrNull(int index) {
        return mCursor.isNull(index) ? null : mCursor.getInt(index) > 0;
    }

    @Override
    public byte[] getBlob(int index) {
        return mCursor.getBlob(index);
    }

    @Override
    public String getString(int index) {
        return mCursor.getString(index);
    }

    @Override
    public boolean moveToFirst() {
        return mCursor.moveToFirst();
    }

    @Override
    public boolean moveToNext() {
        return mCursor.moveToNext();
    }

    @Override
    public boolean moveToPosition(int position) {
        return mCursor.moveToPosition(position);
    }

    @Override
    public void close() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }
}
