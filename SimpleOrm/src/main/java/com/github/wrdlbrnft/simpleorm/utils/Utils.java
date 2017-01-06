package com.github.wrdlbrnft.simpleorm.utils;

import android.support.annotation.NonNull;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 22/12/2016
 */

public class Utils {

    @NonNull
    public static <T> T requireNonNull(T value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return value;
    }

    @NonNull
    public static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }
}
