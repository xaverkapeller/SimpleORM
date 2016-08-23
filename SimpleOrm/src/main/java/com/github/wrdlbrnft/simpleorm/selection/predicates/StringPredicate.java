package com.github.wrdlbrnft.simpleorm.selection.predicates;

import android.support.annotation.NonNull;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */
public interface StringPredicate<T, P> extends Predicate<T, P, String> {
    P contains(@NonNull String text);
    P startsWith(@NonNull String text);
    P endsWith(@NonNull String text);
}
