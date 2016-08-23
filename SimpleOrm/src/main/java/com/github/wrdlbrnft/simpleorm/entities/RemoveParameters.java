package com.github.wrdlbrnft.simpleorm.entities;

import com.github.wrdlbrnft.simpleorm.selection.Selection;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public interface RemoveParameters<T> {
    List<T> getEntitiesToRemove();
    Selection getSelection();
}
