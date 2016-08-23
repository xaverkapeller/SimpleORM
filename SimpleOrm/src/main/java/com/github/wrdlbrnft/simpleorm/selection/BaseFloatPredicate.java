package com.github.wrdlbrnft.simpleorm.selection;

import com.github.wrdlbrnft.simpleorm.fields.Field;
import com.github.wrdlbrnft.simpleorm.selection.predicates.FloatPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public class BaseFloatPredicate<T, P> extends BasePredicate<T, P, Float> implements FloatPredicate<T, P> {

    public BaseFloatPredicate(P builder, Selection.Builder selectionBuilder, Field<T, Float> field) {
        super(builder, selectionBuilder, field);
    }

    @Override
    public P isEqualTo(float value) {
        return appendStatement("=", String.valueOf(value));
    }

    @Override
    public P isGreaterThan(float value) {
        return appendStatement(">", String.valueOf(value));
    }

    @Override
    public P isLessThan(float value) {
        return appendStatement("<", String.valueOf(value));
    }

    @Override
    public P isGreaterThanOrEqualTo(float value) {
        return appendStatement(">=", String.valueOf(value));
    }

    @Override
    public P isLessThanOrEqualTo(float value) {
        return appendStatement("<=", String.valueOf(value));
    }
}
