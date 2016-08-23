package com.github.wrdlbrnft.simpleorm.fields.impl;

import com.github.wrdlbrnft.simpleorm.fields.LongField;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class LongFieldImpl<T> extends FieldImpl<T, Long> implements LongField<T> {

    public LongFieldImpl(String name) {
        super(name);
    }
}
