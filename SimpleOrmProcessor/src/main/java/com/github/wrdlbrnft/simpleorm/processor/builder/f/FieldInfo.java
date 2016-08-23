package com.github.wrdlbrnft.simpleorm.processor.builder.f;

import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.ColumnInfo;
import com.github.wrdlbrnft.simpleorm.processor.analyzer.entity.EntityInfo;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 05/07/16
 */

public interface FieldInfo {
    EntityInfo getEntityInfo();
    ColumnInfo getColumnInfo();
}
