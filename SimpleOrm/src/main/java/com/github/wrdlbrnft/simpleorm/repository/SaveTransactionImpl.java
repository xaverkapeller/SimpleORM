package com.github.wrdlbrnft.simpleorm.repository;

import com.github.wrdlbrnft.simpleorm.Saver;
import com.github.wrdlbrnft.simpleorm.SaveTransaction;
import com.github.wrdlbrnft.simpleorm.entities.SaveParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */

class SaveTransactionImpl<T> implements SaveTransaction<T> {

    private final List<T> mEntitiesToSave = new ArrayList<>();

    private final TransactionResolver<T> mResolver;

    SaveTransactionImpl(TransactionResolver<T> resolver) {
        mResolver = resolver;
    }

    @Override
    public SaveTransaction<T> entity(T entity) {
        mEntitiesToSave.add(entity);
        return this;
    }

    @Override
    public SaveTransaction<T> entities(List<T> entity) {
        mEntitiesToSave.addAll(entity);
        return this;
    }

    private SaveParameters<T> buildTransactionParameters() {
        return new SaveParameterImpl<>(mEntitiesToSave);
    }

    @Override
    public Saver<T> commit() {
        final SaveParameters<T> parameters = buildTransactionParameters();
        return mResolver.commit(parameters);
    }
}
