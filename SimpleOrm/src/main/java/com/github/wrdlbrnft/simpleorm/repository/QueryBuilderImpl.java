package com.github.wrdlbrnft.simpleorm.repository;

import com.github.wrdlbrnft.simpleorm.Loader;
import com.github.wrdlbrnft.simpleorm.Ordering;
import com.github.wrdlbrnft.simpleorm.QueryBuilder;
import com.github.wrdlbrnft.simpleorm.fields.BooleanField;
import com.github.wrdlbrnft.simpleorm.fields.DateField;
import com.github.wrdlbrnft.simpleorm.fields.DoubleField;
import com.github.wrdlbrnft.simpleorm.fields.Field;
import com.github.wrdlbrnft.simpleorm.fields.FloatField;
import com.github.wrdlbrnft.simpleorm.fields.IntField;
import com.github.wrdlbrnft.simpleorm.fields.LongField;
import com.github.wrdlbrnft.simpleorm.fields.OrderByField;
import com.github.wrdlbrnft.simpleorm.fields.StringField;
import com.github.wrdlbrnft.simpleorm.selection.BaseBooleanPredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseDatePredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseDoublePredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseFloatPredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseIntPredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseLongPredicate;
import com.github.wrdlbrnft.simpleorm.selection.BaseStringPredicate;
import com.github.wrdlbrnft.simpleorm.selection.Selection;
import com.github.wrdlbrnft.simpleorm.selection.query.BooleanQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.DateQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.DoubleQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.FloatQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.IntQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.LongQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.StringQueryBuilder;

import java.util.Date;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
class QueryBuilderImpl<T> implements QueryBuilder<T> {

    private final Selection.Builder mSelectionBuilder = new Selection.Builder();
    private final QueryResolver<T> mResolver;

    private String mLimit = null;
    private String mOrderBy = null;

    QueryBuilderImpl(QueryResolver<T> resolver) {
        mResolver = resolver;
    }

    @Override
    public QueryBuilder<T> limit(int count) {
        mLimit = String.valueOf(count);
        return this;
    }

    @Override
    public DateQueryBuilder<T> where(DateField<T> field) {
        return new DateQueryBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public StringQueryBuilder<T> where(StringField<T> field) {
        return new StringQueryBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public BooleanQueryBuilder<T> where(BooleanField<T> field) {
        return new BooleanQueryBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public DoubleQueryBuilder<T> where(DoubleField<T> field) {
        return new DoubleQueryBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public FloatQueryBuilder<T> where(FloatField<T> field) {
        return new FloatQueryBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public IntQueryBuilder<T> where(IntField<T> field) {
        return new IntQueryBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public LongQueryBuilder<T> where(LongField<T> field) {
        return new LongQueryBuilderImpl<>(this, mSelectionBuilder, field);
    }

    @Override
    public QueryBuilder<T> and() {
        mSelectionBuilder.and();
        return this;
    }

    @Override
    public QueryBuilder<T> or() {
        mSelectionBuilder.or();
        return this;
    }

    @Override
    public <R> QueryBuilder<T> orderBy(OrderByField<T, R> field, Ordering ordering) {
        mOrderBy = field.getName() + " " + createSqlOrdering(ordering);
        return this;
    }

    private static String createSqlOrdering(Ordering ordering) {
        if (ordering == Ordering.ASCENDING) {
            return "ASC";
        }

        return "DESC";
    }

    @Override
    public <R> QueryBuilder<T> orderBy(OrderByField<T, R> field) {
        return orderBy(field, Ordering.ASCENDING);
    }

    @Override
    public Loader<T> getFirst() {
        final QueryParametersImpl parameters = new QueryParametersImpl(mSelectionBuilder.build(), "1", mOrderBy);
        return mResolver.queryFirst(parameters);
    }

    @Override
    public Loader<List<T>> getList() {
        final QueryParametersImpl parameters = new QueryParametersImpl(mSelectionBuilder.build(), mLimit, mOrderBy);
        return mResolver.queryList(parameters);
    }

    @Override
    public List<T> lazy() {
        final QueryParametersImpl parameters = new QueryParametersImpl(mSelectionBuilder.build(), mLimit, mOrderBy);
        return mResolver.queryLazy(parameters);
    }

    private static class BooleanQueryBuilderImpl<T> extends BaseBooleanPredicate<T, QueryBuilder<T>> implements BooleanQueryBuilder<T> {

        BooleanQueryBuilderImpl(QueryBuilder<T> queryBuilder, Selection.Builder selectionBuilder, Field<T, Boolean> field) {
            super(queryBuilder, selectionBuilder, field);
        }
    }

    private static class DateQueryBuilderImpl<T> extends BaseDatePredicate<T, QueryBuilder<T>> implements DateQueryBuilder<T> {

        DateQueryBuilderImpl(QueryBuilder<T> builder, Selection.Builder selectionBuilder, Field<T, Date> field) {
            super(builder, selectionBuilder, field);
        }
    }

    private static class DoubleQueryBuilderImpl<T> extends BaseDoublePredicate<T, QueryBuilder<T>> implements DoubleQueryBuilder<T> {

        DoubleQueryBuilderImpl(QueryBuilder<T> builder, Selection.Builder selectionBuilder, Field<T, Double> field) {
            super(builder, selectionBuilder, field);
        }
    }

    private static class FloatQueryBuilderImpl<T> extends BaseFloatPredicate<T, QueryBuilder<T>> implements FloatQueryBuilder<T> {

        FloatQueryBuilderImpl(QueryBuilder<T> builder, Selection.Builder selectionBuilder, Field<T, Float> field) {
            super(builder, selectionBuilder, field);
        }
    }

    private static class IntQueryBuilderImpl<T> extends BaseIntPredicate<T, QueryBuilder<T>> implements IntQueryBuilder<T> {

        IntQueryBuilderImpl(QueryBuilder<T> builder, Selection.Builder selectionBuilder, Field<T, Integer> field) {
            super(builder, selectionBuilder, field);
        }
    }

    private static class LongQueryBuilderImpl<T> extends BaseLongPredicate<T, QueryBuilder<T>> implements LongQueryBuilder<T> {

        LongQueryBuilderImpl(QueryBuilder<T> builder, Selection.Builder selectionBuilder, Field<T, Long> field) {
            super(builder, selectionBuilder, field);
        }
    }

    private static class StringQueryBuilderImpl<T> extends BaseStringPredicate<T, QueryBuilder<T>> implements StringQueryBuilder<T> {

        StringQueryBuilderImpl(QueryBuilder<T> builder, Selection.Builder selectionBuilder, Field<T, String> field) {
            super(builder, selectionBuilder, field);
        }
    }
}
