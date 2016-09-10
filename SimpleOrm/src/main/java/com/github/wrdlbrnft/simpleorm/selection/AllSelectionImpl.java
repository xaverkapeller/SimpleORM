package com.github.wrdlbrnft.simpleorm.selection;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 10/07/16
 */
class AllSelectionImpl implements Selection {

    private static final String[] EMPTY_ARGS = new String[0];

    @Override
    public String getSelection(String tableName) {
        return null;
    }

    @Override
    public String[] getSelectionArgs() {
        return EMPTY_ARGS;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
