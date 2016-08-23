package com.github.wrdlbrnft.simpleorm.repository;

import com.github.wrdlbrnft.simpleorm.Loader;
import com.github.wrdlbrnft.simpleorm.exceptions.SimpleOrmException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
class LoaderTask<T> extends FutureTask<T> implements Loader<T> {

    private final List<Callback<T>> mCallbacks = new ArrayList<>();

    LoaderTask(Callable<T> callable) {
        super(callable);
    }

    @Override
    protected void done() {
        BaseRepository.MAIN_THREAD_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                final T result = now();
                for (Callback<T> callback : mCallbacks) {
                    callback.onResult(result);
                }
            }
        });
    }

    @Override
    public T now() {
        try {
            return get();
        } catch (InterruptedException | ExecutionException e) {
            throw new SimpleOrmException("Failed to get result", e);
        }
    }

    @Override
    public Loader<T> onResult(Callback<T> callback) {
        mCallbacks.add(callback);
        return this;
    }
}
