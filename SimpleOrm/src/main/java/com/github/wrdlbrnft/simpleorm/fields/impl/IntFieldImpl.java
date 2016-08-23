package com.github.wrdlbrnft.simpleorm.fields.impl;

import com.github.wrdlbrnft.simpleorm.fields.IntField;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class IntFieldImpl<T> extends FieldImpl<T, Integer> implements IntField<T> {

    public IntFieldImpl(String name) {
        super(name);
    }
}
