package com.github.wrdlbrnft.simpleorm.entities;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 24/08/16
 */

public abstract class BaseEntityIterator<T> implements EntityIterator<T> {

    private final T[] mCache;
    private final int mSize;
    private int mIndex = 0;

    @SuppressWarnings("unchecked")
    protected BaseEntityIterator(Class<T> clazz, int size) {
        mSize = size;
        mCache = (T[]) Array.newInstance(clazz, size);
    }

    @Override
    public final List<T> asList() {
        return new LazyEntityList();
    }


    @Override
    public final int size() {
        return mSize;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public final boolean hasNext() {
        return mIndex < mSize;
    }

    @Override
    public final T next() {
        if (mIndex < mSize) {
            return read(mIndex++);
        }
        throw new NoSuchElementException();
    }

    private T read(int position) {
        if (mCache[position] == null) {
            mCache[position] = readFromPosition(position);
        }
        return mCache[position];
    }

    protected abstract T readFromPosition(int position);

    private class LazyEntityList extends AbstractList<T> {

        @Override
        public T get(int position) {
            if (position < 0 || position >= mSize) {
                throw new IndexOutOfBoundsException("Index: " + position + ", Size: " + mSize);
            }
            return read(position);
        }

        @Override
        public int size() {
            return mSize;
        }
    }
}
