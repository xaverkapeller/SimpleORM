package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

import java.util.List;

import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 04/07/16
 */
class EntityInfoImpl implements EntityInfo {

    private final String mTableName;
    private final TypeElement mEntityElement;
    private final ColumnInfo mIdColumn;
    private final List<ColumnInfo> mContentColumns;

    public EntityInfoImpl(String tableName, TypeElement entityElement, ColumnInfo idColumn, List<ColumnInfo> contentColumns) {
        mTableName = tableName;
        mEntityElement = entityElement;
        mIdColumn = idColumn;
        mContentColumns = contentColumns;
    }

    @Override
    public String getTableName() {
        return mTableName;
    }

    @Override
    public TypeElement getEntityElement() {
        return mEntityElement;
    }

    @Override
    public ColumnInfo getIdColumn() {
        return mIdColumn;
    }

    @Override
    public List<ColumnInfo> getColumns() {
        return mContentColumns;
    }
}
