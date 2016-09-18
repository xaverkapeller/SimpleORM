package com.github.wrdlbrnft.simpleorm.utils;

import android.support.annotation.NonNull;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 18/09/16
 */
public abstract class ConcurrentSupplier<T> {

    private volatile T mValue;

    public synchronized T get() {
        if (mValue == null) {
            mValue = create();
        }
        return mValue;
    }

    @NonNull
    protected abstract T create();
}
