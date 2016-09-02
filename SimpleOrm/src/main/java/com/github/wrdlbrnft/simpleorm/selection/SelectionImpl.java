package com.github.wrdlbrnft.simpleorm.selection;

import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
class SelectionImpl implements Selection {

    private final List<SelectionElement> mStatements;
    private final String[] mSelectionArgs;

    SelectionImpl(List<SelectionElement> statements, String[] selectionArgs) {
        mStatements = statements;
        mSelectionArgs = selectionArgs;
    }

    @Override
    public String getSelection(String tableName) {
        final StringBuilder builder = new StringBuilder();
        boolean appendSeparator = false;
        for (SelectionElement element : mStatements) {

            if (appendSeparator) {
                builder.append(" ");
            } else {
                appendSeparator = true;
            }

            builder.append(element.resolve(tableName));
        }
        return builder.toString();
    }

    @Override
    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    @Override
    public boolean isEmpty() {
        return mStatements.isEmpty();
    }
}
