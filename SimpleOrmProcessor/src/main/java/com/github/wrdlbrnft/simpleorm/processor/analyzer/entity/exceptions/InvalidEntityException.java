package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.exceptions;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.exceptions.AnalyzerException;

import javax.lang.model.element.Element;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */

public class InvalidEntityException extends AnalyzerException {
    public InvalidEntityException(String message, Element element) {
        super(message, element);
    }

    public InvalidEntityException(String message, Throwable cause, Element element) {
        super(message, cause, element);
    }
}
