package com.github.wrdlbrnft.simpleorm.entities;

import java.util.Iterator;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 20/08/16
 */

public interface EntityIterator<T> extends Iterator<T> {
    int size();
    void close();
}
