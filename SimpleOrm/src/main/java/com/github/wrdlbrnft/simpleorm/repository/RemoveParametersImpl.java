package com.github.wrdlbrnft.simpleorm.repository;

import com.github.wrdlbrnft.simpleorm.entities.RemoveParameters;
import com.github.wrdlbrnft.simpleorm.selection.Selection;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
class RemoveParametersImpl<T> implements RemoveParameters<T> {

    private final List<T> mEntitiesToRemove;
    private final Selection mSelection;

    RemoveParametersImpl(List<T> entitiesToRemove, Selection selection) {
        mEntitiesToRemove = entitiesToRemove;
        mSelection = selection;
    }

    @Override
    public List<T> getEntitiesToRemove() {
        return mEntitiesToRemove;
    }

    @Override
    public Selection getSelection() {
        return mSelection;
    }
}
