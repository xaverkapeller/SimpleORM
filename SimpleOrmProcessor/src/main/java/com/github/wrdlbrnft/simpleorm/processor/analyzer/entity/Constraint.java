package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 17/07/16
 */

public enum Constraint {
    PRIMARY_KEY("PRIMARY KEY"),
    UNIQUE("UNIQUE"),
    AUTO_INCREMENT("AUTOINCREMENT");

    private final String mSqlKeyword;

    Constraint(String sqlKeyword) {
        mSqlKeyword = sqlKeyword;
    }

    public String getSqlKeyword() {
        return mSqlKeyword;
    }
}
