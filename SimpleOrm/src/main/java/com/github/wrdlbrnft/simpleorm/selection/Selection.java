package com.github.wrdlbrnft.simpleorm.selection;

import java.util.ArrayList;
import java.util.List;

import static com.github.wrdlbrnft.simpleorm.selection.OperatorElement.OPERATOR_AND;
import static com.github.wrdlbrnft.simpleorm.selection.OperatorElement.OPERATOR_NONE;
import static com.github.wrdlbrnft.simpleorm.selection.OperatorElement.OPERATOR_OR;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public interface Selection {

    String getSelection(String tableName);
    String[] getSelectionArgs();

    boolean isEmpty();

    class Builder {

        public static Selection all() {
            return new AllSelectionImpl();
        }

        private int mOperator = OPERATOR_NONE;

        private final List<SelectionElement> mStatements = new ArrayList<>();
        private final List<String> mArguments = new ArrayList<>();

        public Builder() {
        }

        public Builder statement(String column, String operator, String argument) {
            appendOperator();
            mStatements.add(new SelectionStatement(column, operator + " ?"));
            mArguments.add(argument);
            return this;
        }

        public Builder isNull(String column) {
            appendOperator();
            mStatements.add(new SelectionStatement(column, "IS NULL"));
            return this;
        }

        public Builder and() {
            mOperator = OPERATOR_AND;
            return this;
        }

        public Builder or() {
            mOperator = OPERATOR_OR;
            return this;
        }

        public Selection build() {
            return new SelectionImpl(
                    mStatements,
                    mArguments.toArray(new String[mArguments.size()])
            );
        }

        private void appendOperator() {
            if (mOperator == OPERATOR_NONE) {
                mOperator = OPERATOR_AND;
            } else if (mOperator == OPERATOR_AND) {
                mStatements.add(new OperatorElement(mOperator));
            } else if (mOperator == OPERATOR_OR) {
                mStatements.add(new OperatorElement(mOperator));
                mOperator = OPERATOR_AND;
            }
        }
    }
}
