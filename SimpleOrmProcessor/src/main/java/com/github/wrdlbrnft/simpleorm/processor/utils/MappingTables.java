package com.github.wrdlbrnft.simpleorm.processor.utils;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 18/07/16
 */

public class MappingTables {

    public static String getTableName(EntityInfo entity, ColumnInfo column) {
        return "_" + entity.getTableName() + column.getColumnName() + "Mapping";
    }
}
