package com.github.wrdlbrnft.simpleorm.selection;

import com.github.wrdlbrnft.simpleorm.fields.Field;
import com.github.wrdlbrnft.simpleorm.selection.predicates.DoublePredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public class BaseDoublePredicate<T, P> extends BasePredicate<T, P, Double> implements DoublePredicate<T, P> {

    public BaseDoublePredicate(P builder, Selection.Builder selectionBuilder, Field<T, Double> field) {
        super(builder, selectionBuilder, field);
    }

    @Override
    public P isEqualTo(double value) {
        return appendStatement("=", String.valueOf(value));
    }

    @Override
    public P isGreaterThan(double value) {
        return appendStatement(">", String.valueOf(value));
    }

    @Override
    public P isLessThan(double value) {
        return appendStatement("<", String.valueOf(value));
    }

    @Override
    public P isGreaterThanOrEqualTo(double value) {
        return appendStatement(">=", String.valueOf(value));
    }

    @Override
    public P isLessThanOrEqualTo(double value) {
        return appendStatement("<=", String.valueOf(value));
    }
}
