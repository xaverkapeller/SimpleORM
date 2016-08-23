package com.github.wrdlbrnft.simpleorm.selection;

import com.github.wrdlbrnft.simpleorm.fields.Field;
import com.github.wrdlbrnft.simpleorm.selection.predicates.BooleanPredicate;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public class BaseBooleanPredicate<T, P> extends BasePredicate<T, P, Boolean> implements BooleanPredicate<T, P> {

    public BaseBooleanPredicate(P queryBuilder, Selection.Builder selectionBuilder, Field<T, Boolean> field) {
        super(queryBuilder, selectionBuilder, field);
    }

    @Override
    public P isEqualTo(boolean value) {
        return appendStatement("=", value ? "1" : "0");
    }

    @Override
    public P isTrue() {
        return isEqualTo(true);
    }

    @Override
    public P isFalse() {
        return isEqualTo(false);
    }
}
