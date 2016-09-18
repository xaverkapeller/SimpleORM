package com.github.wrdlbrnft.simpleorm.utils;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 18/09/16
 */

public class LongSet {

    public static final int DEFAULT_CAPACITY = 10;

    private long[] mArray;
    private int mSize = 0;

    public LongSet() {
        this(DEFAULT_CAPACITY);
    }

    public LongSet(int capacity) {
        mArray = new long[capacity];
    }

    public void add(long value) {
        final int index = ContainerHelpers.binarySearch(mArray, mSize, value);
        if (index < 0) {
            final int length = mArray.length;
            if (mSize >= length) {
                final long[] newArray = new long[length * 2];
                System.arraycopy(mArray, 0, newArray, 0, length);
                mArray = newArray;
            }
            mArray[mSize++] = value;
        }
    }

    public int size() {
        return mSize;
    }

    public long get(int index) {
        return mArray[index];
    }
}
