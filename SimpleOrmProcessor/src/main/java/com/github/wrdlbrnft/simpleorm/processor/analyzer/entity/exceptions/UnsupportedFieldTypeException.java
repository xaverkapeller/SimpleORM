package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions;

import javax.lang.model.element.ExecutableElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class UnsupportedFieldTypeException extends InvalidEntityException {

    public UnsupportedFieldTypeException(String message, ExecutableElement methodElement) {
        super(message, methodElement);
    }

    public UnsupportedFieldTypeException(String message, Throwable cause, ExecutableElement methodElement) {
        super(message, cause, methodElement);
    }
}
