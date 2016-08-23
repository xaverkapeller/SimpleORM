package com.github.wrdlbrnft.simpleorm.selection;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
class SelectionImpl implements Selection {

    private final String mSelection;
    private final String[] mSelectionArgs;

    SelectionImpl(String selection, String[] selectionArgs) {
        mSelection = selection;
        mSelectionArgs = selectionArgs;
    }

    @Override
    public String getSelection() {
        return mSelection;
    }

    @Override
    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }
}
