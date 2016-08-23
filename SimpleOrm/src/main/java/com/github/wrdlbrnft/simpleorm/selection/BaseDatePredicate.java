package com.github.wrdlbrnft.simpleorm.selection;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.simpleorm.fields.Field;
import com.github.wrdlbrnft.simpleorm.selection.predicates.DatePredicate;

import java.util.Date;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public class BaseDatePredicate<T, P> extends BasePredicate<T, P, Date> implements DatePredicate<T, P> {

    public BaseDatePredicate(P builder, Selection.Builder selectionBuilder, Field<T, Date> field) {
        super(builder, selectionBuilder, field);
    }

    @Override
    public P isBefore(@NonNull Date date) {
        return appendStatement("<", String.valueOf(date.getTime()));
    }

    @Override
    public P isAfter(@NonNull Date date) {
        return appendStatement(">", String.valueOf(date.getTime()));
    }

    @Override
    public P isAfterOrEqualTo(@NonNull Date date) {
        return appendStatement(">=", String.valueOf(date.getTime()));
    }

    @Override
    public P isBeforeOrEqualTo(@NonNull Date date) {
        return appendStatement("<=", String.valueOf(date.getTime()));
    }

    @Override
    public P isEqualTo(@NonNull Date date) {
        return appendStatement("=", String.valueOf(date.getTime()));
    }
}
