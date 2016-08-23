package com.github.wrdlbrnft.simpleorm.selection;

import com.github.wrdlbrnft.simpleorm.fields.Field;
import com.github.wrdlbrnft.simpleorm.selection.predicates.IntPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public class BaseIntPredicate<T, P> extends BasePredicate<T, P, Integer> implements IntPredicate<T, P> {

    public BaseIntPredicate(P builder, Selection.Builder selectionBuilder, Field<T, Integer> field) {
        super(builder, selectionBuilder, field);
    }

    @Override
    public P isEqualTo(int value) {
        return appendStatement("=", String.valueOf(value));
    }

    @Override
    public P isGreaterThan(int value) {
        return appendStatement(">", String.valueOf(value));
    }

    @Override
    public P isLessThan(int value) {
        return appendStatement("<", String.valueOf(value));
    }

    @Override
    public P isGreaterThanOrEqualTo(int value) {
        return appendStatement(">=", String.valueOf(value));
    }

    @Override
    public P isLessThanOrEqualTo(int value) {
        return appendStatement("<=", String.valueOf(value));
    }
}
