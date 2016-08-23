package com.github.wrdlbrnft.simpleorm.fields.impl;

import com.github.wrdlbrnft.simpleorm.fields.StringField;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class StringFieldImpl<T> extends FieldImpl<T, String> implements StringField<T> {

    public StringFieldImpl(String name) {
        super(name);
    }
}
