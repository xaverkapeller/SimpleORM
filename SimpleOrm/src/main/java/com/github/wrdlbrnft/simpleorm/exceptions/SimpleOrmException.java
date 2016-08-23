package com.github.wrdlbrnft.simpleorm.exceptions;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */

public class SimpleOrmException extends RuntimeException {

    public SimpleOrmException(String message) {
        super(message);
    }

    public SimpleOrmException(String message, Throwable cause) {
        super(message, cause);
    }
}
