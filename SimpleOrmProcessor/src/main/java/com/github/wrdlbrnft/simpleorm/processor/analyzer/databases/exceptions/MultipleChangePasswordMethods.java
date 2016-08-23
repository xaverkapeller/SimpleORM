package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions;

import javax.lang.model.element.ExecutableElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 17/07/16
 */

public class MultipleChangePasswordMethods extends InvalidDatabaseException {

    public MultipleChangePasswordMethods(String message, ExecutableElement method) {
        super(message, method);
    }

    public MultipleChangePasswordMethods(String message, Throwable cause, ExecutableElement method) {
        super(message, cause, method);
    }
}
