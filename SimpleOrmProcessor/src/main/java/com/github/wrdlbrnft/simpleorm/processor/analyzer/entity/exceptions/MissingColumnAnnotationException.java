package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions;

import javax.lang.model.element.ExecutableElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class MissingColumnAnnotationException extends InvalidEntityException {

    public MissingColumnAnnotationException(String message, ExecutableElement methodElement) {
        super(message, methodElement);
    }

    public MissingColumnAnnotationException(String message, Throwable cause, ExecutableElement methodElement) {
        super(message, cause, methodElement);
    }
}
