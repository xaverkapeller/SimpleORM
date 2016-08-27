package com.github.wrdlbrnft.simpleorm.adapter;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */

public interface ValueConverter<I, O> {
    O convertTo(I input);
    I convertFrom(O input);
}
