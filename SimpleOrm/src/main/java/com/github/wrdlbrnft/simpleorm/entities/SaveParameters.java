package com.github.wrdlbrnft.simpleorm.entities;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public interface SaveParameters<T> {
    List<T> getEntitiesToSave();
}
