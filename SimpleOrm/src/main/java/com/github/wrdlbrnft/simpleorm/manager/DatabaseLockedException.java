package com.github.wrdlbrnft.simpleorm.manager;

/**
 * Created with Android Studio<br>
 * User: Xaver<br>
 * Date: 23/12/2016
 */
class DatabaseLockedException extends RuntimeException {

    public DatabaseLockedException(String message) {
        super(message);
    }

    public DatabaseLockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
