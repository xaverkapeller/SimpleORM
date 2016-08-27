package com.github.wrdlbrnft.simpleorm.processor.analyzer.typeadapter.exceptions;

import javax.lang.model.element.Element;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 27/08/16
 */

public class TypeAdapterException extends RuntimeException {

    private final Element mElement;

    public TypeAdapterException(String message, Element element) {
        super(message);
        mElement = element;
    }

    public TypeAdapterException(String message, Throwable cause, Element element) {
        super(message, cause);
        mElement = element;
    }

    public Element getElement() {
        return mElement;
    }
}
