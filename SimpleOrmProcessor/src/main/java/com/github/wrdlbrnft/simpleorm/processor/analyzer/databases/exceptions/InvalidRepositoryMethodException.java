package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public class InvalidRepositoryMethodException extends InvalidDatabaseException {

    public InvalidRepositoryMethodException(String message, ExecutableElement method) {
        super(message, method);
    }

    public InvalidRepositoryMethodException(String message, Throwable cause, ExecutableElement method) {
        super(message, cause, method);
    }
}
