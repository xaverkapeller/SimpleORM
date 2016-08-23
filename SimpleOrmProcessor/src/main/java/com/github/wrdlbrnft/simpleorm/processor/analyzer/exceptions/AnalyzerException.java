package com.github.wrdlbrnft.simpleorm.processor.analyzer.exceptions;

import javax.lang.model.element.Element;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 11/07/16
 */

public class AnalyzerException extends RuntimeException {

    private final Element mElement;

    public AnalyzerException(String message, Element element) {
        super(message);
        mElement = element;
    }

    public AnalyzerException(String message, Throwable cause, Element element) {
        super(message, cause);
        mElement = element;
    }

    public Element getElement() {
        return mElement;
    }
}
