package com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.exceptions.UnsupportedTypeException;

import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */

public interface TypeAdapterManager {
    TypeAdapterResult resolve(TypeMirror type) throws UnsupportedTypeException;
}
