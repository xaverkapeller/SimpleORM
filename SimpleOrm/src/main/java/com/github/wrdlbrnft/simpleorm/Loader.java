package com.github.wrdlbrnft.simpleorm;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface Loader<T> {

    interface Callback<T> {
        void onResult(T result);
    }

    T now();
    Loader<T> onResult(Callback<T> callback);
}
