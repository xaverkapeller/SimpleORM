package com.github.wrdlbrnft.simpleorm.selection;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/08/16
 */

class SelectionStatement implements SelectionElement {
    private final String mColumn;
    private final String mStatement;

    public SelectionStatement(String column, String statement) {
        mColumn = column;
        mStatement = statement;
    }

    @Override
    public String resolve(String tableName) {
        if (tableName == null) {
            return mColumn + " " + mStatement;
        }
        return tableName + "." + mColumn + " " + mStatement;
    }
}
