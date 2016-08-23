package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions;

import javax.lang.model.element.ExecutableElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 17/07/16
 */

public class InvalidChangePasswordMethod extends InvalidDatabaseException {

    public InvalidChangePasswordMethod(String message, ExecutableElement method) {
        super(message, method);
    }

    public InvalidChangePasswordMethod(String message, Throwable cause, ExecutableElement method) {
        super(message, cause, method);
    }
}
