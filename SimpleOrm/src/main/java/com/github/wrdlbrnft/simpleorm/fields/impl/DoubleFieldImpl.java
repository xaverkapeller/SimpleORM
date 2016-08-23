package com.github.wrdlbrnft.simpleorm.fields.impl;

import com.github.wrdlbrnft.simpleorm.fields.DoubleField;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class DoubleFieldImpl<T> extends FieldImpl<T, Double> implements DoubleField<T> {

    public DoubleFieldImpl(String name) {
        super(name);
    }
}
