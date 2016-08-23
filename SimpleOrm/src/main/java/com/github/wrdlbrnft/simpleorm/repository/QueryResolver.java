package com.github.wrdlbrnft.simpleorm.repository;

import com.github.wrdlbrnft.simpleorm.entities.EntityIterator;
import com.github.wrdlbrnft.simpleorm.Loader;
import com.github.wrdlbrnft.simpleorm.entities.QueryParameters;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
interface QueryResolver<T> {
    Loader<T> queryFirst(QueryParameters parameters);
    Loader<List<T>> queryList(QueryParameters parameters);
    EntityIterator<T> queryLazy(QueryParameters parameters);
}
