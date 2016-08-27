package com.github.wrdlbrnft.simpleorm;

import com.github.wrdlbrnft.simpleorm.entities.EntityIterator;
import com.github.wrdlbrnft.simpleorm.fields.BooleanField;
import com.github.wrdlbrnft.simpleorm.fields.DateField;
import com.github.wrdlbrnft.simpleorm.fields.DoubleField;
import com.github.wrdlbrnft.simpleorm.fields.FloatField;
import com.github.wrdlbrnft.simpleorm.fields.IntField;
import com.github.wrdlbrnft.simpleorm.fields.LongField;
import com.github.wrdlbrnft.simpleorm.fields.OrderByField;
import com.github.wrdlbrnft.simpleorm.fields.StringField;
import com.github.wrdlbrnft.simpleorm.selection.query.BooleanQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.DateQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.DoubleQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.FloatQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.IntQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.LongQueryBuilder;
import com.github.wrdlbrnft.simpleorm.selection.query.StringQueryBuilder;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface QueryBuilder<T> {
    QueryBuilder<T> limit(int count);
    DateQueryBuilder<T> where(DateField<T> field);
    StringQueryBuilder<T> where(StringField<T> field);
    BooleanQueryBuilder<T> where(BooleanField<T> field);
    DoubleQueryBuilder<T> where(DoubleField<T> field);
    FloatQueryBuilder<T> where(FloatField<T> field);
    IntQueryBuilder<T> where(IntField<T> field);
    LongQueryBuilder<T> where(LongField<T> field);
    QueryBuilder<T> and();
    QueryBuilder<T> or();
    <R> QueryBuilder<T> orderBy(OrderByField<T, R> field, Ordering ordering);
    <R> QueryBuilder<T> orderBy(OrderByField<T, R> field);
    Loader<T> getFirst();
    Loader<List<T>> getList();
    List<T> lazy();
}
