package com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */
public interface TypeAdapterInfo {
    TypeElement getAdapterElement();
    TypeMirror getFromType();
    TypeMirror getToType();
}
