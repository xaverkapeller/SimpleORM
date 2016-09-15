package com.github.wrdlbrnft.simpleorm.database;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public interface RowReader {
    float getFloat(int index);
    double getDouble(int index);
    int getInt(int index);
    long getLong(int index);
    short getShort(int index);
    boolean getBoolean(int index);
    Float getFloatOrNull(int index);
    Double getDoubleOrNull(int index);
    Integer getIntOrNull(int index);
    Long getLongOrNull(int index);
    Short getShortOrNull(int index);
    Boolean getBooleanOrNull(int index);
    byte[] getBlob(int index);
    String getString(int index);
}
