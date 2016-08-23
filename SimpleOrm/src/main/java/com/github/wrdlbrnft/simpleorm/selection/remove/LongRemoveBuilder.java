package com.github.wrdlbrnft.simpleorm.selection.remove;

import com.github.wrdlbrnft.simpleorm.RemoveTransaction;
import com.github.wrdlbrnft.simpleorm.selection.predicates.LongPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface LongRemoveBuilder<T> extends LongPredicate<T, RemoveTransaction<T>> {
}
