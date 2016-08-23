package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions;

import javax.lang.model.element.ExecutableElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 17/07/16
 */

public class InvalidTableNameException extends InvalidDatabaseException {

    public InvalidTableNameException(String message, ExecutableElement repositoryMethod) {
        super(message, repositoryMethod);
    }

    public InvalidTableNameException(String message, Throwable cause, ExecutableElement repositoryMethod) {
        super(message, cause, repositoryMethod);
    }
}
