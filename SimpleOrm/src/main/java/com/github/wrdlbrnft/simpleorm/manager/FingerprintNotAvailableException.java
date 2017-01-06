package com.github.wrdlbrnft.simpleorm.manager;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 23/12/2016
 */
class FingerprintNotAvailableException extends RuntimeException {

    public FingerprintNotAvailableException(String message) {
        super(message);
    }

    public FingerprintNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
