package com.github.wrdlbrnft.simpleorm.selection.predicates;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface FloatPredicate<T, P> extends Predicate<T, P, Float> {
    P isEqualTo(float value);
    P isGreaterThan(float value);
    P isLessThan(float value);
    P isGreaterThanOrEqualTo(float value);
    P isLessThanOrEqualTo(float value);
}
