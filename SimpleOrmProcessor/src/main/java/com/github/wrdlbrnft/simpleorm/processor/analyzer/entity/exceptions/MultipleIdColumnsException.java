package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions;

import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class MultipleIdColumnsException extends InvalidEntityException {

    public MultipleIdColumnsException(String message, TypeElement entityElement) {
        super(message, entityElement);
    }

    public MultipleIdColumnsException(String message, Throwable cause, TypeElement entityElement) {
        super(message, cause, entityElement);
    }
}
