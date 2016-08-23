package com.github.wrdlbrnft.simpleorm.entities;

import com.github.wrdlbrnft.simpleorm.selection.Selection;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public interface QueryParameters {
    Selection getSelection();
    String getLimit();
    String getOrderBy();
}
