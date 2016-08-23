package com.github.wrdlbrnft.simpleorm.selection.remove;

import com.github.wrdlbrnft.simpleorm.QueryBuilder;
import com.github.wrdlbrnft.simpleorm.RemoveTransaction;
import com.github.wrdlbrnft.simpleorm.selection.predicates.BooleanPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */
public interface BooleanRemoveBuilder<T> extends BooleanPredicate<T, RemoveTransaction<T>> {
}
