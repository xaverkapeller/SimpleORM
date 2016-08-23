package com.github.wrdlbrnft.simpleorm.repository;

import com.github.wrdlbrnft.simpleorm.entities.SaveParameters;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */

class SaveParameterImpl<T> implements SaveParameters<T> {

    private final List<T> mEntitiesToSave;

    SaveParameterImpl(List<T> entitiesToSave) {
        mEntitiesToSave = entitiesToSave;
    }

    @Override
    public List<T> getEntitiesToSave() {
        return mEntitiesToSave;
    }
}
