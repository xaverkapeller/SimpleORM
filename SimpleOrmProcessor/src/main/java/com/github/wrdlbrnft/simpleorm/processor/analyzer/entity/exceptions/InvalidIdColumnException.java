package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions;

import javax.lang.model.element.Element;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public class InvalidIdColumnException extends InvalidEntityException {

    public InvalidIdColumnException(String message, Element element) {
        super(message, element);
    }

    public InvalidIdColumnException(String message, Throwable cause, Element element) {
        super(message, cause, element);
    }
}
