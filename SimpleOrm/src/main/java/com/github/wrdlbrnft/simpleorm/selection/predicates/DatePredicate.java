package com.github.wrdlbrnft.simpleorm.selection.predicates;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */
public interface DatePredicate<T, P> extends Predicate<T, P, Date> {
    P isBefore(@NonNull Date date);
    P isAfter(@NonNull Date date);
    P isAfterOrEqualTo(@NonNull Date date);
    P isBeforeOrEqualTo(@NonNull Date date);
    P isEqualTo(@NonNull Date date);
}
