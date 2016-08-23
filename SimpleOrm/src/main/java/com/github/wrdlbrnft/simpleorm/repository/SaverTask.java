package com.github.wrdlbrnft.simpleorm.repository;

import com.github.wrdlbrnft.simpleorm.Saver;
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
class SaverTask<T> extends FutureTask<Void> implements Saver<T> {

    private final List<Callback<T>> mCallbacks = new ArrayList<>();

    SaverTask(Callable<Void> callable) {
        super(callable);
    }

    @Override
    protected void done() {
        BaseRepository.MAIN_THREAD_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                now();
                for (Callback<T> callback : mCallbacks) {
                    callback.onFinished();
                }
            }
        });
    }

    @Override
    public void now() {
        try {
            get();
        } catch (InterruptedException | ExecutionException e) {
            throw new SimpleOrmException("Failed to get result", e);
        }
    }

    @Override
    public Saver<T> onFinished(Callback<T> callback) {
        mCallbacks.add(callback);
        return this;
    }
}
