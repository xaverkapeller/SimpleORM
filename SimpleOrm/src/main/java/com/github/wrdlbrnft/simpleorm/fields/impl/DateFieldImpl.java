package com.github.wrdlbrnft.simpleorm.fields.impl;

import com.github.wrdlbrnft.simpleorm.fields.DateField;

import java.util.Date;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class DateFieldImpl<T> extends FieldImpl<T, Date> implements DateField<T> {

    public DateFieldImpl(String name) {
        super(name);
    }
}
