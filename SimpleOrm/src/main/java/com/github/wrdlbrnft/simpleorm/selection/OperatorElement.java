package com.github.wrdlbrnft.simpleorm.selection;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 28/08/16
 */

class OperatorElement implements SelectionElement {

    static final int OPERATOR_NONE = 0x00;
    static final int OPERATOR_AND = 0x01;
    static final int OPERATOR_OR = 0x02;

    private final int mOperator;

    OperatorElement(int operator) {
        mOperator = operator;
    }

    @Override
    public String resolve(String tableName) {
        if (mOperator == OPERATOR_NONE) {
            return "";
        }

        if (mOperator == OPERATOR_AND) {
            return "AND";
        }

        return "OR";
    }
}
