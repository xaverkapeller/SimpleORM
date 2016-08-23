package com.github.wrdlbrnft.simpleorm.fields.impl;

import com.github.wrdlbrnft.simpleorm.fields.Field;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class FieldImpl<T, R> implements Field<T, R> {

    private final String mName;

    public FieldImpl(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
