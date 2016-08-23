package com.github.wrdlbrnft.simpleorm.processor.analyzer.databases.exceptions;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.exceptions.AnalyzerException;

import javax.lang.model.element.Element;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public class InvalidDatabaseException extends AnalyzerException {

    public InvalidDatabaseException(String message, Element element) {
        super(message, element);
    }

    public InvalidDatabaseException(String message, Throwable cause, Element element) {
        super(message, cause, element);
    }
}
