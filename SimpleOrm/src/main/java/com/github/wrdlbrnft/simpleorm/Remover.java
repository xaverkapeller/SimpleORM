package com.github.wrdlbrnft.simpleorm;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public interface Remover<T> {

    interface Callback<T> {
        void onFinished();
    }

    void now();
    Remover<T> onFinished(Callback<T> callback);
}
