package com.github.wrdlbrnft.simpleorm;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 02/07/16
 */
public interface Repository<T> {
    SaveTransaction<T> save();
    RemoveTransaction<T> remove();
    QueryBuilder<T> find();
}
