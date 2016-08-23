package com.github.wrdlbrnft.simpleorm.selection;

import com.github.wrdlbrnft.simpleorm.fields.Field;
import com.github.wrdlbrnft.simpleorm.selection.predicates.LongPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public class BaseLongPredicate<T, P> extends BasePredicate<T, P, Long> implements LongPredicate<T, P> {

    protected BaseLongPredicate(P builder, Selection.Builder selectionBuilder, Field<T, Long> field) {
        super(builder, selectionBuilder, field);
    }

    @Override
    public P isEqualTo(long value) {
        return appendStatement("=", String.valueOf(value));
    }

    @Override
    public P isGreaterThan(long value) {
        return appendStatement(">", String.valueOf(value));
    }

    @Override
    public P isLessThan(long value) {
        return appendStatement("<", String.valueOf(value));
    }

    @Override
    public P isGreaterThanOrEqualTo(long value) {
        return appendStatement(">=", String.valueOf(value));
    }

    @Override
    public P isLessThanOrEqualTo(long value) {
        return appendStatement("<=", String.valueOf(value));
    }
}
