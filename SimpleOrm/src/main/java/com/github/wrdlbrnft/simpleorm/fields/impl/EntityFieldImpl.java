package com.github.wrdlbrnft.simpleorm.fields.impl;

import com.github.wrdlbrnft.simpleorm.fields.EntityField;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class EntityFieldImpl<T, R> extends FieldImpl<T, R> implements EntityField<T, R> {

    public EntityFieldImpl(String name) {
        super(name);
    }
}
