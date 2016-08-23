package com.github.wrdlbrnft.simpleorm.selection.predicates;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface IntPredicate<T, P> extends Predicate<T, P, Integer> {
    P isEqualTo(int value);
    P isGreaterThan(int value);
    P isLessThan(int value);
    P isGreaterThanOrEqualTo(int value);
    P isLessThanOrEqualTo(int value);
}
