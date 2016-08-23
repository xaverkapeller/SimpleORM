package com.github.wrdlbrnft.simpleorm;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public interface Saver<T> {

    interface Callback<T> {
        void onFinished();
    }

    void now();
    Saver<T> onFinished(Callback<T> callback);
}
