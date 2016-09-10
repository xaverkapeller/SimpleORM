package com.github.wrdlbrnft.simpleorm.processor.builder.entity;

import com.github.wrdlbrnft.codebuilder.executables.Method;
import com.github.wrdlbrnft.codebuilder.variables.Field;

import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 07/09/16
 */

class FieldInfo {

    private final Field mField;
    private final TypeMirror mBaseType;
    private final Method mGetter;

    FieldInfo(Field field, TypeMirror baseType, Method getter) {
        mField = field;
        mBaseType = baseType;
        mGetter = getter;
    }

    public Field getField() {
        return mField;
    }

    public Method getGetter() {
        return mGetter;
    }

    public TypeMirror getBaseType() {
        return mBaseType;
    }
}
