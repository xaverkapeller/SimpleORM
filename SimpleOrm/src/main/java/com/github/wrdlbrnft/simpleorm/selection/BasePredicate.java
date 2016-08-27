package com.github.wrdlbrnft.simpleorm.selection;

import com.github.wrdlbrnft.simpleorm.fields.Field;
import com.github.wrdlbrnft.simpleorm.selection.predicates.Predicate;

public abstract class BasePredicate<T, P, R> implements Predicate<T, P, R> {

    private final P mBuilder;
    private final Selection.Builder mSelectionBuilder;
    private final Field<T, R> mField;

    protected BasePredicate(P builder, Selection.Builder selectionBuilder, Field<T, R> field) {
        mBuilder = builder;
        mSelectionBuilder = selectionBuilder;
        mField = field;
    }

    @Override
    public P isNull() {
        mSelectionBuilder.isNull(mField.getName());
        return mBuilder;
    }

    protected P appendStatement(String operator, String argument) {
        mSelectionBuilder.statement(mField.getName(), operator, argument);
        return mBuilder;
    }
}