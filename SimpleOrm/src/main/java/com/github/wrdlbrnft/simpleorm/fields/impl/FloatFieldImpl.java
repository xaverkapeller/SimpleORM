package com.github.wrdlbrnft.simpleorm.fields.impl;

import com.github.wrdlbrnft.simpleorm.fields.FloatField;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class FloatFieldImpl<T> extends FieldImpl<T, Float> implements FloatField<T> {

    public FloatFieldImpl(String name) {
        super(name);
    }
}
