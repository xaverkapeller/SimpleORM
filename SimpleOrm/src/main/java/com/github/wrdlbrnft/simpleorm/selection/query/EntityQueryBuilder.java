package com.github.wrdlbrnft.simpleorm.selection.query;

import com.github.wrdlbrnft.simpleorm.QueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.predicates.EntityPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface EntityQueryBuilder<T, R> extends EntityPredicate<T, QueryBuilder<T>, R> {
}
