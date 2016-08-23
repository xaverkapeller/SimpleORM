package com.github.wrdlbrnft.simpleorm.entities;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public interface EntityManager<T> {
    T queryFirst(QueryParameters parameters);
    List<T> queryList(QueryParameters parameters);
    EntityIterator<T> queryLazy(QueryParameters parameters);
    void commitSaveTransaction(SaveParameters<T> parameters);
    void commitRemoveTransaction(RemoveParameters<T> parameters);
}
