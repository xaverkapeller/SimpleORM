package com.github.wrdlbrnft.simpleorm.entities;

import com.github.wrdlbrnft.simpleorm.database.ReadableSQLiteWrapper;
import com.github.wrdlbrnft.simpleorm.database.SQLiteProvider;
import com.github.wrdlbrnft.simpleorm.database.WritableSQLiteWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */

public abstract class BaseEntityManager<T> implements EntityManager<T> {

    private final SQLiteProvider mWrapperProvider;

    public BaseEntityManager(SQLiteProvider provider) {
        mWrapperProvider = provider;
    }

    @Override
    public T queryFirst(QueryParameters parameters) {
        final ReadableSQLiteWrapper wrapper = mWrapperProvider.getReadableWrapper();
        final EntityIterator<T> iterator = performQuery(wrapper, parameters);
        try {
            return iterator.next();
        } finally {
            iterator.close();
        }
    }

    @Override
    public List<T> queryList(QueryParameters parameters) {
        final ReadableSQLiteWrapper wrapper = mWrapperProvider.getReadableWrapper();
        final EntityIterator<T> iterator = performQuery(wrapper, parameters);

        try {
            final List<T> entities = new ArrayList<>();
            while (iterator.hasNext()) {
                entities.add(iterator.next());
            }
            return entities;
        } finally {
            iterator.close();
        }
    }

    @Override
    public EntityIterator<T> queryLazy(QueryParameters parameters) {
        final ReadableSQLiteWrapper wrapper = mWrapperProvider.getReadableWrapper();
        return performQuery(wrapper, parameters);
    }

    @Override
    public void commitSaveTransaction(SaveParameters<T> parameters) {
        final WritableSQLiteWrapper wrapper = mWrapperProvider.getWritableWrapper();
        try {
            wrapper.beginTransaction();
            performSave(wrapper, parameters);
            wrapper.setTransactionSuccessFul();
        } finally {
            wrapper.endTransaction();
        }
    }

    @Override
    public void commitRemoveTransaction(RemoveParameters<T> parameters) {
        final WritableSQLiteWrapper wrapper = mWrapperProvider.getWritableWrapper();
        try {
            wrapper.beginTransaction();
            performRemove(wrapper, parameters);
            wrapper.setTransactionSuccessFul();
        } finally {
            wrapper.endTransaction();
        }
    }

    protected abstract void performSave(WritableSQLiteWrapper wrapper, SaveParameters<T> parameters);
    protected abstract void performRemove(WritableSQLiteWrapper wrapper, RemoveParameters<T> parameters);
    protected abstract EntityIterator<T> performQuery(ReadableSQLiteWrapper wrapper, QueryParameters parameters);
}
