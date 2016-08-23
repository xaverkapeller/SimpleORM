package com.github.wrdlbrnft.simpleorm.selection.predicates;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface LongPredicate<T, P> extends Predicate<T, P, Long> {
    P isEqualTo(long value);
    P isGreaterThan(long value);
    P isLessThan(long value);
    P isGreaterThanOrEqualTo(long value);
    P isLessThanOrEqualTo(long value);
}
