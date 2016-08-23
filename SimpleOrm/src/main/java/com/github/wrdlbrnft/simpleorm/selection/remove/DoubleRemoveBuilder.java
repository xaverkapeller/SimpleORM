package com.github.wrdlbrnft.simpleorm.selection.remove;

import com.github.wrdlbrnft.simpleorm.RemoveTransaction;
import com.github.wrdlbrnft.simpleorm.selection.predicates.DoublePredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface DoubleRemoveBuilder<T> extends DoublePredicate<T, RemoveTransaction<T>> {
}
