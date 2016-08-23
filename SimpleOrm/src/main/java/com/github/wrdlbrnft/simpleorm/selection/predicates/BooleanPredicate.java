package com.github.wrdlbrnft.simpleorm.selection.predicates;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */
public interface BooleanPredicate<T, P> extends Predicate<T, P, Boolean> {
    P isEqualTo(boolean value);
    P isTrue();
    P isFalse();
}
