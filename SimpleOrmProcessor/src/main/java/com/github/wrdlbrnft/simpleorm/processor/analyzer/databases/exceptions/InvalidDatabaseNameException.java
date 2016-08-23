package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 17/07/16
 */

public class InvalidDatabaseNameException extends InvalidDatabaseException {

    public InvalidDatabaseNameException(String message, TypeElement databaseElement) {
        super(message, databaseElement);
    }

    public InvalidDatabaseNameException(String message, Throwable cause, TypeElement databaseElement) {
        super(message, cause, databaseElement);
    }
}
