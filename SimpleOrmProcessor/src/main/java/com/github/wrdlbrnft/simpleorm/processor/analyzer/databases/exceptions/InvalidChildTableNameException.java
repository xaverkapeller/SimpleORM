package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions;

import javax.lang.model.element.Element;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 18/07/16
 */

public class InvalidChildTableNameException extends InvalidDatabaseException {

    public InvalidChildTableNameException(String message, Element element) {
        super(message, element);
    }

    public InvalidChildTableNameException(String message, Throwable cause, Element element) {
        super(message, cause, element);
    }
}
