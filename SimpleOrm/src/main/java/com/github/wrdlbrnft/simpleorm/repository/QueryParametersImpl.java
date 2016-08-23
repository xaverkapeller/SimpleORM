package com.github.wrdlbrnft.simpleorm.repository;

import com.github.wrdlbrnft.simpleorm.entities.QueryParameters;
import com.github.wrdlbrnft.simpleorm.selection.Selection;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
class QueryParametersImpl implements QueryParameters {

    private final Selection mSelection;
    private final String mLimit;
    private final String mOrderBy;

    QueryParametersImpl(Selection selection, String limit, String orderBy) {
        mSelection = selection;
        mLimit = limit;
        mOrderBy = orderBy;
    }

    @Override
    public Selection getSelection() {
        return mSelection;
    }

    @Override
    public String getLimit() {
        return mLimit;
    }

    @Override
    public String getOrderBy() {
        return mOrderBy;
    }
}
