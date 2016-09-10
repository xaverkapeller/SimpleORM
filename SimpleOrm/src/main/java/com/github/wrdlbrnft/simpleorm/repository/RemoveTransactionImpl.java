package com.github.wrdlbrnft.simpleorm.repository;

import com.github.wrdlbrnft.simpleorm.RemoveTransaction;
import com.github.wrdlbrnft.simpleorm.Remover;
import com.github.wrdlbrnft.simpleorm.entities.RemoveParameters;
import com.github.wrdlbrnft.simpleorm.fields.BooleanField;
import com.github.wrdlbrnft.simpleorm.fields.DateField;
import com.github.wrdlbrnft.simpleorm.fields.DoubleField;
import com.github.wrdlbrnft.simpleorm.fields.Field;
import com.github.wrdlbrnft.simpleorm.fields.FloatField;
import com.github.wrdlbrnft.simpleorm.fields.IntField;
import com.github.wrdlbrnft.simpleorm.fields.LongField;
import com.github.wrdlbrnft.simpleorm.fields.StringField;
import com.github.wrdlbrnft.simpleorm.selection.BaseBooleanPredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseDatePredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseDoublePredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseFloatPredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseIntPredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseLongPredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseStringPredicate;
import com.github.wrdlbrnft.simpleorm.selection.Selection;
import com.github.wrdlbrnft.simpleorm.selection.remove.BooleanRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.DateRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.DoubleRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.FloatRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.IntRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.LongRemoveBuilder;
import com.github.wrdlbrnft.simpleorm.selection.remove.StringRemoveBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
class RemoveTransactionImpl<T> implements RemoveTransaction<T> {

    private final List<T> mEntitiesToRemove = new ArrayList<>();
    private final Selection.Builder mSelectionBuilder = new Selection.Builder();
    private final TransactionResolver<T> mResolver;
    private boolean mRemoveAll = false;

    RemoveTransactionImpl(TransactionResolver<T> resolver) {
        mResolver = resolver;
    }

    @Override
    public RemoveTransaction<T> entity(T entity) {
        mEntitiesToRemove.add(entity);
        return this;
    }

    @Override
    public RemoveTransaction<T> entities(List<T> entities) {
        mEntitiesToRemove.addAll(entities);
        return this;
    }

    @Override
    public DateRemoveBuilder<T> where(DateField<T> field) {
        return new DateRemoveBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public StringRemoveBuilder<T> where(StringField<T> field) {
        return new StringRemoveBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public BooleanRemoveBuilder<T> where(BooleanField<T> field) {
        return new BooleanRemoveBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public DoubleRemoveBuilder<T> where(DoubleField<T> field) {
        return new DoubleRemoveBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public FloatRemoveBuilder<T> where(FloatField<T> field) {
        return new FloatRemoveBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public IntRemoveBuilder<T> where(IntField<T> field) {
        return new IntRemoveBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public LongRemoveBuilder<T> where(LongField<T> field) {
        return new LongRemoveBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public RemoveTransaction<T> and() {
        mSelectionBuilder.and();
        return this;
    }

    @Override
    public RemoveTransaction<T> all() {
        mRemoveAll = true;
        return this;
    }

    @Override
    public RemoveTransaction<T> or() {
        mSelectionBuilder.or();
        return this;
    }

    @Override
    public Remover<T> commit() {
        final RemoveParameters<T> parameters = new RemoveParametersImpl<>(
                mEntitiesToRemove,
                mRemoveAll ? Selection.Builder.all() : mSelectionBuilder.build()
        );
        return mResolver.commit(parameters);
    }

    private static class BooleanRemoveBuilderImpl<T> extends BaseBooleanPredicate<T, RemoveTransaction<T>> implements BooleanRemoveBuilder<T> {

        BooleanRemoveBuilderImpl(RemoveTransaction<T> queryBuilder, Selection.Builder selectionBuilder, Field<T, Boolean> field) {
            super(queryBuilder, selectionBuilder, field);
        }
    }

    private static class DateRemoveBuilderImpl<T> extends BaseDatePredicate<T, RemoveTransaction<T>> implements DateRemoveBuilder<T> {

        DateRemoveBuilderImpl(RemoveTransaction<T> builder, Selection.Builder selectionBuilder, Field<T, Date> field) {
            super(builder, selectionBuilder, field);
        }
    }

    private static class DoubleRemoveBuilderImpl<T> extends BaseDoublePredicate<T, RemoveTransaction<T>> implements DoubleRemoveBuilder<T> {

        DoubleRemoveBuilderImpl(RemoveTransaction<T> builder, Selection.Builder selectionBuilder, Field<T, Double> field) {
            super(builder, selectionBuilder, field);
        }
    }

    private static class FloatRemoveBuilderImpl<T> extends BaseFloatPredicate<T, RemoveTransaction<T>> implements FloatRemoveBuilder<T> {

        FloatRemoveBuilderImpl(RemoveTransaction<T> builder, Selection.Builder selectionBuilder, Field<T, Float> field) {
            super(builder, selectionBuilder, field);
        }
    }

    private static class IntRemoveBuilderImpl<T> extends BaseIntPredicate<T, RemoveTransaction<T>> implements IntRemoveBuilder<T> {

        IntRemoveBuilderImpl(RemoveTransaction<T> builder, Selection.Builder selectionBuilder, Field<T, Integer> field) {
            super(builder, selectionBuilder, field);
        }
    }

    private static class LongRemoveBuilderImpl<T> extends BaseLongPredicate<T, RemoveTransaction<T>> implements LongRemoveBuilder<T> {

        LongRemoveBuilderImpl(RemoveTransaction<T> builder, Selection.Builder selectionBuilder, Field<T, Long> field) {
            super(builder, selectionBuilder, field);
        }
    }

    private static class StringRemoveBuilderImpl<T> extends BaseStringPredicate<T, RemoveTransaction<T>> implements StringRemoveBuilder<T> {

        StringRemoveBuilderImpl(RemoveTransaction<T> builder, Selection.Builder selectionBuilder, Field<T, String> field) {
            super(builder, selectionBuilder, field);
        }
    }
}
