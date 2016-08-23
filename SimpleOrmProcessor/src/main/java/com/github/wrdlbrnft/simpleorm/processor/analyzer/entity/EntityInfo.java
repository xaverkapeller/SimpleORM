package com.github.wrdlbrnft.simpleorm.processor.analyzer.entity;

import java.util.List;

import javax.lang.model.element.TypeElement;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 03/07/16
 */

public interface EntityInfo {
    String getTableName();
    TypeElement getEntityElement();
    ColumnInfo getIdColumn();
    List<ColumnInfo> getColumns();
}
