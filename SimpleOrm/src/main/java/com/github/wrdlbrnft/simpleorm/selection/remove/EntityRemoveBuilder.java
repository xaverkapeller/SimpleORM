package com.github.wrdlbrnft.simpleorm.selection.remove;

import com.github.wrdlbrnft.simpleorm.RemoveTransaction;
import com.github.wrdlbrnft.simpleorm.selection.predicates.EntityPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface EntityRemoveBuilder<T, R> extends EntityPredicate<T, RemoveTransaction<T>, R> {
}
