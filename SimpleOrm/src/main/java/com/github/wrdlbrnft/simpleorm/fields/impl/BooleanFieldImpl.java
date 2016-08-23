package com.github.wrdlbrnft.simpleorm.fields.impl;

import com.github.wrdlbrnft.simpleorm.fields.BooleanField;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class BooleanFieldImpl<T> extends FieldImpl<T, Boolean> implements BooleanField<T> {

    public BooleanFieldImpl(String name) {
        super(name);
    }
}
