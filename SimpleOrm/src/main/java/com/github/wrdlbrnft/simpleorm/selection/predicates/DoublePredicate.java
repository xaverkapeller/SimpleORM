package com.github.wrdlbrnft.simpleorm.selection.predicates;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface DoublePredicate<T, P> extends Predicate<T, P, Double> {
    P isEqualTo(double value);
    P isGreaterThan(double value);
    P isLessThan(double value);
    P isGreaterThanOrEqualTo(double value);
    P isLessThanOrEqualTo(double value);
}
