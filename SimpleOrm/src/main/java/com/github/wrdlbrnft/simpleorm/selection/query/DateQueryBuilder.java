package com.github.wrdlbrnft.simpleorm.selection.query;

import com.github.wrdlbrnft.simpleorm.QueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.predicates.DatePredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */
public interface DateQueryBuilder<T> extends DatePredicate<T, QueryBuilder<T>> {
}
