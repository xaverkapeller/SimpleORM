package com.github.wrdlbrnft.simpleorm.selection.query;

import com.github.wrdlbrnft.simpleorm.QueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.predicates.BooleanPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */
public interface BooleanQueryBuilder<T> extends BooleanPredicate<T, QueryBuilder<T>> {
}
