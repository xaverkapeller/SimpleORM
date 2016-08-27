package com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.exceptions;

import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */

public class UnsupportedTypeException extends RuntimeException {

    private final TypeElement mTypeElement;

    public UnsupportedTypeException(String message, TypeElement typeElement) {
        super(message);
        mTypeElement = typeElement;
    }

    public UnsupportedTypeException(String message, Throwable cause, TypeElement typeElement) {
        super(message, cause);
        mTypeElement = typeElement;
    }
}
