package com.github.wrdlbrnft.simpleorm.repository;

import android.os.Handler;
import android.os.Looper;

import com.github.wrdlbrnft.simpleorm.entities.EntityManager;
import com.github.wrdlbrnft.simpleorm.Loader;
import com.github.wrdlbrnft.simpleorm.QueryBuilder;
import com.github.wrdlbrnft.simpleorm.entities.QueryParameters;
import com.github.wrdlbrnft.simpleorm.entities.RemoveParameters;
import com.github.wrdlbrnft.simpleorm.RemoveTransaction;
import com.github.wrdlbrnft.simpleorm.Remover;
import com.github.wrdlbrnft.simpleorm.Repository;
import com.github.wrdlbrnft.simpleorm.entities.SaveParameters;
import com.github.wrdlbrnft.simpleorm.SaveTransaction;
import com.github.wrdlbrnft.simpleorm.Saver;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public class BaseRepository<T> implements Repository<T> {

    static Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());

    private final TransactionResolver<T> mTransactionResolver = new TransactionResolver<T>() {

        @Override
        public Saver<T> commit(SaveParameters<T> parameters) {
            final Callable<Void> callable = new SaveTransactionCallable<>(mEntityManager, parameters);
            final SaverTask<T> task = new SaverTask<>(callable);
            mExecutor.execute(task);
            return task;
        }

        @Override
        public Remover<T> commit(RemoveParameters<T> parameters) {
            final Callable<Void> callable = new RemoveTransactionCallable<>(mEntityManager, parameters);
            final RemoverTask<T> task = new RemoverTask<>(callable);
            mExecutor.execute(task);
            return task;
        }
    };

    private final QueryResolver<T> mQueryResolver = new QueryResolver<T>() {

        @Override
        public Loader<T> queryFirst(QueryParameters parameters) {
            final Callable<T> callable = new QueryFirstCallable<>(mEntityManager, parameters);
            final LoaderTask<T> task = new LoaderTask<>(callable);
            mExecutor.execute(task);
            return task;
        }

        @Override
        public Loader<List<T>> queryList(QueryParameters parameters) {
            final Callable<List<T>> callable = new QueryListCallable<>(mEntityManager, parameters);
            final LoaderTask<List<T>> task = new LoaderTask<>(callable);
            mExecutor.execute(task);
            return task;
        }

        @Override
        public List<T> queryLazy(QueryParameters parameters) {
            return mEntityManager.queryLazy(parameters);
        }
    };

    private final EntityManager<T> mEntityManager;
    private final Executor mExecutor;

    public BaseRepository(Executor executor, EntityManager<T> entityManager) {
        mEntityManager = entityManager;
        mExecutor = executor;
    }

    @Override
    public SaveTransaction<T> save() {
        return new SaveTransactionImpl<>(mTransactionResolver);
    }

    @Override
    public RemoveTransaction<T> remove() {
        return new RemoveTransactionImpl<>(mTransactionResolver);
    }

    @Override
    public QueryBuilder<T> find() {
        return new QueryBuilderImpl<>(mQueryResolver);
    }

    private static class QueryFirstCallable<T> implements Callable<T> {

        private final EntityManager<T> mEntityManager;
        private final QueryParameters mParameters;

        private QueryFirstCallable(EntityManager<T> entityManager, QueryParameters parameters) {
            mEntityManager = entityManager;
            mParameters = parameters;
        }

        @Override
        public T call() throws Exception {
            return mEntityManager.queryFirst(mParameters);
        }
    }

    private static class QueryListCallable<T> implements Callable<List<T>> {

        private final EntityManager<T> mEntityManager;
        private final QueryParameters mParameters;

        private QueryListCallable(EntityManager<T> entityManager, QueryParameters parameters) {
            mEntityManager = entityManager;
            mParameters = parameters;
        }

        @Override
        public List<T> call() throws Exception {
            return mEntityManager.queryList(mParameters);
        }
    }

    private static class SaveTransactionCallable<T> implements Callable<Void> {

        private final EntityManager<T> mEntityManager;
        private final SaveParameters<T> mParameters;

        private SaveTransactionCallable(EntityManager<T> entityManager, SaveParameters<T> parameters) {
            mEntityManager = entityManager;
            mParameters = parameters;
        }

        @Override
        public Void call() throws Exception {
            mEntityManager.commitSaveTransaction(mParameters);
            return null;
        }
    }

    private static class RemoveTransactionCallable<T> implements Callable<Void> {

        private final EntityManager<T> mEntityManager;
        private final RemoveParameters<T> mParameters;

        private RemoveTransactionCallable(EntityManager<T> entityManager, RemoveParameters<T> parameters) {
            mEntityManager = entityManager;
            mParameters = parameters;
        }

        @Override
        public Void call() throws Exception {
            mEntityManager.commitRemoveTransaction(mParameters);
            return null;
        }
    }
}
