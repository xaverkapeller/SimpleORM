package com.github.wrdlbrnft.simpleorm.selection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/07/16
 */
public interface Selection {

    String getSelection();
    String[] getSelectionArgs();

    class Builder {

        private static final int OPERATOR_NONE = 0x00;
        private static final int OPERATOR_AND = 0x01;
        private static final int OPERATOR_OR = 0x02;

        private int mOperator = OPERATOR_NONE;

        private final StringBuilder mBuilder = new StringBuilder();
        private final List<String> mArguments = new ArrayList<>();

        public Builder statement(String column, String operator, String argument) {
            appendOperator();
            mBuilder.append(column).append(" ").append(operator).append(" ?");
            mArguments.add(argument);
            return this;
        }

        public Builder isNull(String column) {
            appendOperator();
            mBuilder.append(column).append(" IS NULL");
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
                    mBuilder.toString(),
                    mArguments.toArray(new String[mArguments.size()])
            );
        }

        private void appendOperator() {
            if (mOperator == OPERATOR_NONE) {
                mOperator = OPERATOR_AND;
            } else if (mOperator == OPERATOR_AND) {
                mBuilder.append(" AND ");
            } else {
                mBuilder.append(" OR ");
            }
        }
    }
}
