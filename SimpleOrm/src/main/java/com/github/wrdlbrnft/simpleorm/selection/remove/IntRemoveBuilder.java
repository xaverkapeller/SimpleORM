package com.github.wrdlbrnft.simpleorm.selection.remove;

import com.github.wrdlbrnft.simpleorm.RemoveTransaction;
import com.github.wrdlbrnft.simpleorm.selection.predicates.IntPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface IntRemoveBuilder<T> extends IntPredicate<T, RemoveTransaction<T>> {
}
